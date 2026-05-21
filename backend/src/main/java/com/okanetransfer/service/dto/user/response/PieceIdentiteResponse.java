package com.okanetransfer.service.dto.user.response;

import java.time.LocalDate;

public class PieceIdentiteResponse {

    private Long id;

    // numéro déchiffré automatiquement par CryptoConverter
    // exposé uniquement au propriétaire du compte
    private String numero;

    private String type;            // "CIN", "PASSEPORT"...
    private String paysEmetteur;
    private LocalDate dateExpiration;
    private Boolean principale;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public Boolean getPrincipale() { return principale; }
    public void setPrincipale(Boolean principale) { this.principale = principale; }
}