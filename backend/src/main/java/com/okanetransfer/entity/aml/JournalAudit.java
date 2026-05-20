package com.okanetransfer.entity.aml;

import com.okanetransfer.entity.user.Utilisateur;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "journal_audit")
public class JournalAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acteur_id", nullable = false)
    private Utilisateur acteur;

    @Column(nullable = false)
    private String action;          // ex: "PAIEMENT_TRANSFERT"

    @Column(nullable = false)
    private String entiteCible;     // ex: "Transfert"

    @Column(nullable = false)
    private Long idCible;           // ex: 892

    @Column(columnDefinition = "TEXT")
    private String detailAvant;     // JSON état avant

    @Column(columnDefinition = "TEXT")
    private String detailApres;     // JSON état après

    @Column(nullable = false)
    private LocalDateTime dateHeure;

    private String ipAdresse;

    @PrePersist
    public void prePersist() {
        this.dateHeure = LocalDateTime.now();
    }

    // getters / setters
}