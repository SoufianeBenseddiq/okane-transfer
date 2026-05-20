package com.okanetransfer.entity.mobilemoney;

import com.okanetransfer.entity.transfert.Transfert;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transferts_mobile_money")
public class TransfertMobileMoney {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // relation 1-1 avec le transfert classique
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transfert_id", nullable = false, unique = true)
    private Transfert transfert;

    @Column(nullable = false)
    private String operateur;           // "ORANGE_MONEY", "WAVE", "M_PESA"

    @Column(nullable = false)
    private String numeroCible;         // numéro de téléphone du compte mobile money

    @Column(nullable = false)
    private String statutMobile;        // "EN_ATTENTE", "ENVOYE", "CONFIRME", "ECHEC"

    // référence retournée par l'opérateur (pour réconciliation)
    private String referenceOperateur;

    private LocalDateTime envoyeLe;

    @PrePersist
    public void prePersist() {
        this.envoyeLe = LocalDateTime.now();
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