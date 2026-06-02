package com.okanetransfer.service.dto.caisse.response;


import com.okanetransfer.shared.enums.TypeOperation;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CaisseOperationResponse {

    private Long id;
    private Long agentId;
    private String agentNom;
    private TypeOperation type;
    private BigDecimal montant;
    private String devise;   // actual currency: MAD for ENVOI, EUR/XOF/… for RETRAIT
    private LocalDateTime dateHeure;
    private Long transfertId;
    private String referenceTransfert;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAgentId() { return agentId; }
    public void setAgentId(Long agentId) { this.agentId = agentId; }

    public String getAgentNom() { return agentNom; }
    public void setAgentNom(String agentNom) { this.agentNom = agentNom; }

    public TypeOperation getType() { return type; }
    public void setType(TypeOperation type) { this.type = type; }

    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }

    public String getDevise() { return devise; }
    public void setDevise(String devise) { this.devise = devise; }

    public LocalDateTime getDateHeure() { return dateHeure; }
    public void setDateHeure(LocalDateTime dateHeure) { this.dateHeure = dateHeure; }

    public Long getTransfertId() { return transfertId; }
    public void setTransfertId(Long transfertId) { this.transfertId = transfertId; }

    public String getReferenceTransfert() { return referenceTransfert; }
    public void setReferenceTransfert(String referenceTransfert) { this.referenceTransfert = referenceTransfert; }
}
