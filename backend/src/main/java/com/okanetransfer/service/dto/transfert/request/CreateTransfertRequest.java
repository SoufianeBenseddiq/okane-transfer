package com.okanetransfer.service.dto.transfert.request;

import java.math.BigDecimal;

public class CreateTransfertRequest {

    private Long clientId;

    private Long pieceIdentiteId;

    private Long agentId;

    private Long agenceEnvoiId;

    private Long corridorId;

    private Long grilleTarifaireId;

    private String nomBeneficiaire;

    private String prenomBeneficiaire;

    private String telephoneBeneficiaire;

    private String paysBeneficiaire;

    private BigDecimal montant;

    public CreateTransfertRequest() {
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getPieceIdentiteId() {
        return pieceIdentiteId;
    }

    public void setPieceIdentiteId(Long pieceIdentiteId) {
        this.pieceIdentiteId = pieceIdentiteId;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public Long getAgenceEnvoiId() {
        return agenceEnvoiId;
    }

    public void setAgenceEnvoiId(Long agenceEnvoiId) {
        this.agenceEnvoiId = agenceEnvoiId;
    }

    public Long getCorridorId() {
        return corridorId;
    }

    public void setCorridorId(Long corridorId) {
        this.corridorId = corridorId;
    }

    public Long getGrilleTarifaireId() {
        return grilleTarifaireId;
    }

    public void setGrilleTarifaireId(Long grilleTarifaireId) {
        this.grilleTarifaireId = grilleTarifaireId;
    }

    public String getNomBeneficiaire() {
        return nomBeneficiaire;
    }

    public void setNomBeneficiaire(String nomBeneficiaire) {
        this.nomBeneficiaire = nomBeneficiaire;
    }

    public String getPrenomBeneficiaire() {
        return prenomBeneficiaire;
    }

    public void setPrenomBeneficiaire(String prenomBeneficiaire) {
        this.prenomBeneficiaire = prenomBeneficiaire;
    }

    public String getTelephoneBeneficiaire() {
        return telephoneBeneficiaire;
    }

    public void setTelephoneBeneficiaire(String telephoneBeneficiaire) {
        this.telephoneBeneficiaire = telephoneBeneficiaire;
    }

    public String getPaysBeneficiaire() {
        return paysBeneficiaire;
    }

    public void setPaysBeneficiaire(String paysBeneficiaire) {
        this.paysBeneficiaire = paysBeneficiaire;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }
}