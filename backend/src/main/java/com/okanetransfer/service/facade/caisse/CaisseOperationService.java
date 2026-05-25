package com.okanetransfer.service.facade.caisse;

import com.okanetransfer.entity.caisse.CaisseOperation;
import com.okanetransfer.entity.user.Agent;
import com.okanetransfer.service.dto.caisse.request.ClotureCaisseRequest;
import com.okanetransfer.service.dto.caisse.response.CaisseOperationResponse;
import com.okanetransfer.service.dto.caisse.response.ClotureCaisseResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface CaisseOperationService {
    List<CaisseOperationResponse> findByAgentEmail(String email);
    void deleteById(Long id);
    List<CaisseOperationResponse> findAll();
    CaisseOperationResponse ouvrirCaisse(String agentEmail, BigDecimal montantInitial);
    BigDecimal consulterSolde(String agentEmail);

    List<CaisseOperationResponse> historiqueOperations(String agentEmail);

    BigDecimal consulterSoldeDuJour(String agentEmail);

    List<CaisseOperationResponse> operationsDuJour(String agentEmail);

    List<CaisseOperationResponse> historiqueFiltre(String email, LocalDate dateDebut, LocalDate dateFin);
}
