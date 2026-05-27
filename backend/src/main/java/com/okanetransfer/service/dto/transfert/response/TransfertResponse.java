package com.okanetransfer.service.dto.transfert.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransfertResponse {

    // ── Identifiants ──────────────────────────────────────────────────────────
    private Long id;
    private String codeRetrait;
    private String numeroReference;

    // ── Expéditeur ────────────────────────────────────────────────────────────
    private String nomExpediteur;
    private String paysExpediteur;
    private String villeExpediteur;
    private String agenceEnvoi;
    private String telephoneExpediteur;

    // ── Bénéficiaire ──────────────────────────────────────────────────────────
    private String nomBeneficiaire;
    private String paysBeneficiaire;
    private String villeBeneficiaire;
    private String telephoneBeneficiaire;

    // ── Montants ──────────────────────────────────────────────────────────────
    private BigDecimal montantEnvoye;
    private BigDecimal montantRecu;
    private BigDecimal frais;
    private BigDecimal partAgence;
    private String deviseReception;
    private BigDecimal tauxChange;

    // ── Statut & Dates ────────────────────────────────────────────────────────
    private String statut;
    private LocalDateTime creeLe;
    private LocalDateTime expireLe;
    private LocalDateTime payeLe;

    // ── Relations ─────────────────────────────────────────────────────────────
    private Long agentId;
    private Long agenceEnvoiId;
    private Long agenceRetraitId;

    public TransfertResponse() {}

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodeRetrait() { return codeRetrait; }
    public void setCodeRetrait(String codeRetrait) { this.codeRetrait = codeRetrait; }

    public String getNumeroReference() { return numeroReference; }
    public void setNumeroReference(String numeroReference) { this.numeroReference = numeroReference; }

    public String getNomExpediteur() { return nomExpediteur; }
    public void setNomExpediteur(String nomExpediteur) { this.nomExpediteur = nomExpediteur; }

    public String getPaysExpediteur() { return paysExpediteur; }
    public void setPaysExpediteur(String paysExpediteur) { this.paysExpediteur = paysExpediteur; }

    public String getVilleExpediteur() { return villeExpediteur; }
    public void setVilleExpediteur(String villeExpediteur) { this.villeExpediteur = villeExpediteur; }

    public String getAgenceEnvoi() { return agenceEnvoi; }
    public void setAgenceEnvoi(String agenceEnvoi) { this.agenceEnvoi = agenceEnvoi; }

    public String getTelephoneExpediteur() { return telephoneExpediteur; }
    public void setTelephoneExpediteur(String telephoneExpediteur) { this.telephoneExpediteur = telephoneExpediteur; }

    public String getNomBeneficiaire() { return nomBeneficiaire; }
    public void setNomBeneficiaire(String nomBeneficiaire) { this.nomBeneficiaire = nomBeneficiaire; }

    public String getPaysBeneficiaire() { return paysBeneficiaire; }
    public void setPaysBeneficiaire(String paysBeneficiaire) { this.paysBeneficiaire = paysBeneficiaire; }

    public String getVilleBeneficiaire() { return villeBeneficiaire; }
    public void setVilleBeneficiaire(String villeBeneficiaire) { this.villeBeneficiaire = villeBeneficiaire; }

    public String getTelephoneBeneficiaire() { return telephoneBeneficiaire; }
    public void setTelephoneBeneficiaire(String telephoneBeneficiaire) { this.telephoneBeneficiaire = telephoneBeneficiaire; }

    public BigDecimal getMontantEnvoye() { return montantEnvoye; }
    public void setMontantEnvoye(BigDecimal montantEnvoye) { this.montantEnvoye = montantEnvoye; }

    public BigDecimal getMontantRecu() { return montantRecu; }
    public void setMontantRecu(BigDecimal montantRecu) { this.montantRecu = montantRecu; }

    public BigDecimal getFrais() { return frais; }
    public void setFrais(BigDecimal frais) { this.frais = frais; }

    public BigDecimal getPartAgence() { return partAgence; }
    public void setPartAgence(BigDecimal partAgence) { this.partAgence = partAgence; }

    public String getDeviseReception() { return deviseReception; }
    public void setDeviseReception(String deviseReception) { this.deviseReception = deviseReception; }

    public BigDecimal getTauxChange() { return tauxChange; }
    public void setTauxChange(BigDecimal tauxChange) { this.tauxChange = tauxChange; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public LocalDateTime getCreeLe() { return creeLe; }
    public void setCreeLe(LocalDateTime creeLe) { this.creeLe = creeLe; }

    public LocalDateTime getExpireLe() { return expireLe; }
    public void setExpireLe(LocalDateTime expireLe) { this.expireLe = expireLe; }

    public LocalDateTime getPayeLe() { return payeLe; }
    public void setPayeLe(LocalDateTime payeLe) { this.payeLe = payeLe; }

    public Long getAgentId() { return agentId; }
    public void setAgentId(Long agentId) { this.agentId = agentId; }

    public Long getAgenceEnvoiId() { return agenceEnvoiId; }
    public void setAgenceEnvoiId(Long agenceEnvoiId) { this.agenceEnvoiId = agenceEnvoiId; }

    public Long getAgenceRetraitId() { return agenceRetraitId; }
    public void setAgenceRetraitId(Long agenceRetraitId) { this.agenceRetraitId = agenceRetraitId; }
}