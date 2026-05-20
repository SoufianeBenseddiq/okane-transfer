package com.okanetransfer.entity.devise;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "historique_taux")
public class HistoriqueTaux {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "devise_id", nullable = false)
    private Devise devise;

    @Column(nullable = false, precision = 15, scale = 6)
    private BigDecimal ancienTaux;

    @Column(nullable = false, precision = 15, scale = 6)
    private BigDecimal nouveauTaux;

    @Column(nullable = false)
    private LocalDateTime date;

    private String source;   // "MANUEL" ou "API"

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Devise getDevise() {
        return devise;
    }

    public void setDevise(Devise devise) {
        this.devise = devise;
    }

    public BigDecimal getAncienTaux() {
        return ancienTaux;
    }

    public void setAncienTaux(BigDecimal ancienTaux) {
        this.ancienTaux = ancienTaux;
    }

    public BigDecimal getNouveauTaux() {
        return nouveauTaux;
    }

    public void setNouveauTaux(BigDecimal nouveauTaux) {
        this.nouveauTaux = nouveauTaux;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}