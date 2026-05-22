package com.okanetransfer.service.dto.transfert.request;

public class PaiementRequest {

    private String codeRetrait;

    private Long agenceRetraitId;

    public PaiementRequest() {
    }

    public String getCodeRetrait() {
        return codeRetrait;
    }

    public void setCodeRetrait(String codeRetrait) {
        this.codeRetrait = codeRetrait;
    }

    public Long getAgenceRetraitId() {
        return agenceRetraitId;
    }

    public void setAgenceRetraitId(Long agenceRetraitId) {
        this.agenceRetraitId = agenceRetraitId;
    }
}