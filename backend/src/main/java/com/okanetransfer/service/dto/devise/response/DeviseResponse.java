package com.okanetransfer.service.dto.devise.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DeviseResponse {

    private Long id;
    private String code;
    private String nom;
    private String symbole;
    private Boolean active;
    private BigDecimal tauxVersEuro;
    private LocalDateTime derniereMaj;
    private String sourceTaux;

    // getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getSymbole() { return symbole; }
    public void setSymbole(String symbole) { this.symbole = symbole; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public BigDecimal getTauxVersEuro() { return tauxVersEuro; }
    public void setTauxVersEuro(BigDecimal tauxVersEuro) { this.tauxVersEuro = tauxVersEuro; }

    public LocalDateTime getDerniereMaj() { return derniereMaj; }
    public void setDerniereMaj(LocalDateTime derniereMaj) { this.derniereMaj = derniereMaj; }

    public String getSourceTaux() { return sourceTaux; }
    public void setSourceTaux(String sourceTaux) { this.sourceTaux = sourceTaux; }
}