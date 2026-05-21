package com.okanetransfer.service.dto.user.request;

import com.okanetransfer.shared.enums.RoleUtilisateur;
import jakarta.validation.constraints.*;

public class CreateUserRequest {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format email invalide")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[0-9]).+$",
        message = "Le mot de passe doit contenir au moins 1 majuscule et 1 chiffre"
    )
    private String motDePasse;

    @NotBlank(message = "Le téléphone est obligatoire")
    @Pattern(
        regexp = "^\\+?[0-9]{8,15}$",
        message = "Format téléphone invalide (ex: +212661234567)"
    )
    private String telephone;

    @NotBlank(message = "Le pays est obligatoire")
    private String pays;

    @NotNull(message = "Le rôle est obligatoire")
    private RoleUtilisateur role;

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getPays() { return pays; }
    public void setPays(String pays) { this.pays = pays; }

    public RoleUtilisateur getRole() { return role; }
    public void setRole(RoleUtilisateur role) { this.role = role; }
}
