package com.okanetransfer.service.dto.agence.response;


import java.math.BigDecimal;

public class AgenceResponse {

    private Long id;
    private String nom;
    private String adresse;
    private String pays;
    private BigDecimal plafondJournalier;
    private BigDecimal montantTraiteAujourdhui;
    private Boolean active;
    private String responsableNom;
    private Long responsableId;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getPays() { return pays; }
    public void setPays(String pays) { this.pays = pays; }

    public BigDecimal getPlafondJournalier() { return plafondJournalier; }
    public void setPlafondJournalier(BigDecimal plafondJournalier) { this.plafondJournalier = plafondJournalier; }

    public BigDecimal getMontantTraiteAujourdhui() { return montantTraiteAujourdhui; }
    public void setMontantTraiteAujourdhui(BigDecimal montantTraiteAujourdhui) { this.montantTraiteAujourdhui = montantTraiteAujourdhui; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public String getResponsableNom() { return responsableNom; }
    public void setResponsableNom(String responsableNom) { this.responsableNom = responsableNom; }

    public Long getResponsableId() { return responsableId; }
    public void setResponsableId(Long responsableId) { this.responsableId = responsableId; }
}