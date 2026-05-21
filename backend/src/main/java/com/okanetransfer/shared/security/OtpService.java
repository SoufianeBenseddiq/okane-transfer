package com.okanetransfer.shared.security;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service de gestion des codes OTP (One-Time Password) pour le 2FA.
 *
 * Flux d'utilisation :
 *   1. AuthServiceImpl appelle generateOtp(telephone)
 *      → génère un code à 6 chiffres
 *      → le stocke en mémoire avec une expiration de 5 minutes
 *      → (en production) envoie le code par SMS via une gateway (Twilio, etc.)
 *
 *   2. AuthServiceImpl appelle verifyOtp(telephone, code)
 *      → vérifie que le code correspond
 *      → vérifie que le code n'est pas expiré
 *      → supprime le code après vérification (usage unique)
 *
 * Stockage : ConcurrentHashMap en mémoire
 *   - thread-safe pour les accès concurrents
 *   - en production → remplacer par Redis avec TTL automatique
 *
 * Utilisé uniquement par :
 *   - AuthServiceImpl → generateOtp() lors du login 2FA
 *   - AuthServiceImpl → verifyOtp() lors de la vérification OTP
 */
@Component
public class OtpService {

    // durée de validité d'un OTP : 5 minutes
    private static final int OTP_EXPIRATION_MINUTES = 5;

    // longueur du code OTP
    private static final int OTP_LENGTH = 6;

    // générateur de nombres aléatoires sécurisé (cryptographiquement fort)
    private final SecureRandom secureRandom = new SecureRandom();

    // stockage en mémoire : telephone → OtpEntry (code + expiration)
    // ConcurrentHashMap = thread-safe sans synchronisation manuelle
    private final Map<String, OtpEntry> otpStore = new ConcurrentHashMap<>();

    // =========================================================================
    // Génération
    // =========================================================================

    /**
     * Génère un code OTP à 6 chiffres pour un numéro de téléphone donné.
     *
     * Si un OTP existait déjà pour ce téléphone → il est remplacé.
     * Utile si l'utilisateur demande un renvoi du code.
     *
     * En production : envoyer le code par SMS via Twilio ou équivalent.
     * Pour ce projet académique : le code est loggué en console (simulé).
     *
     * @param telephone le numéro de téléphone du client (ex: "+212661234567")
     * @return le code OTP généré (pour les tests — en prod ne jamais le retourner)
     */
    public String generateOtp(String telephone) {
        // générer un code à 6 chiffres entre 100000 et 999999
        int code = 100000 + secureRandom.nextInt(900000);
        String codeStr = String.valueOf(code);

        // calculer la date d'expiration
        LocalDateTime expiration = LocalDateTime.now()
            .plusMinutes(OTP_EXPIRATION_MINUTES);

        // stocker dans la map (remplace l'ancien si existant)
        otpStore.put(telephone, new OtpEntry(codeStr, expiration));

        // TODO production : envoyer par SMS via Twilio
        // twilioClient.sendSms(telephone, "Votre code OTP : " + codeStr);

        // simulation pour le développement : afficher en console
        System.out.println("[OTP] Téléphone: " + telephone + " → Code: " + codeStr
            + " (expire dans " + OTP_EXPIRATION_MINUTES + " min)");

        return codeStr;
    }

    // =========================================================================
    // Vérification
    // =========================================================================

    /**
     * Vérifie un code OTP.
     *
     * Conditions pour qu'un OTP soit valide :
     *   1. Un OTP existe pour ce numéro de téléphone
     *   2. Le code correspond exactement
     *   3. Le code n'est pas expiré (moins de 5 minutes)
     *
     * Après vérification réussie → le code est supprimé (usage unique).
     * Un OTP ne peut être utilisé qu'une seule fois.
     *
     * @param telephone le numéro de téléphone
     * @param code      le code saisi par l'utilisateur
     * @return true si le code est valide et non expiré, false sinon
     */
    public boolean verifyOtp(String telephone, String code) {
        // 1. chercher l'entrée OTP pour ce téléphone
        OtpEntry entry = otpStore.get(telephone);

        // 2. aucun OTP trouvé pour ce téléphone
        if (entry == null) {
            return false;
        }

        // 3. vérifier si le code est expiré
        if (LocalDateTime.now().isAfter(entry.expiration())) {
            // nettoyer l'entrée expirée
            otpStore.remove(telephone);
            return false;
        }

        // 4. vérifier que le code correspond
        if (!entry.code().equals(code)) {
            return false;
        }

        // 5. OTP valide → supprimer immédiatement (usage unique)
        otpStore.remove(telephone);
        return true;
    }

    // =========================================================================
    // Utilitaires
    // =========================================================================

    /**
     * Expire manuellement un OTP.
     * Utilisé si l'utilisateur abandonne le processus de connexion.
     *
     * @param telephone le numéro de téléphone
     */
    public void expireOtp(String telephone) {
        otpStore.remove(telephone);
    }

    /**
     * Vérifie si un OTP est en attente pour un numéro de téléphone.
     * Utilisé pour éviter les requêtes OTP en double.
     *
     * @param telephone le numéro de téléphone
     * @return true si un OTP non expiré existe pour ce numéro
     */
    public boolean hasActiveOtp(String telephone) {
        OtpEntry entry = otpStore.get(telephone);
        if (entry == null) return false;

        // vérifier si non expiré
        if (LocalDateTime.now().isAfter(entry.expiration())) {
            otpStore.remove(telephone);
            return false;
        }
        return true;
    }

    /**
     * Nettoie tous les OTPs expirés de la mémoire.
     * À appeler périodiquement (ex: @Scheduled toutes les heures)
     * pour éviter une accumulation en mémoire.
     */
    public void cleanExpiredOtps() {
        LocalDateTime now = LocalDateTime.now();
        otpStore.entrySet().removeIf(entry ->
            now.isAfter(entry.getValue().expiration())
        );
    }

    // =========================================================================
    // Classe interne — entrée OTP
    // =========================================================================

    /**
     * Représente une entrée dans le store OTP.
     * Record Java 16+ : immutable, equals/hashCode/toString générés automatiquement.
     *
     * @param code       le code OTP à 6 chiffres
     * @param expiration la date/heure d'expiration du code
     */
    private record OtpEntry(String code, LocalDateTime expiration) {}
}
