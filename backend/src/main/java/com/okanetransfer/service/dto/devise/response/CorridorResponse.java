package com.okanetransfer.service.dto.devise.response;

import java.time.LocalDate;

public class CorridorResponse {

    private Long id;
    private String deviseSource;
    private String deviseDestination;
    private Boolean actif;
    private LocalDate dateActivation;

    // getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDeviseSource() { return deviseSource; }
    public void setDeviseSource(String deviseSource) { this.deviseSource = deviseSource; }

    public String getDeviseDestination() { return deviseDestination; }
    public void setDeviseDestination(String deviseDestination) { this.deviseDestination = deviseDestination; }

    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }

    public LocalDate getDateActivation() { return dateActivation; }
    public void setDateActivation(LocalDate dateActivation) { this.dateActivation = dateActivation; }
}