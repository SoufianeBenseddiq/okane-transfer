package com.okanetransfer.shared.util;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class CryptoUtil {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    // clé lue depuis variable d'environnement — jamais en dur dans le code
    private final SecretKeySpec secretKey;
    private final IvParameterSpec iv;

    public CryptoUtil() {
        String key = System.getenv("AES_SECRET_KEY");
        // AES-256 = clé de 32 bytes exactement
        // ex: "okane1234567890abcdef1234567890ab"
        if (key == null || key.length() != 32) {
            throw new IllegalStateException(
                    "AES_SECRET_KEY manquante ou invalide (32 chars requis)"
            );
        }
        String ivStr = System.getenv("AES_IV");
        // IV = 16 bytes exactement
        // ex: "okaneIV123456789"
        if (ivStr == null || ivStr.length() != 16) {
            throw new IllegalStateException(
                    "AES_IV manquante ou invalide (16 chars requis)"
            );
        }
        this.secretKey = new SecretKeySpec(key.getBytes(), "AES");
        this.iv        = new IvParameterSpec(ivStr.getBytes());
    }

    // chiffre une valeur en clair → retourne une chaîne Base64
    // ex: encrypt("BE123456") → "aG9uZXN0bHkgdGhpcyBpcyBmYWtlCg=="
    public String encrypt(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byte[] encrypted = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Erreur chiffrement AES", e);
        }
    }

    // déchiffre une chaîne Base64 → retourne la valeur en clair
    // ex: decrypt("aG9uZXN0bHkgdGhpcyBpcyBmYWtlCg==") → "BE123456"
    public String decrypt(String encryptedText) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            byte[] decoded    = Base64.getDecoder().decode(encryptedText);
            byte[] decrypted  = cipher.doFinal(decoded);
            return new String(decrypted);
        } catch (Exception e) {
            throw new RuntimeException("Erreur déchiffrement AES", e);
        }
    }
}