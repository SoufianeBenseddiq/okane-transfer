package com.okanetransfer.service.facade.agence;

import com.okanetransfer.entity.agence.Agence;
import com.okanetransfer.entity.user.Manager;
import com.okanetransfer.service.dto.agence.request.AgenceRequest;
import com.okanetransfer.service.dto.agence.response.AgenceResponse;

import java.util.List;

public interface AgenceService {
    AgenceResponse findByNom(String nom);
    AgenceResponse findByAdresse(String adresse);
    AgenceResponse findByResponsableEmail(String email);
    void deleteById(long id);
    List<AgenceResponse> findByActiveTrue();
    AgenceResponse save(AgenceRequest agence);
    AgenceResponse update(Long id,AgenceRequest agence);
    List<AgenceResponse> findAll();
}
