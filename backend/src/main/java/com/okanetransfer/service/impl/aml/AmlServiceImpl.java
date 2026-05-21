package com.okanetransfer.service.impl.aml;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.okanetransfer.entity.aml.DeclarationSoupcon;
import com.okanetransfer.entity.aml.RegleAML;
import com.okanetransfer.entity.transfert.Transfert;
import com.okanetransfer.repository.aml.DeclarationSoupconRepository;
import com.okanetransfer.repository.aml.RegleAMLRepository;
import com.okanetransfer.service.facade.aml.IAmlService;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class AmlServiceImpl implements IAmlService {

    private final DeclarationSoupconRepository declarationRepository;
    private final RegleAMLRepository regleRepository;

    public AmlServiceImpl(DeclarationSoupconRepository declarationRepository,
                        RegleAMLRepository regleRepository) {
        this.declarationRepository = declarationRepository;
        this.regleRepository = regleRepository;
    }

    @Override
    public void verifierOFAC(Transfert transfert) {
        // TODO: Implement OFAC verification against ListeOFAC
        // This will be called by TransfertService before creating a transfer
        // Throw OFACViolationException if a match is found
    }

    @Override
    public List<DeclarationSoupcon> evaluerTransfert(Transfert transfert) {
        List<DeclarationSoupcon> declarations = new ArrayList<>();
        
        // Retrieve all active AML rules
        List<RegleAML> reglesActives = regleRepository.findAll().stream()
                .filter(RegleAML::getActive)
                .toList();
        
        // Evaluate each rule against the transfer
        for (RegleAML regle : reglesActives) {
            if (evaluerRegle(regle, transfert)) {
                // Rule triggered - create a declaration
                DeclarationSoupcon declaration = new DeclarationSoupcon();
                declaration.setTransfert(transfert);
                declaration.setRegle(regle);
                declaration.setMotif("Règle " + regle.getNom() + " déclenchée : " + regle.getDescription());
                declaration.setMontantTotal(transfert.getMontantEnvoye());
                declaration.setTraitee(false);
                
                DeclarationSoupcon saved = declarationRepository.save(declaration);
                declarations.add(saved);
            }
        }
        
        return declarations;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeclarationSoupcon> getDeclarations() {
        return declarationRepository.findAll();
    }

    @Override
    public DeclarationSoupcon traiterDeclaration(Long declarationId) {
        DeclarationSoupcon declaration = declarationRepository.findById(declarationId)
                .orElseThrow(() -> new EntityNotFoundException(
                    "DeclarationSoupcon introuvable pour l'id: " + declarationId));
        
        declaration.setTraitee(true);
        return declarationRepository.save(declaration);
    }

    /**
     * Évalue si une règle est déclenchée pour un transfert donné
     * @param regle la règle à évaluer
     * @param transfert le transfert à vérifier
     * @return true si la règle est déclenchée, false sinon
     */
    private boolean evaluerRegle(RegleAML regle, Transfert transfert) {
        // TODO: Implement rule evaluation logic
        // Check based on:
        // - seuilMontant: if transfer amount exceeds threshold
        // - seuilNbTransactions: if number of transactions in time window exceeds threshold
        // - fenetreTempsMinutes: the time window for counting transactions
        return false;
    }
}
