package com.okanetransfer.service.dto.user.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UserResponse {

    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String pays;        // pays.nom  — kept as String for frontend compatibility
    private String paysCodeIso; // pays.codeIso
    private Long   paysId;
    private String role;
    private Boolean actif;
    private BigDecimal soldeCaisse; // non-null for ROLE_AGENT only
    private Long agenceId;          // non-null for ROLE_AGENT and ROLE_MANAGER
    private LocalDateTime creeLe;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getPays() { return pays; }
    public void setPays(String pays) { this.pays = pays; }

    public String getPaysCodeIso() { return paysCodeIso; }
    public void setPaysCodeIso(String paysCodeIso) { this.paysCodeIso = paysCodeIso; }

    public Long getPaysId() { return paysId; }
    public void setPaysId(Long paysId) { this.paysId = paysId; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }

    public BigDecimal getSoldeCaisse() { return soldeCaisse; }
    public void setSoldeCaisse(BigDecimal soldeCaisse) { this.soldeCaisse = soldeCaisse; }

    public Long getAgenceId() { return agenceId; }
    public void setAgenceId(Long agenceId) { this.agenceId = agenceId; }

    public LocalDateTime getCreeLe() { return creeLe; }
    public void setCreeLe(LocalDateTime creeLe) { this.creeLe = creeLe; }
}
