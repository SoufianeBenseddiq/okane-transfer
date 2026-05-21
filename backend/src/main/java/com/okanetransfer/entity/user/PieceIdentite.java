package com.okanetransfer.entity.user;

import com.okanetransfer.shared.enums.TypePiece;
import com.okanetransfer.shared.util.CryptoConverter;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "pieces_identite")
public class PieceIdentite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = CryptoConverter.class)
    @Column(nullable = false)
    private String numero;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypePiece type;

    @Column(nullable = false)
    private String paysEmetteur;        // pays qui a délivré la pièce

    private LocalDate dateExpiration;

    @Column(nullable = false)
    private Boolean principale = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public TypePiece getType() { return type; }
    public void setType(TypePiece type) { this.type = type; }

    public String getPaysEmetteur() { return paysEmetteur; }
    public void setPaysEmetteur(String paysEmetteur) { this.paysEmetteur = paysEmetteur; }

    public LocalDate getDateExpiration() { return dateExpiration; }
    public void setDateExpiration(LocalDate dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public Boolean getPrincipale() { return principale; }
    public void setPrincipale(Boolean principale) { this.principale = principale; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
}