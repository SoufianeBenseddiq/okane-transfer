package com.okanetransfer.service.dto.transfert.request;

import java.math.BigDecimal;

public class UpdateTransfertRequest {

    private String nomBeneficiaire;

    private String prenomBeneficiaire;

    private String telephoneBeneficiaire;

    private String paysBeneficiaire;

    private BigDecimal montant;

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
