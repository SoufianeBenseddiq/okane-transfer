package com.okanetransfer.service.dto.caisse.response;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ClotureCaisseResponse {

    private Long id;
    private Long agentId;
    private String agentNom;
    private LocalDate date;
    private BigDecimal soldeTheorique;
    private BigDecimal soldeSaisi;
    private BigDecimal ecart;
    private Boolean ecartSignale;
    private LocalDateTime clotureeLe;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAgentId() { return agentId; }
    public void setAgentId(Long agentId) { this.agentId = agentId; }

    public String getAgentNom() { return agentNom; }
    public void setAgentNom(String agentNom) { this.agentNom = agentNom; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public BigDecimal getSoldeTheorique() { return soldeTheorique; }
    public void setSoldeTheorique(BigDecimal soldeTheorique) { this.soldeTheorique = soldeTheorique; }

    public BigDecimal getSoldeSaisi() { return soldeSaisi; }
    public void setSoldeSaisi(BigDecimal soldeSaisi) { this.soldeSaisi = soldeSaisi; }

    public BigDecimal getEcart() { return ecart; }
    public void setEcart(BigDecimal ecart) { this.ecart = ecart; }

    public Boolean getEcartSignale() { return ecartSignale; }
    public void setEcartSignale(Boolean ecartSignale) { this.ecartSignale = ecartSignale; }

    public LocalDateTime getClotureeLe() { return clotureeLe; }
    public void setClotureeLe(LocalDateTime clotureeLe) { this.clotureeLe = clotureeLe; }
}