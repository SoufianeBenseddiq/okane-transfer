package com.okanetransfer.service.impl.agence;

import com.okanetransfer.entity.agence.Agence;
import com.okanetransfer.repository.agence.AgenceRepository;
import com.okanetransfer.service.converter.agence.AgenceConverter;
import com.okanetransfer.service.dto.agence.request.AgenceRequest;
import com.okanetransfer.service.dto.agence.response.AgenceResponse;
import com.okanetransfer.service.facade.agence.AgenceService;
import com.okanetransfer.service.facade.user.UtilisateurService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgenceServiceImpl implements AgenceService {

    private final AgenceRepository  repository;
    private final AgenceConverter   converter;
    private final UtilisateurService utilisateurService;

    public AgenceServiceImpl(AgenceRepository repository, AgenceConverter converter,
                             UtilisateurService utilisateurService) {
        this.repository          = repository;
        this.converter           = converter;
        this.utilisateurService  = utilisateurService;
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    @Override
    public AgenceResponse findById(Long id) {
        return converter.toResponse(
            repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Agence introuvable : " + id))
        );
    }

    @Override
    public AgenceResponse findByNom(String nom) {
        Agence a = repository.findByNom(nom);
        if (a == null) throw new EntityNotFoundException("Agence introuvable : " + nom);
        return converter.toResponse(a);
    }

    @Override
    public AgenceResponse findByAdresse(String adresse) {
        Agence a = repository.findByAdresse(adresse);
        if (a == null) throw new EntityNotFoundException("Agence introuvable : " + adresse);
        return converter.toResponse(a);
    }

    @Override
    public AgenceResponse findByResponsableEmail(String email) {
        Agence a = repository.findByResponsableEmail(email);
        if (a == null) throw new EntityNotFoundException("Aucune agence pour le manager : " + email);
        return converter.toResponse(a);
    }

    @Override
    public AgenceResponse findByAgentEmail(String email) {
        return converter.toResponse(
            repository.findByAgentEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Aucune agence pour l'agent : " + email))
        );
    }

    @Override
    public List<AgenceResponse> findAll() {
        return converter.toResponses(repository.findAll());
    }

    @Override
    public List<AgenceResponse> findByActiveTrue() {
        return converter.toResponses(repository.findByActiveTrue());
    }

    @Override
    public List<AgenceResponse> findCentrales() {
        return converter.toResponses(repository.findByEstCentraleTrue());
    }

    // ── Write ─────────────────────────────────────────────────────────────────

    @Override
    public AgenceResponse save(AgenceRequest request) {
        validateHierarchy(request, null);

        Agence entity = converter.toEntity(request);
        if (repository.findByNom(entity.getNom()) != null) {
            throw new IllegalArgumentException("Une agence avec le nom '" + entity.getNom() + "' existe déjà");
        }

        setAgenceCentrale(entity, request.getAgenceCentraleId());

        Agence saved = repository.save(entity);
        utilisateurService.updateAgenceManager(saved.getId(), request.getResponsableId());
        return converter.toResponse(repository.findById(saved.getId()).orElse(saved));
    }

    @Override
    public AgenceResponse update(Long id, AgenceRequest request) {
        Agence existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Agence introuvable : " + id));

        validateHierarchy(request, id);
        converter.updateEntity(existing, request);
        setAgenceCentrale(existing, request.getAgenceCentraleId());

        repository.save(existing);
        utilisateurService.updateAgenceManager(id, request.getResponsableId());
        return converter.toResponse(repository.findById(id).orElse(existing));
    }

    @Override
    public AgenceResponse toggleActive(Long id) {
        Agence agence = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Agence introuvable : " + id));
        agence.setActive(!Boolean.TRUE.equals(agence.getActive()));
        return converter.toResponse(repository.save(agence));
    }

    @Override
    public void deleteById(long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Agence introuvable : " + id);
        }
        repository.deleteById(id);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void validateHierarchy(AgenceRequest request, Long currentId) {
        boolean isCentrale = Boolean.TRUE.equals(request.getEstCentrale());

        if (isCentrale && request.getAgenceCentraleId() != null) {
            throw new IllegalArgumentException("Une agence centrale ne peut pas avoir de centrale parente.");
        }

        if (!isCentrale && request.getAgenceCentraleId() != null) {
            Agence centrale = repository.findById(request.getAgenceCentraleId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Agence centrale introuvable : " + request.getAgenceCentraleId()));
            if (!Boolean.TRUE.equals(centrale.getEstCentrale())) {
                throw new IllegalArgumentException("L'agence parente n'est pas une agence centrale.");
            }
            if (currentId != null && centrale.getId().equals(currentId)) {
                throw new IllegalArgumentException("Une agence ne peut pas être sa propre centrale.");
            }
        }
    }

    private void setAgenceCentrale(Agence agence, Long agenceCentraleId) {
        if (agenceCentraleId != null) {
            Agence centrale = repository.findById(agenceCentraleId)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Agence centrale introuvable : " + agenceCentraleId));
            agence.setAgenceCentrale(centrale);
        } else {
            agence.setAgenceCentrale(null);
        }
    }
}
