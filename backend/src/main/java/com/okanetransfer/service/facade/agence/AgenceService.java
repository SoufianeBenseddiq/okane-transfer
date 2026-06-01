package com.okanetransfer.service.facade.agence;

import com.okanetransfer.service.dto.agence.request.AgenceRequest;
import com.okanetransfer.service.dto.agence.response.AgenceResponse;

import java.util.List;

public interface AgenceService {
    AgenceResponse findById(Long id);
    AgenceResponse findByNom(String nom);
    AgenceResponse findByAdresse(String adresse);
    AgenceResponse findByResponsableEmail(String email);
    AgenceResponse findByAgentEmail(String email);
    List<AgenceResponse> findAll();
    List<AgenceResponse> findByActiveTrue();
    List<AgenceResponse> findCentrales();
    AgenceResponse save(AgenceRequest agence);
    AgenceResponse update(Long id, AgenceRequest agence);
    AgenceResponse toggleActive(Long id);
    void deleteById(long id);
}
