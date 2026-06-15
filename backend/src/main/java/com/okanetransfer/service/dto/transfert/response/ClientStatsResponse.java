package com.okanetransfer.service.dto.transfert.response;

import java.math.BigDecimal;
import java.util.List;

public class ClientStatsResponse {

    private BigDecimal sentThisMonth;
    private String currency;
    private BigDecimal changeVsLastMonth;
    private Integer activeBeneficiariesCount;
    private List<BeneficiaireResponse> beneficiaries;

    public BigDecimal getSentThisMonth() {
        return sentThisMonth;
    }

    public void setSentThisMonth(BigDecimal sentThisMonth) {
        this.sentThisMonth = sentThisMonth;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getChangeVsLastMonth() {
        return changeVsLastMonth;
    }

    public void setChangeVsLastMonth(BigDecimal changeVsLastMonth) {
        this.changeVsLastMonth = changeVsLastMonth;
    }

    public Integer getActiveBeneficiariesCount() {
        return activeBeneficiariesCount;
    }

    public void setActiveBeneficiariesCount(Integer activeBeneficiariesCount) {
        this.activeBeneficiariesCount = activeBeneficiariesCount;
    }

    public List<BeneficiaireResponse> getBeneficiaries() {
        return beneficiaries;
    }

    public void setBeneficiaries(List<BeneficiaireResponse> beneficiaries) {
        this.beneficiaries = beneficiaries;
    }
}
