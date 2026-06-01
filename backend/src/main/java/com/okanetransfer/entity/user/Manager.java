package com.okanetransfer.entity.user;

import com.okanetransfer.entity.agence.Agence;
import jakarta.persistence.*;

@Entity
@Table(name = "managers")
@DiscriminatorValue("MANAGER")
public class Manager extends Utilisateur {

    @OneToOne
    @JoinColumn(name = "agence_id")
    private Agence agence;

    public Agence getAgence() { return agence; }
    public void setAgence(Agence agence) { this.agence = agence; }
}
