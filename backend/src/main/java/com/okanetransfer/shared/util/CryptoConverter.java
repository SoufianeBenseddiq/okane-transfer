package com.okanetransfer.shared.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

// @Converter(autoApply = false) → on l'applique manuellement
// avec @Convert(converter = CryptoConverter.class)
// uniquement sur les champs qui le nécessitent
@Converter
public class CryptoConverter implements AttributeConverter<String, String> {

    private final CryptoUtil cryptoUtil;

    public CryptoConverter(CryptoUtil cryptoUtil) {
        this.cryptoUtil = cryptoUtil;
    }

    // appelé automatiquement par Hibernate AVANT d'écrire en base
    // valeur Java en clair → valeur chiffrée stockée en BDD
    @Override
    public String convertToDatabaseColumn(String plainValue) {
        if (plainValue == null) return null;
        return cryptoUtil.encrypt(plainValue);
    }

    // appelé automatiquement par Hibernate APRÈS lecture depuis la base
    // valeur chiffrée en BDD → valeur Java en clair
    @Override
    public String convertToEntityAttribute(String encryptedValue) {
        if (encryptedValue == null) return null;
        return cryptoUtil.decrypt(encryptedValue);
    }
}
