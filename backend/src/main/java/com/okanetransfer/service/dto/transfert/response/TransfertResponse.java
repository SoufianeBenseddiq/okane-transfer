package com.okanetransfer.service.dto.transfert.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransfertResponse {

<<<<<<< HEAD
    // ── Identifiants ──────────────────────────────────────────────────────────
=======
    private Long id;

>>>>>>> 78c7d10560abb4927f42ed9e093d2c396875add6
    private String codeRetrait;
    private String numeroReference;

    // ── Expéditeur ────────────────────────────────────────────────────────────
    private String nomExpediteur;        // ex: "Mohamed Alaoui"
    private String paysExpediteur;      // ex: "MA"
    private String villeExpediteur;     // ex: "Casablanca, Maroc"
    private String agenceEnvoi;         // ex: "Agence Casablanca Maârif"

    // ── Bénéficiaire ──────────────────────────────────────────────────────────
    private String nomBeneficiaire;     // ex: "Aminata Diallo"
    private String paysBeneficiaire;    // ex: "SN"
    private String villeBeneficiaire;   // ex: "Dakar"
    private String telephoneBeneficiaire; // ex: "+221 77 412 65 09"

    // ── Montants ──────────────────────────────────────────────────────────────
    private BigDecimal montantEnvoye;   // en MAD
    private BigDecimal montantRecu;     // dans la devise locale du bénéficiaire
    private BigDecimal frais;
    private String deviseReception;     // ex: "XOF"
    private BigDecimal tauxChange;      // ex: 107.7

    // ── Statut & Dates ────────────────────────────────────────────────────────
    private String statut;
    private LocalDateTime creeLe;
    private LocalDateTime expireLe;

<<<<<<< HEAD
    public TransfertResponse() {}

    // ── Getters / Setters ─────────────────────────────────────────────────────
=======
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
>>>>>>> 78c7d10560abb4927f42ed9e093d2c396875add6

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
}

//package com.okanetransfer.service.dto.transfert.response;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//public class TransfertResponse {
//
//    private String codeRetrait;
//
//    private String numeroReference;
//
//    private BigDecimal montantEnvoye;
//
//    private BigDecimal montantRecu;
//
//    private BigDecimal frais;
//
//    private String statut;
//
//    private LocalDateTime creeLe;
//
//    public TransfertResponse() {
//    }
//
//    public String getCodeRetrait() {
//        return codeRetrait;
//    }
//
//    public void setCodeRetrait(String codeRetrait) {
//        this.codeRetrait = codeRetrait;
//    }
//
//    public String getNumeroReference() {
//        return numeroReference;
//    }
//
//    public void setNumeroReference(String numeroReference) {
//        this.numeroReference = numeroReference;
//    }
//
//    public BigDecimal getMontantEnvoye() {
//        return montantEnvoye;
//    }
//
//    public void setMontantEnvoye(BigDecimal montantEnvoye) {
//        this.montantEnvoye = montantEnvoye;
//    }
//
//    public BigDecimal getMontantRecu() {
//        return montantRecu;
//    }
//
//    public void setMontantRecu(BigDecimal montantRecu) {
//        this.montantRecu = montantRecu;
//    }
//
//    public BigDecimal getFrais() {
//        return frais;
//    }
//
//    public void setFrais(BigDecimal frais) {
//        this.frais = frais;
//    }
//
//    public String getStatut() {
//        return statut;
//    }
//
//    public void setStatut(String statut) {
//        this.statut = statut;
//    }
//
//    public LocalDateTime getCreeLe() {
//        return creeLe;
//    }
//
//    public void setCreeLe(LocalDateTime creeLe) {
//        this.creeLe = creeLe;
//    }
//}


<<<<<<< HEAD
=======
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
>>>>>>> 78c7d10560abb4927f42ed9e093d2c396875add6
