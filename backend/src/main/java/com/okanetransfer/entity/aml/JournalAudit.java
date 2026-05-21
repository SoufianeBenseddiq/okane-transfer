package com.okanetransfer.entity.aml;

import java.time.LocalDateTime;

import com.okanetransfer.entity.user.Utilisateur;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "journal_audit")
public class JournalAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acteur_id", nullable = false)
    private Utilisateur acteur;

    @Column(nullable = false)
    private String action;          // ex: "PAIEMENT_TRANSFERT"

    @Column(nullable = false)
    private String entiteCible;     // ex: "Transfert"

    @Column(nullable = false)
    private Long idCible;           // ex: 892

    @Column(columnDefinition = "TEXT")
    private String detailAvant;     // JSON état avant

    @Column(columnDefinition = "TEXT")
    private String detailApres;     // JSON état après

    @Column(nullable = false)
    private LocalDateTime dateHeure;

    private String ipAdresse;

    @PrePersist
    public void prePersist() {
        this.dateHeure = LocalDateTime.now();
    }

    long getId() {
        return id;
    }
    void setId(long id) {
        this.id = id;
    }
    public Utilisateur getActeur() {
        return acteur;
    }
    public void setActeur(Utilisateur acteur) {
        this.acteur = acteur;
    }
    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;}  
    public String getEntiteCible() {
        return entiteCible; }
    public void setEntiteCible(String entiteCible) {
        this.entiteCible = entiteCible;
    }
    public Long getIdCible() {
        return idCible;
    }
    public void setIdCible(Long idCible) {
        this.idCible = idCible;
    }
    public String getDetailAvant() {
        return detailAvant;
    }
    public void setDetailAvant(String detailAvant) {
        this.detailAvant = detailAvant;
    }
    public String getDetailApres() {
        return detailApres;
    }
    public void setDetailApres(String detailApres) {
        this.detailApres = detailApres;
    }
    public LocalDateTime getDateHeure() {
        return dateHeure;
    }
    public void setDateHeure(LocalDateTime dateHeure) {
        this.dateHeure = dateHeure;
    }
    public String getIpAdresse() {
        return ipAdresse;
    }
    public void setIpAdresse(String ipAdresse) {
        this.ipAdresse = ipAdresse;
    } 
    
    
}