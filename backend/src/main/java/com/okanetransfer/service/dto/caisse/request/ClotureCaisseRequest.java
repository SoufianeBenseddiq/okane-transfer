package com.okanetransfer.service.dto.caisse.request;


import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ClotureCaisseRequest {

    @NotNull(message = "L'ID de l'agent est obligatoire")
    private String agentEmail;

    @NotNull(message = "La date est obligatoire")
    private LocalDate date;

    @NotNull(message = "Le solde saisi est obligatoire")
    @DecimalMin(value = "0.0", message = "Le solde saisi ne peut pas être négatif")
    private BigDecimal soldeSaisi;

    // Getters & Setters
    public String getAgentEmail() { return agentEmail; }
    public void setAgentEmail(String agentEmail) { this.agentEmail = agentEmail; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public BigDecimal getSoldeSaisi() { return soldeSaisi; }
    public void setSoldeSaisi(BigDecimal soldeSaisi) { this.soldeSaisi = soldeSaisi; }
}