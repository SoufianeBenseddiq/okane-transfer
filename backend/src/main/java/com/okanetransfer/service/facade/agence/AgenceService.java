package com.okanetransfer.service.facade.agence;

import java.util.List;

import com.okanetransfer.service.dto.agence.request.AgenceRequest;
import com.okanetransfer.service.dto.agence.request.RevisionPlafondRequest;
import com.okanetransfer.service.dto.agence.response.AgenceResponse;

public interface AgenceService {
    AgenceResponse findByNom(String nom);

    AgenceResponse findByAdresse(String adresse);

    AgenceResponse findByResponsableEmail(String email);

    void deleteById(long id);

    List<AgenceResponse> findByActiveTrue();

    AgenceResponse save(AgenceRequest agence);

    AgenceResponse update(Long id, AgenceRequest agence);

    List<AgenceResponse> findAll();

    void demanderRevisionPlafond(Long agenceId, RevisionPlafondRequest request);
}
