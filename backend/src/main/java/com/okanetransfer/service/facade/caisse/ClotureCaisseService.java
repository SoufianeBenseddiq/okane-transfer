package com.okanetransfer.service.facade.caisse;

import java.time.LocalDate;
import java.util.List;

import com.okanetransfer.service.dto.caisse.request.ClotureCaisseRequest;
import com.okanetransfer.service.dto.caisse.response.ClotureCaisseResponse;

public interface ClotureCaisseService {
    ClotureCaisseResponse findByAgentEmailAndDate(String email, LocalDate date);

    List<ClotureCaisseResponse> findByEcartSignaleTrue();

    List<ClotureCaisseResponse> findByAgentEmailAndEcartSignaleTrue(String email);

    void deleteById(Long id);

    ClotureCaisseResponse save(ClotureCaisseRequest caisse);

    ClotureCaisseResponse update(ClotureCaisseRequest caisse);

    List<ClotureCaisseResponse> findAll();

    ClotureCaisseResponse rapportCloture(String agentEmail, LocalDate date);

    ClotureCaisseResponse cloturerCaisse(ClotureCaisseRequest request);

    List<ClotureCaisseResponse> findByAgence(Long agenceId, int limit);
}
