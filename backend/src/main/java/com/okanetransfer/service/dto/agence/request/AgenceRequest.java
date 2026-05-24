package com.okanetransfer.service.dto.agence.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class AgenceRequest {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "L'adresse est obligatoire")
    private String adresse;

    @NotBlank(message = "Le pays est obligatoire")
    private String pays;

    @NotNull(message = "Le plafond journalier est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le plafond doit être positif")
    private BigDecimal plafondJournalier;

    private Long responsableId;

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getPays() { return pays; }
    public void setPays(String pays) { this.pays = pays; }

    public BigDecimal getPlafondJournalier() { return plafondJournalier; }
    public void setPlafondJournalier(BigDecimal plafondJournalier) { this.plafondJournalier = plafondJournalier; }

    public Long getResponsableId() { return responsableId; }
    public void setResponsableId(Long responsableId) { this.responsableId = responsableId; }
}