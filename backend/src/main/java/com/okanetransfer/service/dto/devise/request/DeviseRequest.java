package com.okanetransfer.service.dto.devise.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class DeviseRequest {

    @NotBlank(message = "Le code est obligatoire")
    @Size(min = 3, max = 3, message = "Le code doit contenir exactement 3 caractères")
    private String code;

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le symbole est obligatoire")
    private String symbole;

    @NotNull(message = "Le taux vers euro est obligatoire")
    @Positive(message = "Le taux doit être positif")
    private BigDecimal tauxVersEuro;

    private String sourceTaux = "MANUEL";

    // getters / setters
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getSymbole() { return symbole; }
    public void setSymbole(String symbole) { this.symbole = symbole; }

    public BigDecimal getTauxVersEuro() { return tauxVersEuro; }
    public void setTauxVersEuro(BigDecimal tauxVersEuro) { this.tauxVersEuro = tauxVersEuro; }

    public String getSourceTaux() { return sourceTaux; }
    public void setSourceTaux(String sourceTaux) { this.sourceTaux = sourceTaux; }
}