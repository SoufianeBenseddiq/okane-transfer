package com.okanetransfer.service.impl.transfert;


import com.okanetransfer.entity.agence.Agence;
import com.okanetransfer.entity.caisse.CaisseOperation;
import com.okanetransfer.entity.devise.Corridor;
import com.okanetransfer.entity.devise.GrilleTarifaire;
import com.okanetransfer.entity.transfert.Beneficiaire;
import com.okanetransfer.entity.transfert.Expediteur;
import com.okanetransfer.entity.transfert.Transfert;
import com.okanetransfer.entity.user.Agent;
import com.okanetransfer.entity.user.Client;
import com.okanetransfer.entity.user.PieceIdentite;
import com.okanetransfer.repository.agence.AgenceRepository;
import com.okanetransfer.repository.caisse.CaisseOperationRepository;
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
import com.okanetransfer.service.dto.transfert.response.TransfertStatsResponse;
import com.okanetransfer.service.facade.transfert.ITransfertService;
import com.okanetransfer.shared.enums.StatutTransfert;
<<<<<<< HEAD
import com.okanetransfer.shared.exception.TransfertNotFoundException;
=======
import com.okanetransfer.shared.enums.TypeOperation;
import org.springframework.data.domain.Sort;
>>>>>>> 78c7d10560abb4927f42ed9e093d2c396875add6
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
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

    private final CaisseOperationRepository caisseOperationRepository;

    public TransfertServiceImpl(
            TransfertRepository transfertRepository,
            BeneficiaireRepository beneficiaireRepository,
            ExpediteurRepository expediteurRepository,
            UtilisateurRepository utilisateurRepository,
            PieceIdentiteRepository pieceIdentiteRepository,
            AgenceRepository agenceRepository,
            CorridorRepository corridorRepository,
            GrilleTarifaireRepository grilleTarifaireRepository,
            CaisseOperationRepository caisseOperationRepository) {

        this.transfertRepository = transfertRepository;
        this.beneficiaireRepository = beneficiaireRepository;
        this.expediteurRepository = expediteurRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.pieceIdentiteRepository = pieceIdentiteRepository;
        this.agenceRepository = agenceRepository;
        this.corridorRepository = corridorRepository;
        this.grilleTarifaireRepository = grilleTarifaireRepository;
        this.caisseOperationRepository = caisseOperationRepository;
    }

    // ── Lecture ───────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<TransfertResponse> getAllTransferts() {
        return transfertRepository.findAll(Sort.by(Sort.Direction.DESC, "creeLe"))
                .stream()
                .map(TransfertConverter::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TransfertStatsResponse getStats() {
        TransfertStatsResponse stats = new TransfertStatsResponse();
        List<Transfert> transferts = transfertRepository.findAll();

        stats.setTotal(transferts.size());

        BigDecimal montantTotalEnvoye = BigDecimal.ZERO;
        BigDecimal montantTotalPaye = BigDecimal.ZERO;
        BigDecimal fraisTotal = BigDecimal.ZERO;
        long enAttente = 0;
        long payes = 0;
        long annules = 0;

        for (Transfert transfert : transferts) {
            if (transfert.getStatut() == StatutTransfert.EN_ATTENTE) {
                enAttente++;
            } else if (transfert.getStatut() == StatutTransfert.PAYE) {
                payes++;
                if (transfert.getMontantRecu() != null) {
                    montantTotalPaye = montantTotalPaye.add(transfert.getMontantRecu());
                }
            } else if (transfert.getStatut() == StatutTransfert.ANNULE) {
                annules++;
            }

            if (transfert.getMontantEnvoye() != null) {
                montantTotalEnvoye = montantTotalEnvoye.add(transfert.getMontantEnvoye());
            }
            if (transfert.getFrais() != null) {
                fraisTotal = fraisTotal.add(transfert.getFrais());
            }
        }

        stats.setEnAttente(enAttente);
        stats.setPayes(payes);
        stats.setAnnules(annules);
        stats.setMontantTotalEnvoye(montantTotalEnvoye);
        stats.setMontantTotalPaye(montantTotalPaye);
        stats.setFraisTotal(fraisTotal);

        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public TransfertResponse getTransfertById(Long id) {
        Transfert transfert = transfertRepository.findById(id)
<<<<<<< HEAD
                .orElseThrow(() -> new TransfertNotFoundException("Transfert introuvable"));
=======
                .orElseThrow(() -> new IllegalArgumentException("Transfert introuvable"));

>>>>>>> 78c7d10560abb4927f42ed9e093d2c396875add6
        return TransfertConverter.toResponse(transfert);
    }

    @Override
    @Transactional(readOnly = true)
    public TransfertResponse getByCodeRetrait(String codeRetrait) {
        Transfert transfert = transfertRepository
                .findByCodeRetrait(codeRetrait)
                .orElseThrow(() -> new TransfertNotFoundException("Transfert introuvable"));
        return TransfertConverter.toResponse(transfert);
    }

    @Override
    @Transactional(readOnly = true)
    public TransfertResponse getByTelephoneBeneficiaire(String telephone) {
        List<Transfert> resultats = transfertRepository
                .findByBeneficiairePhone(telephone);

        if (resultats.isEmpty()) {
            throw new TransfertNotFoundException("Aucun transfert trouvé pour ce numéro");
        }

        // Le premier = le plus récent (ORDER BY creeLe DESC)
        return TransfertConverter.toResponse(resultats.get(0));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransfertResponse> getMesTransferts(Long clientId) {
        return transfertRepository.findByExpediteurClientId(clientId)
                .stream()
                .map(TransfertConverter::toResponse)
                .toList();
    }

    // ── Écriture ──────────────────────────────────────────────────────────────

    @Override
    public TransfertResponse creerTransfert(CreateTransfertRequest request) {

        if (request.getMontant() == null || request.getMontant().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Montant invalide");
        }

        // Récupération des entités liées
        Client client = request.getClientId() == null
                ? null
                : utilisateurRepository.findClientById(request.getClientId())
                .orElseThrow(() -> new IllegalArgumentException("Client introuvable"));

        PieceIdentite pieceIdentite = null;
        if (request.getPieceIdentiteId() != null) {
            pieceIdentite = client == null
                    ? pieceIdentiteRepository.findById(request.getPieceIdentiteId())
                    .orElseThrow(() -> new IllegalArgumentException("Piece d'identite introuvable"))
                    : pieceIdentiteRepository.findByIdAndClientId(request.getPieceIdentiteId(), client.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Piece d'identite introuvable pour ce client"));
        }

        if (request.getAgentId() == null) {
            throw new IllegalArgumentException("Agent obligatoire pour enregistrer l'operation de caisse");
        }

        Agent agent = utilisateurRepository.findAgentById(request.getAgentId())
                .orElseThrow(() -> new IllegalArgumentException("Agent introuvable ou inactif"));

        Agence agenceEnvoi = request.getAgenceEnvoiId() == null
                ? null
                : agenceRepository.findById(request.getAgenceEnvoiId())
                .orElseThrow(() -> new IllegalArgumentException("Agence d'envoi introuvable"));

        Corridor corridor = request.getCorridorId() == null
                ? null
                : corridorRepository.findById(request.getCorridorId())
                .orElseThrow(() -> new IllegalArgumentException("Corridor introuvable"));

        GrilleTarifaire grilleTarifaire = request.getGrilleTarifaireId() == null
                ? null
                : grilleTarifaireRepository.findById(request.getGrilleTarifaireId())
                .orElseThrow(() -> new IllegalArgumentException("Grille tarifaire introuvable"));

        // Expéditeur
        Expediteur expediteur = new Expediteur();
        expediteur.setClient(client);
        expediteur.setPieceConfirmee(pieceIdentite);
        expediteur = expediteurRepository.save(expediteur);

        // Bénéficiaire
        Beneficiaire beneficiaire = new Beneficiaire();
        beneficiaire.setNom(request.getNomBeneficiaire());
        beneficiaire.setPrenom(request.getPrenomBeneficiaire());
        beneficiaire.setTelephone(request.getTelephoneBeneficiaire());
        beneficiaire.setPays(request.getPaysBeneficiaire());
        beneficiaire.setSurListeSurveillance(false);
        beneficiaire = beneficiaireRepository.save(beneficiaire);

        // Calcul des frais
        BigDecimal frais;
        if (grilleTarifaire != null) {
            if (grilleTarifaire.getFraisFixe() == null || grilleTarifaire.getFraisPourcentage() == null) {
                throw new IllegalArgumentException("Grille tarifaire incomplete");
            }
            if (grilleTarifaire.getMontantMin() != null
                    && request.getMontant().compareTo(grilleTarifaire.getMontantMin()) < 0) {
                throw new IllegalArgumentException("Montant inferieur au minimum de la grille tarifaire");
            }
            if (grilleTarifaire.getMontantMax() != null
                    && request.getMontant().compareTo(grilleTarifaire.getMontantMax()) > 0) {
                throw new IllegalArgumentException("Montant superieur au maximum de la grille tarifaire");
            }

            BigDecimal fraisVariables = request.getMontant()
                    .multiply(grilleTarifaire.getFraisPourcentage())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            frais = grilleTarifaire.getFraisFixe().add(fraisVariables);
        } else {
            // fallback 5% en attendant l'intégration complète
            frais = request.getMontant().multiply(BigDecimal.valueOf(0.05));
        }

        // Calcul montant reçu — converti en devise destination si corridor disponible
        BigDecimal montantNetMAD = request.getMontant().subtract(frais);
        BigDecimal montantRecu;

        if (corridor != null
                && corridor.getDeviseSource() != null
                && corridor.getDeviseDestination() != null
                && corridor.getDeviseDestination().getTauxVersEuro().compareTo(BigDecimal.ZERO) != 0) {

            BigDecimal taux = corridor.getDeviseSource().getTauxVersEuro()
                    .divide(corridor.getDeviseDestination().getTauxVersEuro(), 4, RoundingMode.HALF_UP);
            montantRecu = montantNetMAD.multiply(taux).setScale(2, RoundingMode.HALF_UP);
        } else {
            montantRecu = montantNetMAD;
        }

        // Génération des identifiants
        String codeRetrait = UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
        String numeroReference = "TRF-" + System.currentTimeMillis();

        // Construction du transfert
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
<<<<<<< HEAD
=======

        enregistrerOperationCaisse(transfert, agent, TypeOperation.ENVOI, transfert.getMontantEnvoye());

>>>>>>> 78c7d10560abb4927f42ed9e093d2c396875add6
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
            BigDecimal frais = request.getMontant().multiply(BigDecimal.valueOf(0.05));
            BigDecimal montantRecu = request.getMontant().subtract(frais);
            transfert.setMontantEnvoye(request.getMontant());
            transfert.setFrais(frais);
            transfert.setMontantRecu(montantRecu);
        }

        Beneficiaire beneficiaire = transfert.getBeneficiaire();
        if (beneficiaire != null) {
            if (request.getNomBeneficiaire() != null)
                beneficiaire.setNom(request.getNomBeneficiaire());
            if (request.getPrenomBeneficiaire() != null)
                beneficiaire.setPrenom(request.getPrenomBeneficiaire());
            if (request.getTelephoneBeneficiaire() != null)
                beneficiaire.setTelephone(request.getTelephoneBeneficiaire());
            if (request.getPaysBeneficiaire() != null)
                beneficiaire.setPays(request.getPaysBeneficiaire());
            beneficiaireRepository.save(beneficiaire);
        }

        transfert = transfertRepository.save(transfert);
        return TransfertConverter.toResponse(transfert);
    }

    @Override
    public TransfertResponse payerTransfert(PaiementRequest request) {
        Transfert transfert = transfertRepository
                .findByCodeRetrait(request.getCodeRetrait())
                .orElseThrow(() -> new TransfertNotFoundException("Transfert introuvable"));

        if (transfert.getStatut() == StatutTransfert.PAYE) {
            throw new RuntimeException("Transfert déjà payé");
        }

        Agent agentPaiement = request.getAgentId() == null
                ? transfert.getAgentSaisie()
                : utilisateurRepository.findAgentById(request.getAgentId())
                .orElseThrow(() -> new RuntimeException("Agent introuvable"));

        if (agentPaiement == null) {
            throw new RuntimeException("Agent de paiement obligatoire");
        }

        Agence agenceRetrait = request.getAgenceRetraitId() == null
                ? null
                : agenceRepository.findById(request.getAgenceRetraitId())
                .orElseThrow(() -> new RuntimeException("Agence de retrait introuvable"));

        BigDecimal soldeActuel = agentPaiement.getSoldeCaisse() == null
                ? BigDecimal.ZERO
                : agentPaiement.getSoldeCaisse();

        if (soldeActuel.compareTo(transfert.getMontantRecu()) < 0) {
            throw new RuntimeException("Solde caisse insuffisant pour payer ce transfert");
        }

        transfert.setStatut(StatutTransfert.PAYE);
        transfert.setPayeLe(LocalDateTime.now());
<<<<<<< HEAD
        transfert.setAgenceRetrait(null); // TODO: brancher agenceRetraitId
        transfert.setTypePieceBeneficiaire(request.getTypePieceBeneficiaire());
        transfert.setNumeroPieceBeneficiaire(request.getNumeroPieceBeneficiaire());

        transfert = transfertRepository.save(transfert);
=======

        transfert.setAgenceRetrait(agenceRetrait);

        transfert = transfertRepository.save(transfert);

        enregistrerOperationCaisse(transfert, agentPaiement, TypeOperation.RETRAIT, transfert.getMontantRecu());

>>>>>>> 78c7d10560abb4927f42ed9e093d2c396875add6
        return TransfertConverter.toResponse(transfert);
    }

    @Override
<<<<<<< HEAD
=======
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

    private void enregistrerOperationCaisse(Transfert transfert, Agent agent, TypeOperation type, BigDecimal montant) {
        if (agent == null || montant == null) {
            return;
        }

        CaisseOperation operation = new CaisseOperation();
        operation.setAgent(agent);
        operation.setTransfert(transfert);
        operation.setReferenceTransfert(transfert.getNumeroReference());
        operation.setType(type);
        operation.setMontant(montant);
        operation.setDateHeure(LocalDateTime.now());

        caisseOperationRepository.save(operation);

        BigDecimal soldeActuel = agent.getSoldeCaisse() == null
                ? BigDecimal.ZERO
                : agent.getSoldeCaisse();

        if (type == TypeOperation.RETRAIT) {
            agent.setSoldeCaisse(soldeActuel.subtract(montant));
        } else {
            agent.setSoldeCaisse(soldeActuel.add(montant));
        }

        utilisateurRepository.save(agent);
    }

    @Override
    public BigDecimal commissionsAgent(String email, LocalDate debut, LocalDate fin) {
        return transfertRepository.sumCommissionsAgent(
                email,
                debut.atStartOfDay(),
                fin.atTime(23, 59, 59)
        );
    }
    @Override
>>>>>>> 78c7d10560abb4927f42ed9e093d2c396875add6
    public TransfertResponse annulerTransfert(Long id) {
        Transfert transfert = transfertRepository.findById(id)
                .orElseThrow(() -> new TransfertNotFoundException("Transfert introuvable"));

        transfert.setStatut(StatutTransfert.ANNULE);
        transfert = transfertRepository.save(transfert);
        return TransfertConverter.toResponse(transfert);
    }
<<<<<<< HEAD

    // Je n'ai pas implémenté DELETE /api/transferts/{id} car les transactions doivent rester toujours traçables.
    // À la place, j'ai créé un endpoint d'annulation de transaction.
}
=======
}
>>>>>>> 78c7d10560abb4927f42ed9e093d2c396875add6
