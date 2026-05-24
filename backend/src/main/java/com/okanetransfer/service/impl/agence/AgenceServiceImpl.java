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

    @Override
    public AgenceResponse findByNom(String nom) {
        Agence byNom = repository.findByNom(nom);
        if (byNom == null) {
            throw new EntityNotFoundException("Agence introuvable avec le nom : " +nom);
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
           throw new EntityNotFoundException("Agence introuvable pour ce manager"+ email);
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
                repository.findByActiveTrue()
        );
    }

    @Override
    public AgenceResponse save(AgenceRequest agenceRequest) {
        Agence entity = converter.toEntity(agenceRequest);
        if (repository.findByNom(entity.getNom()) != null) {
            throw new IllegalArgumentException(
                    "Une agence avec le nom '" + entity.getNom() + "' existe déjà");
        }
        Agence saved = repository.save(entity);
        utilisateurService.updateAgenceManager(saved.getId(), agenceRequest.getResponsableId());
        return converter.toResponse(repository.findById(saved.getId()).orElse(saved));
    }

    @Override
    public AgenceResponse update(Long id, AgenceRequest agenceRequest) {
        Agence existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Agence introuvable avec l'id : " + id));

        converter.updateEntity(existing, agenceRequest);
        repository.save(existing);
        utilisateurService.updateAgenceManager(id, agenceRequest.getResponsableId());
        return converter.toResponse(repository.findById(id).orElse(existing));
    }

    @Override
    public List<AgenceResponse> findAll() {
        return converter.toResponses(repository.findAll());
    }

    private final AgenceRepository repository;
    private final AgenceConverter converter;
    private final UtilisateurService utilisateurService;

    public AgenceServiceImpl(AgenceRepository repository, AgenceConverter converter,
                             UtilisateurService utilisateurService) {
        this.repository         = repository;
        this.converter          = converter;
        this.utilisateurService = utilisateurService;
    }
}
