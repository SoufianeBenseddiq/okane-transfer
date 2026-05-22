package com.okanetransfer.service.converter.transfert;

import com.okanetransfer.entity.transfert.Beneficiaire;
import com.okanetransfer.service.dto.transfert.response.BeneficiaireResponse;

public final class BeneficiaireConverter {

    private BeneficiaireConverter() {
    }

    public static BeneficiaireResponse toResponse(Beneficiaire beneficiaire) {
        BeneficiaireResponse response = new BeneficiaireResponse();
        response.setId(beneficiaire.getId());
        response.setNom(beneficiaire.getNom());
        response.setPrenom(beneficiaire.getPrenom());
        response.setTelephone(beneficiaire.getTelephone());
        response.setPays(beneficiaire.getPays());
        response.setSurListeSurveillance(beneficiaire.getSurListeSurveillance());
        return response;
    }
}
