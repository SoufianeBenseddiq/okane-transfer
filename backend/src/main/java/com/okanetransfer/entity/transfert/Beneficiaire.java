package com.okanetransfer.entity.transfert;

import jakarta.persistence.*;

@Entity
@Table(name = "beneficiaires")
public class Beneficiaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false)
    private String telephone;

    @Column(nullable = false)
    private String pays;

    // mis à true automatiquement si détecté sur liste OFAC
    @Column(nullable = false)
    private Boolean surListeSurveillance = false;

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