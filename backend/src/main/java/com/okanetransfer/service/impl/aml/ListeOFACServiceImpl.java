package com.okanetransfer.service.impl.aml;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.okanetransfer.entity.aml.ListeOFAC;
import com.okanetransfer.repository.aml.ListeOFACRepository;
import com.okanetransfer.service.facade.aml.IListeOFACService;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class ListeOFACServiceImpl implements IListeOFACService {

    private final ListeOFACRepository listeOFACRepository;

    public ListeOFACServiceImpl(ListeOFACRepository listeOFACRepository) {
        this.listeOFACRepository = listeOFACRepository;
    }

    @Override
    public ListeOFAC create(ListeOFAC listeOFAC) {
        return listeOFACRepository.save(listeOFAC);
    }

    @Override
    public ListeOFAC update(Long id, ListeOFAC listeOFAC) {
        ListeOFAC existing = listeOFACRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ListeOFAC introuvable pour l'id: " + id));
        existing.setNom(listeOFAC.getNom());
        existing.setPrenom(listeOFAC.getPrenom());
        existing.setAlias(listeOFAC.getAlias());
        existing.setPays(listeOFAC.getPays());
        existing.setMotifInscription(listeOFAC.getMotifInscription());
        existing.setActif(listeOFAC.getActif());
        existing.setAjoutManuel(listeOFAC.getAjoutManuel());
        return listeOFACRepository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public ListeOFAC getById(Long id) {
        return listeOFACRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ListeOFAC introuvable pour l'id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ListeOFAC> getAll() {
        return listeOFACRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        if (!listeOFACRepository.existsById(id)) {
            throw new EntityNotFoundException("ListeOFAC introuvable pour l'id: " + id);
        }
        listeOFACRepository.deleteById(id);
    }
}
