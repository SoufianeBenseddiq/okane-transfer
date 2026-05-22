package com.okanetransfer.service.facade.caisse;

import com.okanetransfer.entity.caisse.CaisseOperation;
import com.okanetransfer.entity.user.Agent;
import com.okanetransfer.service.dto.caisse.response.CaisseOperationResponse;

import java.util.List;

public interface CaisseOperationService {
    List<CaisseOperationResponse> findByAgentEmail(String email);
    void deleteById(Long id);
    List<CaisseOperationResponse> findAll();
}
