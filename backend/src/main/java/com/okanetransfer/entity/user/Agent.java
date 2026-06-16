package com.okanetransfer.entity.user;

import com.okanetransfer.entity.agence.Agence;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "agents")
@DiscriminatorValue("AGENT")
public class Agent extends Utilisateur {

    @ManyToOne
    @JoinColumn(name = "agence_id")
    private Agence agence;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal soldeCaisse = BigDecimal.ZERO;

    @Column
    private LocalDate dateOuverture;

    public Agence getAgence() {
        return agence;
    }

    public void setAgence(Agence agence) {
        this.agence = agence;
    }

    public BigDecimal getSoldeCaisse() {
        return soldeCaisse;
    }

    public void setSoldeCaisse(BigDecimal soldeCaisse) {
        this.soldeCaisse = soldeCaisse;
    }

    public LocalDate getDateOuverture() {
        return dateOuverture;
    }

    public void setDateOuverture(LocalDate dateOuverture) {
        this.dateOuverture = dateOuverture;
    }
}