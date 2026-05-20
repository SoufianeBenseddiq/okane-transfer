package com.okanetransfer.entity.caisse;

import com.okanetransfer.entity.user.Agent;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "clotures_caisse")
public class ClotureCaisse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false)
    private Agent agent;

    @Column(nullable = false)
    private LocalDate date;

    // calculé automatiquement par le système
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal soldeTheorique;

    // saisi manuellement par l'agent
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal soldeSaisi;

    // soldeSaisi - soldeTheorique (doit être 0)
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal ecart;

    @Column(nullable = false)
    private Boolean ecartSignale = false;

    private LocalDateTime clotureeLe;

    @PrePersist
    public void prePersist() {
        this.clotureeLe = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getSoldeTheorique() {
        return soldeTheorique;
    }

    public void setSoldeTheorique(BigDecimal soldeTheorique) {
        this.soldeTheorique = soldeTheorique;
    }

    public BigDecimal getSoldeSaisi() {
        return soldeSaisi;
    }

    public void setSoldeSaisi(BigDecimal soldeSaisi) {
        this.soldeSaisi = soldeSaisi;
    }

    public BigDecimal getEcart() {
        return ecart;
    }

    public void setEcart(BigDecimal ecart) {
        this.ecart = ecart;
    }

    public Boolean getEcartSignale() {
        return ecartSignale;
    }

    public void setEcartSignale(Boolean ecartSignale) {
        this.ecartSignale = ecartSignale;
    }

    public LocalDateTime getClotureeLe() {
        return clotureeLe;
    }

    public void setClotureeLe(LocalDateTime clotureeLe) {
        this.clotureeLe = clotureeLe;
    }
}