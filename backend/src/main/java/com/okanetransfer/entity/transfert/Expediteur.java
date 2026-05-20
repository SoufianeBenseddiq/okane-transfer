package com.okanetransfer.entity.transfert;

import com.okanetransfer.shared.enums.TypePiece;
import com.okanetransfer.shared.util.CryptoConverter;
import jakarta.persistence.*;

@Entity
@Table(name = "expediteurs")
public class Expediteur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    // chiffré automatiquement AES-256 avant stockage
    @Convert(converter = CryptoConverter.class)
    @Column(nullable = false)
    private String numeroPiece;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypePiece typePiece;

    @Column(nullable = false)
    private String telephone;

    @Column(nullable = false)
    private String pays;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNumeroPiece() {
        return numeroPiece;
    }

    public void setNumeroPiece(String numeroPiece) {
        this.numeroPiece = numeroPiece;
    }

    public TypePiece getTypePiece() {
        return typePiece;
    }

    public void setTypePiece(TypePiece typePiece) {
        this.typePiece = typePiece;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }
}