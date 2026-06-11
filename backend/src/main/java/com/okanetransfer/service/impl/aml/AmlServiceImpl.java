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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class AmlServiceImpl implements IAmlService {

    private final DeclarationSoupconRepository declarationRepository;
    private final RegleAMLRepository           regleRepository;
    private final ListeOFACRepository          listeOFACRepository;

    public AmlServiceImpl(DeclarationSoupconRepository declarationRepository,
                          RegleAMLRepository regleRepository,
                          ListeOFACRepository listeOFACRepository) {
        this.declarationRepository = declarationRepository;
        this.regleRepository       = regleRepository;
        this.listeOFACRepository   = listeOFACRepository;
    }

    @Override
    public void verifierOFAC(Transfert transfert) {
        if (transfert == null) return;

        String expediteurNom    = transfert.getExpediteur() != null && transfert.getExpediteur().getClient() != null
                ? transfert.getExpediteur().getClient().getNom() : null;
        String expediteurPrenom = transfert.getExpediteur() != null && transfert.getExpediteur().getClient() != null
                ? transfert.getExpediteur().getClient().getPrenom() : null;
        String expediteurPays   = transfert.getExpediteur() != null && transfert.getExpediteur().getClient() != null
                ? (transfert.getExpediteur().getClient().getPays() != null
                        ? transfert.getExpediteur().getClient().getPays().getNom() : null) : null;

        String beneficiaireNom    = transfert.getBeneficiaire() != null ? transfert.getBeneficiaire().getNom()   : null;
        String beneficiairePrenom = transfert.getBeneficiaire() != null ? transfert.getBeneficiaire().getPrenom() : null;
        String beneficiairePays   = transfert.getBeneficiaire() != null ? transfert.getBeneficiaire().getPays()   : null;

        List<ListeOFAC> entries = listeOFACRepository.findAll().stream()
                .filter(ListeOFAC::getActif).toList();

        boolean expediteurMatch   = entries.stream().anyMatch(e -> matchOFAC(e, expediteurNom,   expediteurPrenom,   expediteurPays));
        boolean beneficiaireMatch = entries.stream().anyMatch(e -> matchOFAC(e, beneficiaireNom, beneficiairePrenom, beneficiairePays));

        if (beneficiaireMatch && transfert.getBeneficiaire() != null) {
            transfert.getBeneficiaire().setSurListeSurveillance(true);
        }

        if (expediteurMatch || beneficiaireMatch) {
            String who = expediteurMatch ? "expéditeur" : "bénéficiaire";
            throw new IllegalArgumentException(
                "Transfert bloqué : " + who + " détecté sur la liste des sanctions OFAC.");
        }
    }

    @Override
    public List<DeclarationSoupcon> evaluerTransfert(Transfert transfert) {
        List<DeclarationSoupcon> declarations = new ArrayList<>();

        List<RegleAML> reglesActives = regleRepository.findAll().stream()
                .filter(RegleAML::getActive).toList();

        for (RegleAML regle : reglesActives) {
            if (evaluerRegle(regle, transfert)) {
                DeclarationSoupcon declaration = new DeclarationSoupcon();
                declaration.setTransfert(transfert);
                declaration.setRegle(regle);
                declaration.setMotif("Règle " + regle.getNom() + " déclenchée : " + regle.getDescription());
                declaration.setMontantTotal(transfert.getMontantEnvoye());
                declaration.setTraitee(false);
                declarations.add(declarationRepository.save(declaration));
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
                .orElseThrow(() -> new EntityNotFoundException("DeclarationSoupcon introuvable : " + declarationId));
        declaration.setTraitee(true);
        return declarationRepository.save(declaration);
    }

    private boolean evaluerRegle(RegleAML regle, Transfert transfert) {
        if (regle == null || transfert == null) return false;
        BigDecimal seuil  = regle.getSeuilMontant();
        BigDecimal montant = transfert.getMontantEnvoye();
        boolean declencheSeuilMontant = seuil != null && montant != null && montant.compareTo(seuil) >= 0;
        // TODO: implement time-window rules (FRACTIONNEMENT, SURVEILLANCE_FREQUENCE)
        boolean declencheVolume = false;
        return declencheSeuilMontant || declencheVolume;
    }

    private boolean matchOFAC(ListeOFAC entry, String nom, String prenom, String pays) {
        if (entry == null || nom == null || prenom == null) return false;
        String nomN    = normalize(nom);
        String prenomN = normalize(prenom);
        String eNom    = normalize(entry.getNom());
        String ePrenom = normalize(entry.getPrenom());
        String eAlias  = normalize(entry.getAlias());
        String ePays   = normalize(entry.getPays());
        String paysN   = normalize(pays);
        boolean nameMatch  = nomN.equals(eNom) && prenomN.equals(ePrenom);
        boolean aliasMatch = !eAlias.isEmpty()
                && (eAlias.equals(nomN) || eAlias.equals(prenomN)
                || eAlias.equals((nomN + " " + prenomN).trim()));
        boolean paysMatch  = ePays.isEmpty() || paysN.isEmpty() || ePays.equals(paysN);
        return (nameMatch || aliasMatch) && paysMatch;
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }
}
