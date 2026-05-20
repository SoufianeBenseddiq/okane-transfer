package com.okanetransfer.entity.aml;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "regles_aml")
public class RegleAML {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nom;             // ex: "DETECTION_FRACTIONNEMENT"

    @Column(nullable = false)
    private String description;

    private BigDecimal seuilMontant;            // ex: 5000 MAD

    private Integer seuilNbTransactions;        // ex: 3

    private Integer fenetreTempsMinutes;        // ex: 180 (3h)

    @Column(nullable = false)
    private Boolean active = true;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getSeuilMontant() {
        return seuilMontant;
    }

    public void setSeuilMontant(BigDecimal seuilMontant) {
        this.seuilMontant = seuilMontant;
    }

    public Integer getSeuilNbTransactions() {
        return seuilNbTransactions;
    }

    public void setSeuilNbTransactions(Integer seuilNbTransactions) {
        this.seuilNbTransactions = seuilNbTransactions;
    }

    public Integer getFenetreTempsMinutes() {
        return fenetreTempsMinutes;
    }

    public void setFenetreTempsMinutes(Integer fenetreTempsMinutes) {
        this.fenetreTempsMinutes = fenetreTempsMinutes;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}