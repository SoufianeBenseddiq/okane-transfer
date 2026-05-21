package com.okanetransfer.entity.user;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clients")
@DiscriminatorValue("CLIENT")
public class Client extends Utilisateur {

    @Column(nullable = false)
    private Boolean twoFactorActive = false;

    // ex: CIN + Passeport
    @OneToMany(mappedBy = "client",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<PieceIdentite> piecesIdentites = new ArrayList<>();

    // getters / setters
}