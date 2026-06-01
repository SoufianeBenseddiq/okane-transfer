package com.okanetransfer.service.dto.transfert.response;

import java.math.BigDecimal;

public class TransfertStatsResponse {

    private long total;
    private long enAttente;
    private long payes;
    private long annules;
    private BigDecimal montantTotalEnvoye = BigDecimal.ZERO;
    private BigDecimal montantTotalPaye = BigDecimal.ZERO;
    private BigDecimal fraisTotal = BigDecimal.ZERO;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getEnAttente() {
        return enAttente;
    }

    public void setEnAttente(long enAttente) {
        this.enAttente = enAttente;
    }

    public long getPayes() {
        return payes;
    }

    public void setPayes(long payes) {
        this.payes = payes;
    }

    public long getAnnules() {
        return annules;
    }

    public void setAnnules(long annules) {
        this.annules = annules;
    }

    public BigDecimal getMontantTotalEnvoye() {
        return montantTotalEnvoye;
    }

    public void setMontantTotalEnvoye(BigDecimal montantTotalEnvoye) {
        this.montantTotalEnvoye = montantTotalEnvoye;
    }

    public BigDecimal getMontantTotalPaye() {
        return montantTotalPaye;
    }

    public void setMontantTotalPaye(BigDecimal montantTotalPaye) {
        this.montantTotalPaye = montantTotalPaye;
    }

    public BigDecimal getFraisTotal() {
        return fraisTotal;
    }

    public void setFraisTotal(BigDecimal fraisTotal) {
        this.fraisTotal = fraisTotal;
    }
}
