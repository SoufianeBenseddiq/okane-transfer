package com.okanetransfer.service.dto.agence.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RevisionPlafondRequest {

    @NotNull(message = "Le nouveau plafond est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le plafond doit être positif")
    private BigDecimal nouveauPlafond;

    @NotBlank(message = "La justification est obligatoire")
    private String justification;

    public BigDecimal getNouveauPlafond() {
        return nouveauPlafond;
    }

    public void setNouveauPlafond(BigDecimal nouveauPlafond) {
        this.nouveauPlafond = nouveauPlafond;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }
}
