package com.okanetransfer.service.impl.aml;

import com.okanetransfer.entity.aml.DeclarationSoupcon;
import com.okanetransfer.entity.aml.ListeOFAC;
import com.okanetransfer.entity.aml.RegleAML;
import com.okanetransfer.entity.transfert.Transfert;
import com.okanetransfer.repository.aml.DeclarationSoupconRepository;
import com.okanetransfer.repository.aml.ListeOFACRepository;
import com.okanetransfer.repository.aml.RegleAMLRepository;
import com.okanetransfer.service.facade.aml.IAmlService;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AmlServiceImpl implements IAmlService {

    private final DeclarationSoupconRepository declarationRepository;
    private final RegleAMLRepository regleRepository;
    private final ListeOFACRepository listeOFACRepository;

    public AmlServiceImpl(DeclarationSoupconRepository declarationRepository,
                          RegleAMLRepository regleRepository,
                          ListeOFACRepository listeOFACRepository) {
        this.declarationRepository = declarationRepository;
        this.regleRepository = regleRepository;
        this.listeOFACRepository = listeOFACRepository;
    }

    @Override
    public void verifierOFAC(Transfert transfert) {
        if (transfert == null) {
            throw new IllegalArgumentException("Transfert ne peut pas être null");
        }
        // Vérifie expéditeur (client) et bénéficiaire contre la liste OFAC active
        String expediteurNom = transfert.getExpediteur() != null && transfert.getExpediteur().getClient() != null
                ? transfert.getExpediteur().getClient().getNom()
                : null;
        String expediteurPrenom = transfert.getExpediteur() != null && transfert.getExpediteur().getClient() != null
                ? transfert.getExpediteur().getClient().getPrenom()
                : null;
        String expediteurPays = transfert.getExpediteur() != null && transfert.getExpediteur().getClient() != null
                ? transfert.getExpediteur().getClient().getPays()
                : null;

        String beneficiaireNom = transfert.getBeneficiaire() != null ? transfert.getBeneficiaire().getNom() : null;
        String beneficiairePrenom = transfert.getBeneficiaire() != null ? transfert.getBeneficiaire().getPrenom() : null;
        String beneficiairePays = transfert.getBeneficiaire() != null ? transfert.getBeneficiaire().getPays() : null;

        List<ListeOFAC> entries = listeOFACRepository.findAll().stream()
                .filter(ListeOFAC::getActif)
                .toList();

        boolean expediteurMatch = entries.stream()
                .anyMatch(entry -> matchOFAC(entry, expediteurNom, expediteurPrenom, expediteurPays));
        boolean beneficiaireMatch = entries.stream()
                .anyMatch(entry -> matchOFAC(entry, beneficiaireNom, beneficiairePrenom, beneficiairePays));

        if (beneficiaireMatch && transfert.getBeneficiaire() != null) {
            // Marque le bénéficiaire en surveillance si détecté
            transfert.getBeneficiaire().setSurListeSurveillance(true);
        }

        if (expediteurMatch || beneficiaireMatch) {
            // TODO: Remplacer par OFACViolationException quand disponible
            throw new IllegalStateException("OFAC: expéditeur ou bénéficiaire détecté sur la liste.");
        }
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
        if (regle == null || transfert == null) {
            return false;
        }

        // Règle basée sur le seuil montant (simple et locale au transfert)
        BigDecimal seuil = regle.getSeuilMontant();
        BigDecimal montant = transfert.getMontantEnvoye();
        boolean declencheSeuilMontant = seuil != null && montant != null && montant.compareTo(seuil) >= 0;

        // TODO: Implémenter les règles basées sur le volume dans la fenêtre de temps
        // Nécessite un TransfertRepository pour compter les transferts dans la fenêtre.
        boolean declencheVolume = false;

        return declencheSeuilMontant || declencheVolume;
    }

    private boolean matchOFAC(ListeOFAC entry, String nom, String prenom, String pays) {
        if (entry == null || nom == null || prenom == null) {
            return false;
        }
        String nomNorm = normalize(nom);
        String prenomNorm = normalize(prenom);
        String entryNom = normalize(entry.getNom());
        String entryPrenom = normalize(entry.getPrenom());
        String entryAlias = normalize(entry.getAlias());
        String entryPays = normalize(entry.getPays());
        String paysNorm = normalize(pays);

        boolean nomPrenomMatch = nomNorm.equals(entryNom) && prenomNorm.equals(entryPrenom);
        boolean aliasMatch = !entryAlias.isEmpty()
                && (entryAlias.equals(nomNorm) || entryAlias.equals(prenomNorm)
                || entryAlias.equals((nomNorm + " " + prenomNorm).trim()));
        boolean paysMatch = entryPays.isEmpty() || paysNorm.isEmpty() || entryPays.equals(paysNorm);

        return (nomPrenomMatch || aliasMatch) && paysMatch;
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }
}
