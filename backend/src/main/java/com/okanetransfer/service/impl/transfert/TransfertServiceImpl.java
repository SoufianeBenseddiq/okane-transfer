package com.okanetransfer.service.impl.transfert;


import com.okanetransfer.entity.agence.Agence;
import com.okanetransfer.entity.devise.Corridor;
import com.okanetransfer.entity.devise.GrilleTarifaire;
import com.okanetransfer.entity.transfert.Beneficiaire;
import com.okanetransfer.entity.transfert.Expediteur;
import com.okanetransfer.entity.transfert.Transfert;
import com.okanetransfer.entity.user.Agent;
import com.okanetransfer.entity.user.Client;
import com.okanetransfer.entity.user.PieceIdentite;
import com.okanetransfer.repository.agence.AgenceRepository;
import com.okanetransfer.repository.devise.CorridorRepository;
import com.okanetransfer.repository.devise.GrilleTarifaireRepository;
import com.okanetransfer.repository.transfert.BeneficiaireRepository;
import com.okanetransfer.repository.transfert.ExpediteurRepository;
import com.okanetransfer.repository.transfert.TransfertRepository;
import com.okanetransfer.repository.user.PieceIdentiteRepository;
import com.okanetransfer.repository.user.UtilisateurRepository;
import com.okanetransfer.service.converter.transfert.TransfertConverter;
import com.okanetransfer.service.dto.transfert.request.CreateTransfertRequest;
import com.okanetransfer.service.dto.transfert.request.PaiementRequest;
import com.okanetransfer.service.dto.transfert.request.UpdateTransfertRequest;
import com.okanetransfer.service.dto.transfert.response.TransfertResponse;
import com.okanetransfer.service.facade.transfert.ITransfertService;
import com.okanetransfer.shared.enums.StatutTransfert;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TransfertServiceImpl implements ITransfertService {

    private final TransfertRepository transfertRepository;

    private final BeneficiaireRepository beneficiaireRepository;

    private final ExpediteurRepository expediteurRepository;

    private final UtilisateurRepository utilisateurRepository;

    private final PieceIdentiteRepository pieceIdentiteRepository;

    private final AgenceRepository agenceRepository;

    private final CorridorRepository corridorRepository;

    private final GrilleTarifaireRepository grilleTarifaireRepository;

    public TransfertServiceImpl(
            TransfertRepository transfertRepository,
            BeneficiaireRepository beneficiaireRepository,
            ExpediteurRepository expediteurRepository,
            UtilisateurRepository utilisateurRepository,
            PieceIdentiteRepository pieceIdentiteRepository,
            AgenceRepository agenceRepository,
            CorridorRepository corridorRepository,
            GrilleTarifaireRepository grilleTarifaireRepository) {

        this.transfertRepository = transfertRepository;
        this.beneficiaireRepository = beneficiaireRepository;
        this.expediteurRepository = expediteurRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.pieceIdentiteRepository = pieceIdentiteRepository;
        this.agenceRepository = agenceRepository;
        this.corridorRepository = corridorRepository;
        this.grilleTarifaireRepository = grilleTarifaireRepository;
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
                .orElseThrow(() -> new RuntimeException("Transfert introuvable"));

        return TransfertConverter.toResponse(transfert);
    }

    @Override
    public TransfertResponse creerTransfert(CreateTransfertRequest request) {

        if (request.getMontant() == null || request.getMontant().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Montant invalide");
        }

        Client client = request.getClientId() == null
                ? null
                : utilisateurRepository.findClientById(request.getClientId()).orElse(null);

        PieceIdentite pieceIdentite = null;
        if (request.getPieceIdentiteId() != null) {
            pieceIdentite = client == null
                    ? pieceIdentiteRepository.findById(request.getPieceIdentiteId()).orElse(null)
                    : pieceIdentiteRepository.findByIdAndClientId(request.getPieceIdentiteId(), client.getId()).orElse(null);
        }

        Agent agent = request.getAgentId() == null
                ? null
                : utilisateurRepository.findAgentById(request.getAgentId()).orElse(null);

        Agence agenceEnvoi = request.getAgenceEnvoiId() == null
                ? null
                : agenceRepository.findById(request.getAgenceEnvoiId()).orElse(null);

        Corridor corridor = request.getCorridorId() == null
                ? null
                : corridorRepository.findById(request.getCorridorId()).orElse(null);

        GrilleTarifaire grilleTarifaire = request.getGrilleTarifaireId() == null
                ? null
                : grilleTarifaireRepository.findById(request.getGrilleTarifaireId()).orElse(null);

        Expediteur expediteur = new Expediteur();
        expediteur.setClient(client);
        expediteur.setPieceConfirmee(pieceIdentite);
        expediteur = expediteurRepository.save(expediteur);

        Beneficiaire beneficiaire = new Beneficiaire();
        beneficiaire.setNom(request.getNomBeneficiaire());
        beneficiaire.setPrenom(request.getPrenomBeneficiaire());
        beneficiaire.setTelephone(request.getTelephoneBeneficiaire());
        beneficiaire.setPays(request.getPaysBeneficiaire());
        beneficiaire.setSurListeSurveillance(false);
        beneficiaire = beneficiaireRepository.save(beneficiaire);

        BigDecimal frais;
        if (grilleTarifaire != null) {
            BigDecimal fraisVariables = request.getMontant()
                    .multiply(grilleTarifaire.getFraisPourcentage())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            frais = grilleTarifaire.getFraisFixe()
                    .add(fraisVariables);
        } else {
            frais = request.getMontant()
                    .multiply(BigDecimal.valueOf(0.05));
        }

        BigDecimal montantRecu = request.getMontant()
                .subtract(frais);

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
        transfert.setExpediteur(expediteur);
        transfert.setAgentSaisie(agent);
        transfert.setAgenceEnvoi(agenceEnvoi);
        transfert.setCorridor(corridor);
        transfert.setGrilleTarifaire(grilleTarifaire);

        transfert = transfertRepository.save(transfert);

        return TransfertConverter.toResponse(transfert);
    }

    @Override
    public TransfertResponse updateTransfert(Long id, UpdateTransfertRequest request) {
        Transfert transfert = transfertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transfert introuvable"));

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
                        new RuntimeException("Transfert introuvable")
                );

        if (transfert.getStatut() == StatutTransfert.PAYE) {
            throw new RuntimeException("Transfert déjà payé");
        }

        transfert.setStatut(StatutTransfert.PAYE);

        transfert.setPayeLe(LocalDateTime.now());

        transfert.setAgenceRetrait(null);

        transfert = transfertRepository.save(transfert);

        return TransfertConverter.toResponse(transfert);
    }

    @Override
    public TransfertResponse getByCodeRetrait(String codeRetrait) {

        Transfert transfert = transfertRepository
                .findByCodeRetrait(codeRetrait)
                .orElseThrow(() ->
                        new RuntimeException("Transfert introuvable")
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
                .orElseThrow(() -> new RuntimeException("Transfert introuvable"));

        transfert.setStatut(StatutTransfert.ANNULE);
        transfert = transfertRepository.save(transfert);

        return TransfertConverter.toResponse(transfert);
    }
// Je n’ai pas implémenté Delete /api/transferts/{id} car les transactions doivent rester toujours traçables.
// A la place, j’ai créé un endpoint d’annulation de transaction
}
