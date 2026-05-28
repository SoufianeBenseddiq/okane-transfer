package com.okanetransfer.service.dto.transfert.request;

import com.okanetransfer.service.dto.user.request.PieceIdentiteRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class CreateTransfertAvecNouveauClientRequest {

    @Valid
    @NotNull(message = "Les informations du nouveau client sont obligatoires.")
    private NouveauClientRequest nouveauClient;

    @Valid
    @NotNull(message = "La pièce d'identité est obligatoire.")
    private PieceIdentiteRequest pieceIdentite;

    private Long agentId;
    private Long agenceEnvoiId;
    private Long corridorId;
    private Long grilleTarifaireId;

    private String nomBeneficiaire;
    private String prenomBeneficiaire;
    private String telephoneBeneficiaire;
    private String paysBeneficiaire;
    private BigDecimal montant;

    public NouveauClientRequest getNouveauClient() { return nouveauClient; }
    public void setNouveauClient(NouveauClientRequest nouveauClient) { this.nouveauClient = nouveauClient; }

    public PieceIdentiteRequest getPieceIdentite() { return pieceIdentite; }
    public void setPieceIdentite(PieceIdentiteRequest pieceIdentite) { this.pieceIdentite = pieceIdentite; }

    public Long getAgentId() { return agentId; }
    public void setAgentId(Long agentId) { this.agentId = agentId; }

    public Long getAgenceEnvoiId() { return agenceEnvoiId; }
    public void setAgenceEnvoiId(Long agenceEnvoiId) { this.agenceEnvoiId = agenceEnvoiId; }

    public Long getCorridorId() { return corridorId; }
    public void setCorridorId(Long corridorId) { this.corridorId = corridorId; }

    public Long getGrilleTarifaireId() { return grilleTarifaireId; }
    public void setGrilleTarifaireId(Long grilleTarifaireId) { this.grilleTarifaireId = grilleTarifaireId; }

    public String getNomBeneficiaire() { return nomBeneficiaire; }
    public void setNomBeneficiaire(String nomBeneficiaire) { this.nomBeneficiaire = nomBeneficiaire; }

    public String getPrenomBeneficiaire() { return prenomBeneficiaire; }
    public void setPrenomBeneficiaire(String prenomBeneficiaire) { this.prenomBeneficiaire = prenomBeneficiaire; }

    public String getTelephoneBeneficiaire() { return telephoneBeneficiaire; }
    public void setTelephoneBeneficiaire(String telephoneBeneficiaire) { this.telephoneBeneficiaire = telephoneBeneficiaire; }

    public String getPaysBeneficiaire() { return paysBeneficiaire; }
    public void setPaysBeneficiaire(String paysBeneficiaire) { this.paysBeneficiaire = paysBeneficiaire; }

    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }

    public static class NouveauClientRequest {
        @NotBlank(message = "Le nom du client est obligatoire.")
        @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
        private String nom;

        @NotBlank(message = "Le prénom du client est obligatoire.")
        @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
        private String prenom;

        @NotBlank(message = "L'email du client est obligatoire.")
        @Email(message = "Format email invalide")
        private String email;

        @NotBlank(message = "Le mot de passe du client est obligatoire.")
        @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
        @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[0-9]).+$",
            message = "Le mot de passe doit contenir au moins 1 majuscule et 1 chiffre"
        )
        private String motDePasse;

        @NotBlank(message = "Le téléphone du client est obligatoire.")
        private String telephone;

        @NotBlank(message = "Le pays du client est obligatoire.")
        private String pays;

        public String getNom() { return nom; }
        public void setNom(String nom) { this.nom = nom; }

        public String getPrenom() { return prenom; }
        public void setPrenom(String prenom) { this.prenom = prenom; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getMotDePasse() { return motDePasse; }
        public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

        public String getTelephone() { return telephone; }
        public void setTelephone(String telephone) { this.telephone = telephone; }

        public String getPays() { return pays; }
        public void setPays(String pays) { this.pays = pays; }
    }
}
