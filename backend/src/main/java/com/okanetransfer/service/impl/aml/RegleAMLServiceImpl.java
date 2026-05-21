package com.okanetransfer.service.impl.aml;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.okanetransfer.entity.aml.RegleAML;
import com.okanetransfer.repository.aml.RegleAMLRepository;
import com.okanetransfer.service.facade.aml.IRegleAMLService;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class RegleAMLServiceImpl implements IRegleAMLService {

    private final RegleAMLRepository regleAMLRepository;

    public RegleAMLServiceImpl(RegleAMLRepository regleAMLRepository) {
        this.regleAMLRepository = regleAMLRepository;
    }

    @Override
    public RegleAML create(RegleAML regleAML) {
        return regleAMLRepository.save(regleAML);
    }

    @Override
    public RegleAML update(Long id, RegleAML regleAML) {
        RegleAML existing = regleAMLRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("RegleAML introuvable pour l'id: " + id));
        existing.setNom(regleAML.getNom());
        existing.setDescription(regleAML.getDescription());
        existing.setSeuilMontant(regleAML.getSeuilMontant());
        existing.setSeuilNbTransactions(regleAML.getSeuilNbTransactions());
        existing.setFenetreTempsMinutes(regleAML.getFenetreTempsMinutes());
        existing.setActive(regleAML.getActive());
        return regleAMLRepository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public RegleAML getById(Long id) {
        return regleAMLRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("RegleAML introuvable pour l'id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegleAML> getAll() {
        return regleAMLRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        if (!regleAMLRepository.existsById(id)) {
            throw new EntityNotFoundException("RegleAML introuvable pour l'id: " + id);
        }
        regleAMLRepository.deleteById(id);
    }
}
