package com.okanetransfer.entity.devise;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "devises")
public class Devise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 3)
    private String code;           // ex: "MAD", "EUR", "XOF"

    @Column(nullable = false)
    private String nom;            // ex: "Dirham Marocain"

    @Column(nullable = false, length = 10)
    private String symbole;        // ex: "د.م."

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false, precision = 15, scale = 6)
    private BigDecimal tauxVersEuro;

    private LocalDateTime derniereMaj;

    private String sourceTaux;     // "MANUEL" ou "API"

    // getters / setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getSymbole() {
        return symbole;
    }

    public void setSymbole(String symbole) {
        this.symbole = symbole;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public BigDecimal getTauxVersEuro() {
        return tauxVersEuro;
    }

    public void setTauxVersEuro(BigDecimal tauxVersEuro) {
        this.tauxVersEuro = tauxVersEuro;
    }

    public LocalDateTime getDerniereMaj() {
        return derniereMaj;
    }

    public void setDerniereMaj(LocalDateTime derniereMaj) {
        this.derniereMaj = derniereMaj;
    }

    public String getSourceTaux() {
        return sourceTaux;
    }

    public void setSourceTaux(String sourceTaux) {
        this.sourceTaux = sourceTaux;
    }
}