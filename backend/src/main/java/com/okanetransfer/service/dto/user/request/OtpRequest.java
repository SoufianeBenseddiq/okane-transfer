package com.okanetransfer.service.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO reçu à l'étape 2 de la connexion (vérification OTP 2FA).
 * Endpoint : POST /api/auth/verify-otp
 */
public class OtpRequest {

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format email invalide")
    private String email;

    @NotBlank(message = "Le téléphone est obligatoire")
    private String telephone;

    // exactement 6 chiffres — correspond au code envoyé par SMS
    @NotBlank(message = "Le code OTP est obligatoire")
    @Pattern(regexp = "^[0-9]{6}$", message = "Le code OTP doit contenir exactement 6 chiffres")
    private String code;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
