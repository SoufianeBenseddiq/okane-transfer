package com.okanetransfer.service.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

/**
 * DTO pour ajouter une pièce d'identité à un compte client.
 * Endpoint : POST /api/users/pieces
 *
 * Le numéro sera chiffré AES-256 automatiquement par CryptoConverter
 * au moment de la persistance JPA — aucun traitement manuel dans le service.
 */
public class PieceIdentiteRequest {

    // numéro brut — sera chiffré AES-256 par CryptoConverter avant insertion en base
    @NotBlank(message = "Le numéro de pièce est obligatoire")
    private String numero;

    // valeurs acceptées : CIN, PASSEPORT, CARTE_SEJOUR, PERMIS
    @NotBlank(message = "Le type de pièce est obligatoire")
    @Pattern(
        regexp = "^(CIN|PASSEPORT|CARTE_SEJOUR|PERMIS)$",
        message = "Type invalide. Valeurs acceptées : CIN, PASSEPORT, CARTE_SEJOUR, PERMIS"
    )
    private String type;

    @NotBlank(message = "Le pays émetteur est obligatoire")
    private String paysEmetteur;

    // optionnel — null si la pièce n'expire pas (ex: CIN Maroc sans date)
    private LocalDate dateExpiration;

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getPaysEmetteur() { return paysEmetteur; }
    public void setPaysEmetteur(String paysEmetteur) { this.paysEmetteur = paysEmetteur; }

    public LocalDate getDateExpiration() { return dateExpiration; }
    public void setDateExpiration(LocalDate dateExpiration) {
        this.dateExpiration = dateExpiration;
    }
}
