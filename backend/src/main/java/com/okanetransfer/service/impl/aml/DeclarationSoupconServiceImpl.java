package com.okanetransfer.service.impl.aml;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.okanetransfer.entity.aml.DeclarationSoupcon;
import com.okanetransfer.repository.aml.DeclarationSoupconRepository;
import com.okanetransfer.service.facade.aml.IDeclarationSoupconService;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class DeclarationSoupconServiceImpl implements IDeclarationSoupconService {

    private final DeclarationSoupconRepository declarationRepository;

    public DeclarationSoupconServiceImpl(DeclarationSoupconRepository declarationRepository) {
        this.declarationRepository = declarationRepository;
    }

    @Override
    public DeclarationSoupcon create(DeclarationSoupcon declaration) {
        return declarationRepository.save(declaration);
    }

    @Override
    public DeclarationSoupcon update(Long id, DeclarationSoupcon declaration) {
        DeclarationSoupcon existing = declarationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("DeclarationSoupcon introuvable pour l'id: " + id));

        if (declaration.getTransfert() != null)   existing.setTransfert(declaration.getTransfert());
        if (declaration.getRegle() != null)        existing.setRegle(declaration.getRegle());
        if (declaration.getMotif() != null)        existing.setMotif(declaration.getMotif());
        if (declaration.getMontantTotal() != null) existing.setMontantTotal(declaration.getMontantTotal());
        if (declaration.getTraitee() != null)      existing.setTraitee(declaration.getTraitee());

        return declarationRepository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public DeclarationSoupcon getById(Long id) {
        return declarationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("DeclarationSoupcon introuvable pour l'id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeclarationSoupcon> getAll() {
        return declarationRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        if (!declarationRepository.existsById(id)) {
            throw new EntityNotFoundException("DeclarationSoupcon introuvable pour l'id: " + id);
        }
        declarationRepository.deleteById(id);
    }
}
