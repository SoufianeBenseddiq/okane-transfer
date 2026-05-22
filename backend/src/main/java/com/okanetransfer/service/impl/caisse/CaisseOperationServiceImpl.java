package com.okanetransfer.service.impl.caisse;

import com.okanetransfer.entity.caisse.CaisseOperation;
import com.okanetransfer.repository.caisse.CaisseOperationRepository;
//import com.okanetransfer.repository.user.AgentRepository;
import com.okanetransfer.service.converter.caisse.CaisseOperationConverter;
import com.okanetransfer.service.dto.caisse.response.CaisseOperationResponse;
import com.okanetransfer.service.facade.caisse.CaisseOperationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CaisseOperationServiceImpl implements CaisseOperationService {

    @Override
    public List<CaisseOperationResponse> findByAgentEmail(String email) {
//        if (agentRepository.findByEmail(email)) {
//            throw new EntityNotFoundException("Agent introuvable avec l'email : " + email);
//        }
        return converter.toResponses(
                repository.findByAgentEmail(email)
        );
    }

    @Override
    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException(
                    "Opération introuvable avec l'id : " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public List<CaisseOperationResponse> findAll() {
        return converter.toResponses(repository.findAll());
    }

    private final CaisseOperationRepository repository;
    private final CaisseOperationConverter converter;
//    private final AgentRepository agentRepository;

    public CaisseOperationServiceImpl(CaisseOperationRepository repository,
                                      CaisseOperationConverter converter) {
        this.repository = repository;
        this.converter = converter;
//        this.agentRepository = agentRepository;
    }
}