package com.okanetransfer.service.dto.mobilemoney.request;

public class UpdateTransfertMobileMoneyRequest {

    private String numeroCible;

    private String statutMobile;

    private String referenceOperateur;

    public UpdateTransfertMobileMoneyRequest() {
    }

    public UpdateTransfertMobileMoneyRequest(String numeroCible, String statutMobile, String referenceOperateur) {
        this.numeroCible = numeroCible;
        this.statutMobile = statutMobile;
        this.referenceOperateur = referenceOperateur;
    }

    public String getNumeroCible() {
        return numeroCible;
    }

    public void setNumeroCible(String numeroCible) {
        this.numeroCible = numeroCible;
    }

    public String getStatutMobile() {
        return statutMobile;
    }

    public void setStatutMobile(String statutMobile) {
        this.statutMobile = statutMobile;
    }

    public String getReferenceOperateur() {
        return referenceOperateur;
    }

    public void setReferenceOperateur(String referenceOperateur) {
        this.referenceOperateur = referenceOperateur;
    }
}
