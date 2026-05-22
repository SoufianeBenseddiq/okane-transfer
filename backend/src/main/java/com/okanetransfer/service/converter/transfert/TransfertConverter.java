package com.okanetransfer.service.converter.transfert;


import com.okanetransfer.entity.transfert.Transfert;
import com.okanetransfer.service.dto.transfert.response.TransfertResponse;

public class TransfertConverter {

    private TransfertConverter() {
    }

    public static TransfertResponse toResponse(Transfert t) {

        TransfertResponse r = new TransfertResponse();

        r.setCodeRetrait(t.getCodeRetrait());
        r.setNumeroReference(t.getNumeroReference());
        r.setMontantEnvoye(t.getMontantEnvoye());
        r.setMontantRecu(t.getMontantRecu());
        r.setFrais(t.getFrais());
        r.setStatut(t.getStatut().name());
        r.setCreeLe(t.getCreeLe());

        return r;
    }
}