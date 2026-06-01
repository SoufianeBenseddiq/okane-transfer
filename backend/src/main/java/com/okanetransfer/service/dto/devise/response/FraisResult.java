package com.okanetransfer.service.dto.devise.response;

import java.math.BigDecimal;

public class FraisResult {

    private BigDecimal montantFrais;
    private BigDecimal partAgence;
    private BigDecimal partCentrale;
    private BigDecimal montantRecu;
    private BigDecimal taux;
    private Integer delaiMin;
    private Long grilleTarifaireId;

    // getters / setters
    public BigDecimal getMontantFrais() { return montantFrais; }
    public void setMontantFrais(BigDecimal montantFrais) { this.montantFrais = montantFrais; }

    public BigDecimal getPartAgence() { return partAgence; }
    public void setPartAgence(BigDecimal partAgence) { this.partAgence = partAgence; }

    public BigDecimal getPartCentrale() { return partCentrale; }
    public void setPartCentrale(BigDecimal partCentrale) { this.partCentrale = partCentrale; }

    public BigDecimal getMontantRecu() { return montantRecu; }
    public void setMontantRecu(BigDecimal montantRecu) { this.montantRecu = montantRecu; }

    public BigDecimal getTaux() { return taux; }
    public void setTaux(BigDecimal taux) { this.taux = taux; }

    public Integer getDelaiMin() { return delaiMin; }
    public void setDelaiMin(Integer delaiMin) { this.delaiMin = delaiMin; }

    public Long getGrilleTarifaireId() { return grilleTarifaireId; }
    public void setGrilleTarifaireId(Long grilleTarifaireId) { this.grilleTarifaireId = grilleTarifaireId; }
}