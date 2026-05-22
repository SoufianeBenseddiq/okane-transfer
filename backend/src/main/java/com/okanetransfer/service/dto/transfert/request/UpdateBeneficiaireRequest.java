package com.okanetransfer.service.dto.transfert.request;

import jakarta.validation.constraints.NotBlank;

public class UpdateBeneficiaireRequest {

    @NotBlank
    private String nom;

    @NotBlank
    private String prenom;

    @NotBlank
    private String telephone;

    @NotBlank
    private String pays;

    private Boolean surListeSurveillance = false;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public Boolean getSurListeSurveillance() {
        return surListeSurveillance;
    }

    public void setSurListeSurveillance(Boolean surListeSurveillance) {
        this.surListeSurveillance = surListeSurveillance;
    }
}
