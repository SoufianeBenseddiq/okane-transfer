package com.okanetransfer.service.dto.transfert.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransfertResponse {

    private Long id;

    private String codeRetrait;

    private String numeroReference;

    private BigDecimal montantEnvoye;

    private BigDecimal montantRecu;

    private BigDecimal frais;

    private String statut;

    private LocalDateTime creeLe;

    private LocalDateTime payeLe;

    private Long agentId;

    private Long agenceEnvoiId;

    private Long agenceRetraitId;

    public TransfertResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodeRetrait() {
        return codeRetrait;
    }

    public void setCodeRetrait(String codeRetrait) {
        this.codeRetrait = codeRetrait;
    }

    public String getNumeroReference() {
        return numeroReference;
    }

    public void setNumeroReference(String numeroReference) {
        this.numeroReference = numeroReference;
    }

    public BigDecimal getMontantEnvoye() {
        return montantEnvoye;
    }

    public void setMontantEnvoye(BigDecimal montantEnvoye) {
        this.montantEnvoye = montantEnvoye;
    }

    public BigDecimal getMontantRecu() {
        return montantRecu;
    }

    public void setMontantRecu(BigDecimal montantRecu) {
        this.montantRecu = montantRecu;
    }

    public BigDecimal getFrais() {
        return frais;
    }

    public void setFrais(BigDecimal frais) {
        this.frais = frais;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public LocalDateTime getCreeLe() {
        return creeLe;
    }

    public void setCreeLe(LocalDateTime creeLe) {
        this.creeLe = creeLe;
    }

    public LocalDateTime getPayeLe() {
        return payeLe;
    }

    public void setPayeLe(LocalDateTime payeLe) {
        this.payeLe = payeLe;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public Long getAgenceEnvoiId() {
        return agenceEnvoiId;
    }

    public void setAgenceEnvoiId(Long agenceEnvoiId) {
        this.agenceEnvoiId = agenceEnvoiId;
    }

    public Long getAgenceRetraitId() {
        return agenceRetraitId;
    }

    public void setAgenceRetraitId(Long agenceRetraitId) {
        this.agenceRetraitId = agenceRetraitId;
    }
}
