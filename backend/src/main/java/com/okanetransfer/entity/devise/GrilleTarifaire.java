package com.okanetransfer.entity.devise;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "grilles_tarifaires")
public class GrilleTarifaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "corridor_id", nullable = false)
    private Corridor corridor;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montantMin;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montantMax;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal fraisFixe;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal fraisPourcentage = BigDecimal.ZERO;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal partAgence;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal partCentrale;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Corridor getCorridor() {
        return corridor;
    }

    public void setCorridor(Corridor corridor) {
        this.corridor = corridor;
    }

    public BigDecimal getMontantMin() {
        return montantMin;
    }

    public void setMontantMin(BigDecimal montantMin) {
        this.montantMin = montantMin;
    }

    public BigDecimal getMontantMax() {
        return montantMax;
    }

    public void setMontantMax(BigDecimal montantMax) {
        this.montantMax = montantMax;
    }

    public BigDecimal getFraisFixe() {
        return fraisFixe;
    }

    public void setFraisFixe(BigDecimal fraisFixe) {
        this.fraisFixe = fraisFixe;
    }

    public BigDecimal getFraisPourcentage() {
        return fraisPourcentage;
    }

    public void setFraisPourcentage(BigDecimal fraisPourcentage) {
        this.fraisPourcentage = fraisPourcentage;
    }

    public BigDecimal getPartAgence() {
        return partAgence;
    }

    public void setPartAgence(BigDecimal partAgence) {
        this.partAgence = partAgence;
    }

    public BigDecimal getPartCentrale() {
        return partCentrale;
    }

    public void setPartCentrale(BigDecimal partCentrale) {
        this.partCentrale = partCentrale;
    }
}