package com.okanetransfer.service.dto.devise.response;

import java.time.LocalDate;

public class CorridorResponse {

    private Long id;
    private String deviseSource;
    private String deviseDestination;
    private Boolean actif;
    private LocalDate dateActivation;
    private PaysResponse paysSource;
    private PaysResponse paysDestination;

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

    public PaysResponse getPaysSource() { return paysSource; }
    public void setPaysSource(PaysResponse paysSource) { this.paysSource = paysSource; }

    public PaysResponse getPaysDestination() { return paysDestination; }
    public void setPaysDestination(PaysResponse paysDestination) { this.paysDestination = paysDestination; }
}
