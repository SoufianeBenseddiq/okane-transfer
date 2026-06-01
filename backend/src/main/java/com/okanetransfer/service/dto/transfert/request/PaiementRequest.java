package com.okanetransfer.service.dto.transfert.request;

import com.okanetransfer.shared.enums.TypePiece;

public class PaiementRequest {

    private String codeRetrait;
    private Long agenceRetraitId;
    private Long agentId;
    private TypePiece typePieceBeneficiaire;
    private String numeroPieceBeneficiaire;

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

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public TypePiece getTypePieceBeneficiaire() {
        return typePieceBeneficiaire;
    }

    public void setTypePieceBeneficiaire(TypePiece typePieceBeneficiaire) {
        this.typePieceBeneficiaire = typePieceBeneficiaire;
    }

    public String getNumeroPieceBeneficiaire() {
        return numeroPieceBeneficiaire;
    }

    public void setNumeroPieceBeneficiaire(String numeroPieceBeneficiaire) {
        this.numeroPieceBeneficiaire = numeroPieceBeneficiaire;
    }
}