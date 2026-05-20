package com.okanetransfer.entity.user;

import jakarta.persistence.*;

@Entity
@Table(name = "administrateurs")
@DiscriminatorValue("ADMIN")
public class Administrateur extends Utilisateur {
    // pas de champs supplémentaires
    // son pouvoir vient uniquement de son ROLE_ADMIN dans Spring Security
}