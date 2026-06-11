package com.okanetransfer.service.dto.devise.response;

public class PaysResponse {

    private Long id;
    private String nom;
    private String codeIso;
    private String indicatifTel;
    private String formatTel;
    private Integer longueurTel;
    private String deviseCode;
    private String deviseNom;
    private String deviseSymbole;

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

    public String getDeviseCode() { return deviseCode; }
    public void setDeviseCode(String deviseCode) { this.deviseCode = deviseCode; }

    public String getDeviseNom() { return deviseNom; }
    public void setDeviseNom(String deviseNom) { this.deviseNom = deviseNom; }

    public String getDeviseSymbole() { return deviseSymbole; }
    public void setDeviseSymbole(String deviseSymbole) { this.deviseSymbole = deviseSymbole; }
}
