package com.okanetransfer.service.converter.caisse;

import com.okanetransfer.entity.caisse.CaisseOperation;
import com.okanetransfer.entity.devise.Corridor;
import com.okanetransfer.entity.transfert.Transfert;
import com.okanetransfer.service.dto.caisse.request.CaisseOperationRequest;
import com.okanetransfer.service.dto.caisse.response.CaisseOperationResponse;
import com.okanetransfer.shared.enums.TypeOperation;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CaisseOperationConverter {

    public CaisseOperation toEntity(CaisseOperationRequest request) {
        CaisseOperation operation = new CaisseOperation();
        operation.setType(request.getType());
        operation.setMontant(request.getMontant());
        operation.setReferenceTransfert(request.getReferenceTransfert());
        return operation;
    }

    public CaisseOperationResponse toResponse(CaisseOperation operation) {
        CaisseOperationResponse response = new CaisseOperationResponse();
        response.setId(operation.getId());
        response.setAgentId(operation.getAgent().getId());
        response.setAgentNom(operation.getAgent().getPrenom() + " " + operation.getAgent().getNom());
        response.setType(operation.getType());
        response.setMontant(operation.getMontant());
        response.setDateHeure(operation.getDateHeure());
        if (operation.getTransfert() != null) {
            response.setTransfertId(operation.getTransfert().getId());
        }
        response.setReferenceTransfert(operation.getReferenceTransfert());

        // Resolve devise from corridor — one field, no calculation
        response.setDevise(resolveDevise(operation));

        return response;
    }

    public List<CaisseOperation> toEntities(List<CaisseOperationRequest> requests) {
        return requests.stream().map(this::toEntity).toList();
    }

    public List<CaisseOperationResponse> toResponses(List<CaisseOperation> operations) {
        return operations.stream().map(this::toResponse).toList();
    }

    private String resolveDevise(CaisseOperation operation) {
        Transfert transfert = operation.getTransfert();
        if (transfert == null) return "MAD";

        Corridor corridor = transfert.getCorridor();
        if (corridor == null) return "MAD";

        if (operation.getType() == TypeOperation.RETRAIT) {
            if (corridor.getPaysDestination() != null && corridor.getPaysDestination().getDevise() != null) {
                return corridor.getPaysDestination().getDevise().getCode();
            }
        } else if (operation.getType() == TypeOperation.ENVOI) {
            if (corridor.getPaysSource() != null && corridor.getPaysSource().getDevise() != null) {
                return corridor.getPaysSource().getDevise().getCode();
            }
        }
        return "MAD";
    }
}
