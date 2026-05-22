package com.okanetransfer.service.dto.transfert.response;

public class BeneficiaireResponse {

    private Long id;
    private String nom;
    private String prenom;
    private String telephone;
    private String pays;
    private Boolean surListeSurveillance;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
