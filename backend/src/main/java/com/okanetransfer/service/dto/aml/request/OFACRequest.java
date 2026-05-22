package com.okanetransfer.service.dto.aml.request;

public class OFACRequest {

    private String nom;
    private String prenom;
    private String alias;
    private String pays;
    private String motifInscription;
    private Boolean actif = true;
    private Boolean ajoutManuel = true;

    public OFACRequest() {
    }

    public OFACRequest(String nom, String prenom, String alias, String pays, String motifInscription) {
        this.nom = nom;
        this.prenom = prenom;
        this.alias = alias;
        this.pays = pays;
        this.motifInscription = motifInscription;
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

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public String getMotifInscription() {
        return motifInscription;
    }

    public void setMotifInscription(String motifInscription) {
        this.motifInscription = motifInscription;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public Boolean getAjoutManuel() {
        return ajoutManuel;
    }

    public void setAjoutManuel(Boolean ajoutManuel) {
        this.ajoutManuel = ajoutManuel;
    }
}
