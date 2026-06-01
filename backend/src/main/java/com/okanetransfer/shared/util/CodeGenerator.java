package com.okanetransfer.shared.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class CodeGenerator {

    // code retrait formaté en 4-4
    // ex: "K7X2-M9QA"
    public String generateCodeRetrait() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        // retire les caractères ambigus : I, O, 0, 1
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return formatCodeRetrait(sb.toString());
    }

    public String formatCodeRetrait(String codeRetrait) {
        if (codeRetrait == null) {
            return null;
        }

        String normalise = codeRetrait.replace("-", "").toUpperCase();
        if (normalise.length() != 8) {
            return normalise;
        }

        return normalise.substring(0, 4) + "-" + normalise.substring(4);
    }

    public String normaliseCodeRetrait(String codeRetrait) {
        if (codeRetrait == null) {
            return null;
        }

        return codeRetrait.replace("-", "").toUpperCase();
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
