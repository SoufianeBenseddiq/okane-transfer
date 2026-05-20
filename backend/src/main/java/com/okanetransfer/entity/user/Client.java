package com.okanetransfer.entity.user;

import jakarta.persistence.*;

@Entity
@Table(name = "clients")
@DiscriminatorValue("CLIENT")
public class Client extends Utilisateur {

    @Column(nullable = false)
    private Boolean twoFactorActive = false;

    // getters / setters
}