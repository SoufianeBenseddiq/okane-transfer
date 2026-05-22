package com.okanetransfer.service.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO reçu à l'étape 1 de la connexion.
 * Endpoint : POST /api/auth/login
 */
public class LoginRequest {

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format email invalide")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String motDePasse;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
}
