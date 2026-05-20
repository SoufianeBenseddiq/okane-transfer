package com.okanetransfer.entity.aml;

import com.okanetransfer.entity.transfert.Transfert;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "declarations_soupcon")
public class DeclarationSoupcon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transfert_id", nullable = false)
    private Transfert transfert;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "regle_id", nullable = false)
    private RegleAML regle;

    @Column(nullable = false)
    private String motif;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montantTotal;

    @Column(nullable = false)
    private LocalDateTime genereLe;

    @Column(nullable = false)
    private Boolean traitee = false;

    @PrePersist
    public void prePersist() {
        this.genereLe = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Transfert getTransfert() {
        return transfert;
    }

    public void setTransfert(Transfert transfert) {
        this.transfert = transfert;
    }

    public RegleAML getRegle() {
        return regle;
    }

    public void setRegle(RegleAML regle) {
        this.regle = regle;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public BigDecimal getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(BigDecimal montantTotal) {
        this.montantTotal = montantTotal;
    }

    public LocalDateTime getGenereLe() {
        return genereLe;
    }

    public void setGenereLe(LocalDateTime genereLe) {
        this.genereLe = genereLe;
    }

    public Boolean getTraitee() {
        return traitee;
    }

    public void setTraitee(Boolean traitee) {
        this.traitee = traitee;
    }
}