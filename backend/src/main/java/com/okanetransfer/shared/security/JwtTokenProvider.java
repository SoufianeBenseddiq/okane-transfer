package com.okanetransfer.shared.security;

import com.okanetransfer.entity.user.Utilisateur;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Composant responsable de la gestion des tokens JWT.
 *
 * Deux types de tokens :
 *   - access token  → durée de vie 1h  (3 600 000 ms) — envoyé à chaque requête API
 *   - refresh token → durée de vie 7j  (604 800 000 ms) — utilisé pour renouveler l'access token
 *
 * Algorithme de signature : HS256 (HMAC-SHA256)
 * Clé secrète : lue depuis la propriété jwt.secret (application.properties → ${JWT_SECRET}, min 32 chars)
 *
 * Utilisé par :
 *   - AuthServiceImpl    → generateAccessToken(), generateRefreshToken(), invalidateToken()
 *   - AuthServiceImpl    → validateToken(), extractEmail() dans refreshToken()
 *   - JwtAuthFilter      → validateToken(), extractEmail() à chaque requête HTTP
 */
@Component
public class JwtTokenProvider {

    // durée de vie de l'access token en millisecondes (jwt.expiration)
    private final long accessTokenExpiration;

    // durée de vie du refresh token : 7 jours en millisecondes
    private static final long REFRESH_TOKEN_EXPIRATION = 604_800_000L;

    // clé de signature dérivée de jwt.secret (application.properties)
    private final SecretKey secretKey;

    // blacklist des tokens invalidés (logout)
    // en production → utiliser Redis avec TTL = expiration du token
    private final Set<String> blacklistedTokens = new HashSet<>();

    public JwtTokenProvider(@Value("${jwt.secret}") String secret,
                             @Value("${jwt.expiration}") long accessTokenExpiration) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException(
                "jwt.secret manquant ou trop court (minimum 32 caractères requis)"
            );
        }

        // dériver une SecretKey HMAC-SHA256 depuis la chaîne de caractères
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenExpiration = accessTokenExpiration;
    }

    // =========================================================================
    // Génération des tokens
    // =========================================================================

    /**
     * Génère un access token JWT pour l'utilisateur authentifié.
     *
     * Payload (claims) inclus :
     *   - sub   : email de l'utilisateur (identifiant unique)
     *   - role  : rôle Spring Security (ex: "ROLE_AGENT")
     *   - iat   : date d'émission
     *   - exp   : date d'expiration (iat + 1h)
     *
     * @param utilisateur l'utilisateur authentifié
     * @return le token JWT signé en String
     */
    public String generateAccessToken(Utilisateur utilisateur) {
        Date now        = new Date();
        Date expiration = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
            .subject(utilisateur.getEmail())                  // sub = email
            .claim("role", utilisateur.getRole().name())      // claim custom : rôle
            .issuedAt(now)                                    // iat = maintenant
            .expiration(expiration)                           // exp = maintenant + 1h
            .signWith(secretKey)                              // signature HS256
            .compact();
    }

    /**
     * Génère un refresh token JWT.
     *
     * Payload minimal — contient uniquement l'email et les dates.
     * Pas de rôle car le refresh token ne donne pas accès aux endpoints.
     *
     * @param email l'email de l'utilisateur
     * @return le refresh token JWT signé en String
     */
    public String generateRefreshToken(String email) {
        Date now        = new Date();
        Date expiration = new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION);

        return Jwts.builder()
            .subject(email)              // sub = email
            .issuedAt(now)               // iat = maintenant
            .expiration(expiration)      // exp = maintenant + 7j
            .signWith(secretKey)         // signature HS256
            .compact();
    }

    // =========================================================================
    // Validation et extraction
    // =========================================================================

    /**
     * Valide un token JWT.
     *
     * Vérifie :
     *   1. La signature (clé secrète correcte)
     *   2. La date d'expiration (non expiré)
     *   3. Que le token n'est pas dans la blacklist (logout)
     *
     * @param token le token JWT à valider
     * @return true si valide, false sinon
     */
    public boolean validateToken(String token) {
        try {
            // 1. vérifier si le token est blacklisté (logout)
            if (blacklistedTokens.contains(token)) {
                return false;
            }

            // 2. parser et vérifier la signature + expiration
            // lève une exception si invalide ou expiré
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);

            return true;

        } catch (JwtException | IllegalArgumentException e) {
            // token invalide : signature incorrecte, expiré, malformé...
            return false;
        }
    }

    /**
     * Extrait l'email (subject) depuis un token JWT.
     * Appeler validateToken() avant cette méthode.
     *
     * @param token le token JWT (supposé valide)
     * @return l'email contenu dans le claim "sub"
     */
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Extrait le rôle depuis un access token JWT.
     * Le rôle n'est présent que dans l'access token — pas dans le refresh token.
     *
     * @param token l'access token JWT
     * @return le rôle ex: "ROLE_AGENT"
     */
    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    /**
     * Extrait la date d'expiration depuis un token JWT.
     * Utilisé pour calculer le TTL lors de la mise en blacklist.
     *
     * @param token le token JWT
     * @return la date d'expiration
     */
    public Date extractExpiration(String token) {
        return getClaims(token).getExpiration();
    }

    // =========================================================================
    // Invalidation (logout)
    // =========================================================================

    /**
     * Invalide un token en l'ajoutant à la blacklist.
     * Le token sera rejeté par validateToken() jusqu'à sa date d'expiration naturelle.
     *
     * Note : en production, remplacer le HashSet par Redis avec TTL automatique
     * pour éviter une accumulation de tokens en mémoire.
     *
     * @param token le token à invalider
     */
    public void invalidateToken(String token) {
        if (token != null && !token.isBlank()) {
            blacklistedTokens.add(token);
        }
    }

    // =========================================================================
    // Méthode privée utilitaire
    // =========================================================================

    /**
     * Parse le token et retourne les claims (payload).
     *
     * @param token le token JWT
     * @return l'objet Claims contenant toutes les informations du payload
     * @throws JwtException si le token est invalide
     */
    private Claims getClaims(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}
