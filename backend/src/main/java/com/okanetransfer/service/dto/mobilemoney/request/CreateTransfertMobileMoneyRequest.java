package com.okanetransfer.service.dto.mobilemoney.request;

public class CreateTransfertMobileMoneyRequest {

    private Long transfertId;

    private String operateur;

    private String numeroCible;

    public CreateTransfertMobileMoneyRequest() {
    }

    public CreateTransfertMobileMoneyRequest(Long transfertId, String operateur, String numeroCible) {
        this.transfertId = transfertId;
        this.operateur = operateur;
        this.numeroCible = numeroCible;
    }

    public Long getTransfertId() {
        return transfertId;
    }

    public void setTransfertId(Long transfertId) {
        this.transfertId = transfertId;
    }

    public String getOperateur() {
        return operateur;
    }

    public void setOperateur(String operateur) {
        this.operateur = operateur;
    }

    public String getNumeroCible() {
        return numeroCible;
    }

    public void setNumeroCible(String numeroCible) {
        this.numeroCible = numeroCible;
    }
}
