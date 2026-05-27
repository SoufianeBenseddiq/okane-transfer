package com.okanetransfer.entity.transfert;

import com.okanetransfer.entity.agence.Agence;
import com.okanetransfer.entity.caisse.CaisseOperation;
import com.okanetransfer.entity.devise.Corridor;
import com.okanetransfer.entity.devise.GrilleTarifaire;
import com.okanetransfer.entity.user.Agent;
import com.okanetransfer.shared.enums.StatutTransfert;
import com.okanetransfer.shared.enums.TypePiece;
import com.okanetransfer.shared.util.CryptoConverter;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "transferts")
public class Transfert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codeRetrait;         // ex: "K7X2M9QA"

    @Column(nullable = false, unique = true)
    private String numeroReference;     // ex: "TRF-20260517-00892"

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montantEnvoye;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montantRecu;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal frais;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutTransfert statut = StatutTransfert.EN_ATTENTE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expediteur_id")
    private Expediteur expediteur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beneficiaire_id", nullable = false)
    private Beneficiaire beneficiaire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_saisie_id")
    private Agent agentSaisie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agence_envoi_id")
    private Agence agenceEnvoi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agence_retrait_id")
    private Agence agenceRetrait;       // null jusqu'au paiement

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "corridor_id")
    private Corridor corridor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grille_tarifaire_id")
    private GrilleTarifaire grilleTarifaire;

    @OneToMany(mappedBy = "transfert", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CaisseOperation> operationsCaisse = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime creeLe;

    private LocalDateTime payeLe;       // null jusqu'au paiement

    @Enumerated(EnumType.STRING)
    @Column
    private TypePiece typePieceBeneficiaire;

    @Convert(converter = CryptoConverter.class)
    @Column
    private String numeroPieceBeneficiaire;

    @PrePersist
    public void prePersist() {
        this.creeLe = LocalDateTime.now();
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

    public StatutTransfert getStatut() {
        return statut;
    }

    public void setStatut(StatutTransfert statut) {
        this.statut = statut;
    }

    public Expediteur getExpediteur() {
        return expediteur;
    }

    public void setExpediteur(Expediteur expediteur) {
        this.expediteur = expediteur;
    }

    public Beneficiaire getBeneficiaire() {
        return beneficiaire;
    }

    public void setBeneficiaire(Beneficiaire beneficiaire) {
        this.beneficiaire = beneficiaire;
    }

    public Agent getAgentSaisie() {
        return agentSaisie;
    }

    public void setAgentSaisie(Agent agentSaisie) {
        this.agentSaisie = agentSaisie;
    }

    public Agence getAgenceEnvoi() {
        return agenceEnvoi;
    }

    public void setAgenceEnvoi(Agence agenceEnvoi) {
        this.agenceEnvoi = agenceEnvoi;
    }

    public Agence getAgenceRetrait() {
        return agenceRetrait;
    }

    public void setAgenceRetrait(Agence agenceRetrait) {
        this.agenceRetrait = agenceRetrait;
    }

    public Corridor getCorridor() {
        return corridor;
    }

    public void setCorridor(Corridor corridor) {
        this.corridor = corridor;
    }

    public GrilleTarifaire getGrilleTarifaire() {
        return grilleTarifaire;
    }

    public void setGrilleTarifaire(GrilleTarifaire grilleTarifaire) {
        this.grilleTarifaire = grilleTarifaire;
    }

    public List<CaisseOperation> getOperationsCaisse() {
        return operationsCaisse;
    }

    public void setOperationsCaisse(List<CaisseOperation> operationsCaisse) {
        this.operationsCaisse = operationsCaisse;
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
<<<<<<< HEAD

    public TypePiece getTypePieceBeneficiaire() {
        return typePieceBeneficiaire;
    }

    public void setTypePieceBeneficiaire(TypePiece typePieceBeneficiaire) {
        this.typePieceBeneficiaire = typePieceBeneficiaire;
    }

    public String getNumeroPieceBeneficiaire() {
        return numeroPieceBeneficiaire;
    }

    public void setNumeroPieceBeneficiaire(String numeroPieceBeneficiaire) {
        this.numeroPieceBeneficiaire = numeroPieceBeneficiaire;
    }
}
=======
}
>>>>>>> 78c7d10560abb4927f42ed9e093d2c396875add6
