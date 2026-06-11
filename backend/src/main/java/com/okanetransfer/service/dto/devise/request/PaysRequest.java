package com.okanetransfer.service.dto.devise.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PaysRequest {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le code ISO est obligatoire")
    @Size(min = 2, max = 3, message = "Le code ISO doit contenir 2 ou 3 caractères")
    private String codeIso;

    @NotBlank(message = "L'indicatif téléphonique est obligatoire")
    private String indicatifTel;

    private String formatTel;

    private Integer longueurTel;

    @NotNull(message = "La devise est obligatoire")
    private Long deviseId;

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getCodeIso() { return codeIso; }
    public void setCodeIso(String codeIso) { this.codeIso = codeIso; }

    public String getIndicatifTel() { return indicatifTel; }
    public void setIndicatifTel(String indicatifTel) { this.indicatifTel = indicatifTel; }

    public String getFormatTel() { return formatTel; }
    public void setFormatTel(String formatTel) { this.formatTel = formatTel; }

    public Integer getLongueurTel() { return longueurTel; }
    public void setLongueurTel(Integer longueurTel) { this.longueurTel = longueurTel; }

    public Long getDeviseId() { return deviseId; }
    public void setDeviseId(Long deviseId) { this.deviseId = deviseId; }
}
