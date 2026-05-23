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

//    ClotureCaisseResponse cloturerCaisse(ClotureCaisseRequest request);

//    ClotureCaisseResponse rapportCloture(String agentEmail, LocalDate date);

    BigDecimal calculerSoldeTheorique(String agentEmail, LocalDateTime debut, LocalDateTime fin);
}
