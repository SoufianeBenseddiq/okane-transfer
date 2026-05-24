package com.okanetransfer.service.impl.transfert;


import com.okanetransfer.entity.transfert.Beneficiaire;
import com.okanetransfer.entity.transfert.Transfert;
import com.okanetransfer.repository.transfert.BeneficiaireRepository;
import com.okanetransfer.repository.transfert.ExpediteurRepository;
import com.okanetransfer.repository.transfert.TransfertRepository;
import com.okanetransfer.service.converter.transfert.TransfertConverter;
import com.okanetransfer.service.dto.transfert.request.CreateTransfertRequest;
import com.okanetransfer.service.dto.transfert.request.PaiementRequest;
import com.okanetransfer.service.dto.transfert.request.UpdateTransfertRequest;
import com.okanetransfer.service.dto.transfert.response.TransfertResponse;
import com.okanetransfer.service.facade.transfert.ITransfertService;
import com.okanetransfer.shared.enums.StatutTransfert;
import com.okanetransfer.shared.exception.TransfertNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TransfertServiceImpl implements ITransfertService {

    private final TransfertRepository transfertRepository;

    private final BeneficiaireRepository beneficiaireRepository;

    private final ExpediteurRepository expediteurRepository;

    public TransfertServiceImpl(
            TransfertRepository transfertRepository,
            BeneficiaireRepository beneficiaireRepository,
            ExpediteurRepository expediteurRepository) {

        this.transfertRepository = transfertRepository;
        this.beneficiaireRepository = beneficiaireRepository;
        this.expediteurRepository = expediteurRepository;
    }

    @Override
    public List<TransfertResponse> getAllTransferts() {
        return transfertRepository.findAll()
                .stream()
                .map(TransfertConverter::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TransfertResponse getTransfertById(Long id) {
        Transfert transfert = transfertRepository.findById(id)
                .orElseThrow(() -> new TransfertNotFoundException("Transfert introuvable"));

        return TransfertConverter.toResponse(transfert);
    }

    @Override
    public TransfertResponse creerTransfert(CreateTransfertRequest request) {

        Beneficiaire beneficiaire = new Beneficiaire();
        beneficiaire.setNom(request.getNomBeneficiaire());
        beneficiaire.setPrenom(request.getPrenomBeneficiaire());
        beneficiaire.setTelephone(request.getTelephoneBeneficiaire());
        beneficiaire.setPays(request.getPaysBeneficiaire());
        beneficiaire.setSurListeSurveillance(false);
        beneficiaire = beneficiaireRepository.save(beneficiaire);

        // prendre en consideration que les frais = 5% en attd le module Frais
        BigDecimal frais = request.getMontant()
                .multiply(BigDecimal.valueOf(0.05));//INTEGRATION

        BigDecimal montantRecu = request.getMontant()
                .subtract(frais);

        if (request.getMontant().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Montant invalide");
        }

        String codeRetrait = UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();

        String numeroReference = "TRF-" + System.currentTimeMillis();

        Transfert transfert = new Transfert();
        transfert.setCodeRetrait(codeRetrait);
        transfert.setNumeroReference(numeroReference);
        transfert.setMontantEnvoye(request.getMontant());
        transfert.setMontantRecu(montantRecu);
        transfert.setFrais(frais);
        transfert.setStatut(StatutTransfert.EN_ATTENTE);
        transfert.setBeneficiaire(beneficiaire);

        // relations non geres encore (considerer null jusqu a integration )
        transfert.setExpediteur(null);
        transfert.setAgentSaisie(null);
        transfert.setAgenceEnvoi(null);
        transfert.setCorridor(null);
        transfert.setGrilleTarifaire(null);

        transfert = transfertRepository.save(transfert);

        return TransfertConverter.toResponse(transfert);
    }

    @Override
    public TransfertResponse updateTransfert(Long id, UpdateTransfertRequest request) {
        Transfert transfert = transfertRepository.findById(id)
                .orElseThrow(() -> new TransfertNotFoundException("Transfert introuvable"));

        if (transfert.getStatut() == StatutTransfert.PAYE) {
            throw new RuntimeException("Impossible de modifier un transfert deja paye");
        }

        if (transfert.getStatut() == StatutTransfert.ANNULE) {
            throw new RuntimeException("Impossible de modifier un transfert annule");
        }

        if (request.getMontant() != null) {
            if (request.getMontant().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Montant invalide");
            }

            BigDecimal frais = request.getMontant()
                    .multiply(BigDecimal.valueOf(0.05));

            BigDecimal montantRecu = request.getMontant()
                    .subtract(frais);

            transfert.setMontantEnvoye(request.getMontant());
            transfert.setFrais(frais);
            transfert.setMontantRecu(montantRecu);
        }

        Beneficiaire beneficiaire = transfert.getBeneficiaire();
        if (beneficiaire != null) {
            if (request.getNomBeneficiaire() != null) {
                beneficiaire.setNom(request.getNomBeneficiaire());
            }
            if (request.getPrenomBeneficiaire() != null) {
                beneficiaire.setPrenom(request.getPrenomBeneficiaire());
            }
            if (request.getTelephoneBeneficiaire() != null) {
                beneficiaire.setTelephone(request.getTelephoneBeneficiaire());
            }
            if (request.getPaysBeneficiaire() != null) {
                beneficiaire.setPays(request.getPaysBeneficiaire());
            }
            beneficiaireRepository.save(beneficiaire);
        }

        transfert = transfertRepository.save(transfert);

        return TransfertConverter.toResponse(transfert);
    }

    @Override
    public TransfertResponse payerTransfert(PaiementRequest request) {

        Transfert transfert = transfertRepository
                .findByCodeRetrait(request.getCodeRetrait())
                .orElseThrow(() ->
                        new TransfertNotFoundException("Transfert introuvable")
                );

        if (transfert.getStatut() == StatutTransfert.PAYE) {
            throw new RuntimeException("Transfert déjà payé");
        }

        transfert.setStatut(StatutTransfert.PAYE);

        transfert.setPayeLe(LocalDateTime.now());

        transfert.setAgenceRetrait(null);

        transfert.setTypePieceBeneficiaire(request.getTypePieceBeneficiaire());

        transfert.setNumeroPieceBeneficiaire(request.getNumeroPieceBeneficiaire());

        transfert = transfertRepository.save(transfert);

        return TransfertConverter.toResponse(transfert);
    }

    @Override
    public TransfertResponse getByCodeRetrait(String codeRetrait) {

        Transfert transfert = transfertRepository
                .findByCodeRetrait(codeRetrait)
                .orElseThrow(() ->
                        new TransfertNotFoundException("Transfert introuvable")
                );

        return TransfertConverter.toResponse(transfert);
    }

    @Override
    public List<TransfertResponse> getMesTransferts(Long clientId) {
        return transfertRepository.findByExpediteurClientId(clientId)
                .stream()
                .map(TransfertConverter::toResponse)
                .toList();
    }

    @Override
    public TransfertResponse annulerTransfert(Long id) {
        Transfert transfert = transfertRepository.findById(id)
                .orElseThrow(() -> new TransfertNotFoundException("Transfert introuvable"));

        transfert.setStatut(StatutTransfert.ANNULE);
        transfert = transfertRepository.save(transfert);

        return TransfertConverter.toResponse(transfert);
    }
// Je n’ai pas implémenté DELETE /api/transferts/{id} car les transactions doivent rester toujours traçables.
// À la place, j’ai créé un endpoint d’annulation de transaction.}

    @Override
    @Transactional(readOnly = true)
    public TransfertResponse getByTelephoneBeneficiaire(String telephone) {
        List<Transfert> resultats = transfertRepository
                .findByBeneficiairePhone(telephone);

        if (resultats.isEmpty()) {
            throw new RuntimeException("Aucun transfert en attente pour ce numéro");
        }

        // Le premier = le plus récent (ORDER BY creeLe DESC dans le repository)
        return TransfertConverter.toResponse(resultats.get(0));
    }
}