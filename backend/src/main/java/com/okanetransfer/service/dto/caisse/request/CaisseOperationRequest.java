package com.okanetransfer.service.dto.caisse.request;


import com.okanetransfer.shared.enums.TypeOperation;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class CaisseOperationRequest {

    @NotNull(message = "L'ID de l'agent est obligatoire")
    private Long agentId;

    @NotNull(message = "Le type d'opération est obligatoire")
    private TypeOperation type;

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    private BigDecimal montant;

    private String referenceTransfert;


    public Long getAgentId() { return agentId; }
    public void setAgentId(Long agentId) { this.agentId = agentId; }

    public TypeOperation getType() { return type; }
    public void setType(TypeOperation type) { this.type = type; }

    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }

    public String getReferenceTransfert() { return referenceTransfert; }
    public void setReferenceTransfert(String referenceTransfert) { this.referenceTransfert = referenceTransfert; }
}