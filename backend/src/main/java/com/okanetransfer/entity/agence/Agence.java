package com.okanetransfer.entity.agence;

import com.okanetransfer.entity.user.Agent;
import com.okanetransfer.entity.user.Manager;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "agences")
public class Agence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String adresse;

    @Column(nullable = false)
    private String pays;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal plafondJournalier;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montantTraiteAujourdhui = BigDecimal.ZERO;

    @Column(nullable = false)
    private Boolean active = true;

    @OneToOne(mappedBy = "agence")
    private Manager responsable;

    @OneToMany(mappedBy = "agence")
    private List<Agent> agents;

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

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public BigDecimal getPlafondJournalier() {
        return plafondJournalier;
    }

    public void setPlafondJournalier(BigDecimal plafondJournalier) {
        this.plafondJournalier = plafondJournalier;
    }

    public BigDecimal getMontantTraiteAujourdhui() {
        return montantTraiteAujourdhui;
    }

    public void setMontantTraiteAujourdhui(BigDecimal montantTraiteAujourdhui) {
        this.montantTraiteAujourdhui = montantTraiteAujourdhui;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Manager getResponsable() {
        return responsable;
    }

    public void setResponsable(Manager responsable) {
        this.responsable = responsable;
    }

    public List<Agent> getAgents() {
        return agents;
    }

    public void setAgents(List<Agent> agents) {
        this.agents = agents;
    }
}