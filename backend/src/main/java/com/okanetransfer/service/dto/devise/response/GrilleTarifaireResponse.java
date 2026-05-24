package com.okanetransfer.service.dto.devise.response;

import java.math.BigDecimal;

public class GrilleTarifaireResponse {

    private Long id;
    private Long corridorId;
    private BigDecimal montantMin;
    private BigDecimal montantMax;
    private BigDecimal fraisFixe;
    private BigDecimal fraisPourcentage;
    private BigDecimal partAgence;
    private BigDecimal partCentrale;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCorridorId() { return corridorId; }
    public void setCorridorId(Long corridorId) { this.corridorId = corridorId; }

    public BigDecimal getMontantMin() { return montantMin; }
    public void setMontantMin(BigDecimal montantMin) { this.montantMin = montantMin; }

    public BigDecimal getMontantMax() { return montantMax; }
    public void setMontantMax(BigDecimal montantMax) { this.montantMax = montantMax; }

    public BigDecimal getFraisFixe() { return fraisFixe; }
    public void setFraisFixe(BigDecimal fraisFixe) { this.fraisFixe = fraisFixe; }

    public BigDecimal getFraisPourcentage() { return fraisPourcentage; }
    public void setFraisPourcentage(BigDecimal fraisPourcentage) { this.fraisPourcentage = fraisPourcentage; }

    public BigDecimal getPartAgence() { return partAgence; }
    public void setPartAgence(BigDecimal partAgence) { this.partAgence = partAgence; }

    public BigDecimal getPartCentrale() { return partCentrale; }
    public void setPartCentrale(BigDecimal partCentrale) { this.partCentrale = partCentrale; }
}
