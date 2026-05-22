package com.okanetransfer.service.dto.aml.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DeclarationResponse {

    private Long id;
    private Long transfertId;
    private Long regleId;
    private String motif;
    private BigDecimal montantTotal;
    private LocalDateTime genereLe;
    private Boolean traitee;

    public DeclarationResponse() {
    }

    public DeclarationResponse(Long id, Long transfertId, Long regleId, String motif,
                              BigDecimal montantTotal, LocalDateTime genereLe, Boolean traitee) {
        this.id = id;
        this.transfertId = transfertId;
        this.regleId = regleId;
        this.motif = motif;
        this.montantTotal = montantTotal;
        this.genereLe = genereLe;
        this.traitee = traitee;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTransfertId() {
        return transfertId;
    }

    public void setTransfertId(Long transfertId) {
        this.transfertId = transfertId;
    }

    public Long getRegleId() {
        return regleId;
    }

    public void setRegleId(Long regleId) {
        this.regleId = regleId;
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
