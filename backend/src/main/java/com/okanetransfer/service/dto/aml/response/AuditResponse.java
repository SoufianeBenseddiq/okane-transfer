package com.okanetransfer.service.dto.aml.response;

import java.time.LocalDateTime;

public class AuditResponse {

    private Long id;
    private Long acteurId;
    private String action;
    private String entiteCible;
    private Long idCible;
    private String detailAvant;
    private String detailApres;
    private LocalDateTime dateHeure;
    private String ipAdresse;

    public AuditResponse() {
    }

    public AuditResponse(Long id, Long acteurId, String action, String entiteCible,
                        Long idCible, String detailAvant, String detailApres,
                        LocalDateTime dateHeure, String ipAdresse) {
        this.id = id;
        this.acteurId = acteurId;
        this.action = action;
        this.entiteCible = entiteCible;
        this.idCible = idCible;
        this.detailAvant = detailAvant;
        this.detailApres = detailApres;
        this.dateHeure = dateHeure;
        this.ipAdresse = ipAdresse;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getActeurId() {
        return acteurId;
    }

    public void setActeurId(Long acteurId) {
        this.acteurId = acteurId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEntiteCible() {
        return entiteCible;
    }

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
