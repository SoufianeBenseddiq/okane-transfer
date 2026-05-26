package com.okanetransfer.service.impl.agence;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.okanetransfer.entity.agence.Agence;
import com.okanetransfer.repository.agence.AgenceRepository;
import com.okanetransfer.service.converter.agence.AgenceConverter;
import com.okanetransfer.service.dto.agence.request.AgenceRequest;
import com.okanetransfer.service.dto.agence.request.RevisionPlafondRequest;
import com.okanetransfer.service.dto.agence.response.AgenceResponse;
import com.okanetransfer.service.facade.agence.AgenceService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AgenceServiceImpl implements AgenceService {

    private static final Logger logger = LoggerFactory.getLogger(AgenceServiceImpl.class);

    @Override
    public AgenceResponse findByNom(String nom) {
        Agence byNom = repository.findByNom(nom);
        if (byNom == null) {
            throw new EntityNotFoundException("Agence introuvable avec le nom : " + nom);
        }
        return converter.toResponse(byNom);
    }

    @Override
    public AgenceResponse findByAdresse(String adresse) {
        Agence byAdresse = repository.findByAdresse(adresse);
        if (byAdresse == null) {
            throw new EntityNotFoundException("Agence introuvable avec l'adresse : " + adresse);
        }
        return converter.toResponse(byAdresse);
    }

    @Override
    public AgenceResponse findByResponsableEmail(String email) {
        Agence byResponsable = repository.findByResponsableEmail(email);
        if (byResponsable == null) {
            throw new EntityNotFoundException("Agence introuvable pour ce manager" + email);
        }
        return converter.toResponse(byResponsable);
    }

    @Override
    public void deleteById(long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException(
                    "Agence introuvable avec l'id : " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public List<AgenceResponse> findByActiveTrue() {
        return converter.toResponses(
                repository.findByActiveTrue());
    }

    @Override
    public AgenceResponse save(AgenceRequest agenceRequest) {
        Agence saved = converter.toEntity(agenceRequest);
        if (repository.findByNom(saved.getNom()) != null) {
            throw new IllegalArgumentException(
                    "Une agence avec le nom '" + saved.getNom() + "' existe déjà");
        }

        return converter.toResponse(repository.save(saved));
    }

    @Override
    public AgenceResponse update(Long id, AgenceRequest agenceRequest) {
        Agence existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Agence introuvable avec l'id : " + id));

        converter.updateEntity(existing, agenceRequest);
        return converter.toResponse(repository.save(existing));
    }

    @Override
    public List<AgenceResponse> findAll() {
        return converter.toResponses(repository.findAll());
    }

    @Override
    public void demanderRevisionPlafond(Long agenceId, RevisionPlafondRequest request) {
        Agence agence = repository.findById(agenceId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Agence introuvable avec l'id : " + agenceId));

        logger.info("Demande de révision de plafond pour l'agence {}: nouveau plafond {}, justification: {}",
                agence.getNom(), request.getNouveauPlafond(), request.getJustification());

        agence.setPlafondJournalier(request.getNouveauPlafond());
        repository.save(agence);
    }

    private final AgenceRepository repository;
    private final AgenceConverter converter;

    public AgenceServiceImpl(AgenceRepository repository, AgenceConverter converter) {
        this.repository = repository;
        this.converter = converter;
    }
}
