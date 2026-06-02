package com.okanetransfer.entity.devise;

import jakarta.persistence.*;

@Entity
@Table(name = "pays")
public class Pays {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false, length = 3)
    private String codeIso;

    @Column(nullable = false)
    private String indicatifTel;

    @Column
    private String formatTel;

    @Column
    private Integer longueurTel;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "devise_id")
    private Devise devise;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getCodeIso() { return codeIso; }
    public void setCodeIso(String codeIso) { this.codeIso = codeIso; }

    public String getIndicatifTel() { return indicatifTel; }
    public void setIndicatifTel(String indicatifTel) { this.indicatifTel = indicatifTel; }

    public String getFormatTel() { return formatTel; }
    public void setFormatTel(String formatTel) { this.formatTel = formatTel; }

    public Integer getLongueurTel() { return longueurTel; }
    public void setLongueurTel(Integer longueurTel) { this.longueurTel = longueurTel; }

    public Devise getDevise() { return devise; }
    public void setDevise(Devise devise) { this.devise = devise; }
}
