package com.okanetransfer.service.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class AdminUpdateUserRequest {

    @Size(min = 2, max = 50)
    private String nom;

    @Size(min = 2, max = 50)
    private String prenom;

    @Email
    private String email;

    private String telephone;

    @Size(min = 2, max = 60)
    private String pays;

    public String getNom()       { return nom; }
    public void setNom(String v) { this.nom = v; }

    public String getPrenom()       { return prenom; }
    public void setPrenom(String v) { this.prenom = v; }

    public String getEmail()       { return email; }
    public void setEmail(String v) { this.email = v; }

    public String getTelephone()       { return telephone; }
    public void setTelephone(String v) { this.telephone = v; }

    public String getPays()       { return pays; }
    public void setPays(String v) { this.pays = v; }
}
