package com.okanetransfer.service.dto.user.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO pour modifier son propre profil (CLIENT).
 * Endpoint : PUT /api/users/profil
 *
 * Tous les champs sont OPTIONNELS.
 * Seuls les champs non-null sont mis à jour (sémantique PATCH).
 * Ex : envoyer uniquement { "telephone": "+212698765432" } → seul le tel est modifié.
 */
public class UpdateProfilRequest {

    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    private String nom;

    @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
    private String prenom;

    @Pattern(
        regexp = "^\\+?[0-9]{8,15}$",
        message = "Format téléphone invalide"
    )
    private String telephone;

    @Size(min = 2, max = 60, message = "Le pays doit contenir entre 2 et 60 caractères")
    private String pays;

    private String email;

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getPays() { return pays; }
    public void setPays(String pays) { this.pays = pays; }

    public String getEmail() { return email;}
    public void setEmail(String email) { this.email = email;}
}
