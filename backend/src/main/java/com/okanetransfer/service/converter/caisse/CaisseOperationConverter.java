package com.okanetransfer.service.converter.caisse;


import com.okanetransfer.service.dto.caisse.request.CaisseOperationRequest;
import com.okanetransfer.service.dto.caisse.response.CaisseOperationResponse;
import com.okanetransfer.entity.caisse.CaisseOperation;
import com.okanetransfer.entity.user.Agent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CaisseOperationConverter {

    public CaisseOperation toEntity(CaisseOperationRequest request) {
        CaisseOperation operation = new CaisseOperation();
//        operation.setAgent(agent);
        operation.setType(request.getType());
        operation.setMontant(request.getMontant());
        operation.setReferenceTransfert(request.getReferenceTransfert());
        // dateHeure rempli automatiquement par @PrePersist
        return operation;
    }

    public CaisseOperationResponse toResponse(CaisseOperation operation) {
        CaisseOperationResponse response = new CaisseOperationResponse();
        response.setId(operation.getId());
        response.setAgentId(operation.getAgent().getId());
        response.setAgentNom(
                operation.getAgent().getPrenom() + " " + operation.getAgent().getNom()
        );
        response.setType(operation.getType());
        response.setMontant(operation.getMontant());
        response.setDateHeure(operation.getDateHeure());
        if (operation.getTransfert() != null) {
            response.setTransfertId(operation.getTransfert().getId());
        }
        response.setReferenceTransfert(operation.getReferenceTransfert());
        return response;
    }

    public List<CaisseOperation> toEntities(List<CaisseOperationRequest> requests) {
        return requests.stream()
                .map(this::toEntity)
                .toList();
    }

    public List<CaisseOperationResponse> toResponses(List<CaisseOperation> operations) {
        return operations.stream()
                .map(this::toResponse)
                .toList();
    }
}
