package com.okanetransfer.service.impl.user;

import com.okanetransfer.entity.agence.Agence;
import com.okanetransfer.entity.user.Agent;
import com.okanetransfer.entity.user.Manager;
import com.okanetransfer.entity.user.Utilisateur;
import com.okanetransfer.repository.user.UtilisateurRepository;
import com.okanetransfer.service.dto.user.request.LoginRequest;
import com.okanetransfer.service.dto.user.request.OtpRequest;
import com.okanetransfer.service.dto.user.request.RefreshTokenRequest;
import com.okanetransfer.service.dto.user.response.TokenResponse;
import com.okanetransfer.service.facade.user.AuthService;
import com.okanetransfer.shared.exception.AccesRefuseException;
import com.okanetransfer.shared.security.JwtTokenProvider;
//import com.okanetransfer.shared.security.OtpService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
//    private final OtpService otpService;

    public AuthServiceImpl(UtilisateurRepository utilisateurRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder       = passwordEncoder;
        this.jwtTokenProvider      = jwtTokenProvider;
//        this.otpService            = otpService;
    }

    @Override
    public TokenResponse login(LoginRequest request) {
        // 1. chercher l'utilisateur par email
        Utilisateur user = utilisateurRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new AccesRefuseException("Email ou mot de passe incorrect"));

        // 2. vérifier que le compte est actif
        if (!user.getActif()) {
            throw new AccesRefuseException("Compte désactivé");
        }

        // 2b. vérifier que l'agence est active (Agent et Manager uniquement)
        if (user instanceof Agent agent) {
            Agence agence = agent.getAgence();
            if (agence != null && !Boolean.TRUE.equals(agence.getActive())) {
                throw new AccesRefuseException("Agence suspendue. Contactez l'administration.");
            }
        } else if (user instanceof Manager manager) {
            Agence agence = manager.getAgence();
            if (agence != null && !Boolean.TRUE.equals(agence.getActive())) {
                throw new AccesRefuseException("Agence suspendue. Contactez l'administration.");
            }
        }

        // 3. vérifier le mot de passe
        if (!passwordEncoder.matches(request.getMotDePasse(), user.getMotDePasseHash())) {
            throw new AccesRefuseException("Email ou mot de passe incorrect");
        }

//        // 4. si l'utilisateur est un Client avec 2FA actif
//        if (user instanceof com.okanetransfer.entity.user.Client client
//                && client.getTwoFactorActive()) {
//            // envoyer OTP par SMS
//            otpService.generateOtp(user.getTelephone());
//            // retourner un token partiel (sans droits) pour la 2e étape
//            return TokenResponse.partial(user.getEmail());
//        }

        // 5. sinon → générer directement les tokens
        return buildTokenResponse(user);
    }

//    @Override
//    public TokenResponse verifyOtp(OtpRequest request) {
//        // 1. vérifier le code OTP
//        boolean valid = otpService.verifyOtp(request.getTelephone(), request.getCode());
//        if (!valid) {
//            throw new AccesRefuseException("Code OTP invalide ou expiré");
//        }
//
//        // 2. charger l'utilisateur
//        Utilisateur user = utilisateurRepository
//                .findByEmail(request.getEmail())
//                .orElseThrow(() -> new AccesRefuseException("Utilisateur introuvable"));
//
//        // 3. générer les tokens
//        return buildTokenResponse(user);
//    }

    @Override
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        // 1. valider le refresh token
        if (!jwtTokenProvider.validateToken(request.getRefreshToken())) {
            throw new AccesRefuseException("Refresh token invalide ou expiré");
        }

        // 2. extraire l'email depuis le token
        String email = jwtTokenProvider.extractEmail(request.getRefreshToken());

        // 3. recharger l'utilisateur
        Utilisateur user = utilisateurRepository
                .findByEmail(email)
                .orElseThrow(() -> new AccesRefuseException("Utilisateur introuvable"));

        // 4. générer un nouvel access token
        String newAccessToken = jwtTokenProvider.generateAccessToken(user);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(request.getRefreshToken())  // même refresh token
                .tokenType("Bearer")
                .expiresIn(3600L)
                .build();
    }

    @Override
    public void logout(String accessToken) {
        // invalider le token côté serveur
        // dans une implémentation stateless pure, on peut juste laisser expirer
        // pour un vrai blacklist → ajouter le token dans une table ou un cache Redis
        jwtTokenProvider.invalidateToken(accessToken);
    }

    private TokenResponse buildTokenResponse(Utilisateur user) {
        String accessToken  = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(3600L)
                .build();
    }
}