package com.okanetransfer.entity.aml;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "liste_ofac")
public class ListeOFAC {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    private String alias;
    private String pays;
    private String motifInscription;

    @Column(nullable = false)
    private LocalDate dateAjout;

    @Column(nullable = false)
    private Boolean actif = true;

    // true = ajouté manuellement par admin
    // false = ajouté automatiquement par AmlService
    @Column(nullable = false)
    private Boolean ajoutManuel = true;

    @PrePersist
    public void prePersist() {
        this.dateAjout = LocalDate.now();
    }

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

    public LocalDate getDateAjout() {
        return dateAjout;
    }

    public void setDateAjout(LocalDate dateAjout) {
        this.dateAjout = dateAjout;
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