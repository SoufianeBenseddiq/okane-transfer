package com.okanetransfer.service.dto.mobilemoney.response;

import java.time.LocalDateTime;

public class TransfertMobileMoneyResponse {

    private Long id;

    private Long transfertId;

    private String operateur;

    private String numeroCible;

    private String statutMobile;

    private String referenceOperateur;

    private LocalDateTime envoyeLe;

    public TransfertMobileMoneyResponse() {
    }

    public TransfertMobileMoneyResponse(Long id, Long transfertId, String operateur, String numeroCible, String statutMobile, String referenceOperateur, LocalDateTime envoyeLe) {
        this.id = id;
        this.transfertId = transfertId;
        this.operateur = operateur;
        this.numeroCible = numeroCible;
        this.statutMobile = statutMobile;
        this.referenceOperateur = referenceOperateur;
        this.envoyeLe = envoyeLe;
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

    public LocalDateTime getEnvoyeLe() {
        return envoyeLe;
    }

    public void setEnvoyeLe(LocalDateTime envoyeLe) {
        this.envoyeLe = envoyeLe;
    }
}
