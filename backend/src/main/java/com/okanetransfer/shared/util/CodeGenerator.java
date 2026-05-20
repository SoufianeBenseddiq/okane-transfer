package com.okanetransfer.shared.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class CodeGenerator {

    // code retrait : 8 chars alphanumériques en majuscules
    // ex: "K7X2M9QA"
    public String generateCodeRetrait() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        // retire les caractères ambigus : I, O, 0, 1
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    // référence : préfixe + date + séquence
    // ex: "TRF-20260517-00892"
    public String generateReference() {
        String date = LocalDate.now().format(
                DateTimeFormatter.ofPattern("yyyyMMdd")
        );
        String seq = String.format("%05d",
                new SecureRandom().nextInt(99999)
        );
        return "TRF-" + date + "-" + seq;
    }
}
