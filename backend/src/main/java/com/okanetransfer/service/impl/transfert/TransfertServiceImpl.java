package com.okanetransfer.service.impl.transfert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.okanetransfer.repository.devise.PaysRepository;
import com.okanetransfer.repository.transfert.BeneficiaireRepository;
import com.okanetransfer.repository.transfert.ExpediteurRepository;
import com.okanetransfer.repository.transfert.TransfertRepository;
import com.okanetransfer.repository.user.PieceIdentiteRepository;
import com.okanetransfer.repository.user.UtilisateurRepository;
import com.okanetransfer.service.converter.transfert.BeneficiaireConverter;
import com.okanetransfer.service.converter.transfert.TransfertConverter;
import com.okanetransfer.service.dto.transfert.request.CreateTransfertAvecNouveauClientRequest;
import com.okanetransfer.service.dto.transfert.request.CreateTransfertRequest;
import com.okanetransfer.service.dto.transfert.request.PaiementRequest;
import com.okanetransfer.service.dto.transfert.request.SendTransfertReceiptEmailRequest;
import com.okanetransfer.service.dto.transfert.request.UpdateTransfertRequest;
import com.okanetransfer.service.dto.transfert.response.BeneficiaireResponse;
import com.okanetransfer.service.dto.transfert.response.ClientStatsResponse;
import com.okanetransfer.service.dto.transfert.response.TransfertResponse;
import com.okanetransfer.service.dto.transfert.response.TransfertStatsResponse;
import com.okanetransfer.service.dto.user.request.PieceIdentiteRequest;
import com.okanetransfer.service.dto.user.response.PieceIdentiteResponse;
import com.okanetransfer.service.facade.transfert.ITransfertService;
import com.okanetransfer.service.facade.user.PieceIdentiteService;
import com.okanetransfer.shared.enums.RoleUtilisateur;
import com.okanetransfer.shared.enums.StatutTransfert;
import com.okanetransfer.shared.enums.TypeOperation;
import com.okanetransfer.shared.exception.TransfertNotFoundException;
import com.okanetransfer.shared.util.CodeGenerator;

import jakarta.mail.internet.MimeMessage;

@Service
@Transactional
public class TransfertServiceImpl implements ITransfertService {

    private final TransfertRepository       transfertRepository;
    private final BeneficiaireRepository    beneficiaireRepository;
    private final ExpediteurRepository      expediteurRepository;
    private final UtilisateurRepository     utilisateurRepository;
    private final PieceIdentiteRepository   pieceIdentiteRepository;
    private final AgenceRepository          agenceRepository;
    private final CorridorRepository        corridorRepository;
    private final GrilleTarifaireRepository grilleTarifaireRepository;
    private final PaysRepository            paysRepository;
    private final PieceIdentiteService      pieceIdentiteService;
    private final CaisseOperationRepository caisseOperationRepository;
    private final CodeGenerator             codeGenerator;
    private final PasswordEncoder           passwordEncoder;
    private final JavaMailSender            javaMailSender;
    private final String                    mailFrom;

    public TransfertServiceImpl(
            TransfertRepository transfertRepository,
            BeneficiaireRepository beneficiaireRepository,
            ExpediteurRepository expediteurRepository,
            UtilisateurRepository utilisateurRepository,
            PieceIdentiteRepository pieceIdentiteRepository,
            AgenceRepository agenceRepository,
            CorridorRepository corridorRepository,
            GrilleTarifaireRepository grilleTarifaireRepository,
            PaysRepository paysRepository,
            PieceIdentiteService pieceIdentiteService,
            CaisseOperationRepository caisseOperationRepository,
            CodeGenerator codeGenerator,
            PasswordEncoder passwordEncoder,
            JavaMailSender javaMailSender,
            @Value("${mail.from:no-reply@okanetransfer.local}") String mailFrom) {

        this.transfertRepository        = transfertRepository;
        this.beneficiaireRepository     = beneficiaireRepository;
        this.expediteurRepository       = expediteurRepository;
        this.utilisateurRepository      = utilisateurRepository;
        this.pieceIdentiteRepository    = pieceIdentiteRepository;
        this.agenceRepository           = agenceRepository;
        this.corridorRepository         = corridorRepository;
        this.grilleTarifaireRepository  = grilleTarifaireRepository;
        this.paysRepository             = paysRepository;
        this.pieceIdentiteService       = pieceIdentiteService;
        this.caisseOperationRepository  = caisseOperationRepository;
        this.codeGenerator              = codeGenerator;
        this.passwordEncoder            = passwordEncoder;
        this.javaMailSender             = javaMailSender;
        this.mailFrom                   = mailFrom;
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
                .orElseThrow(() -> new TransfertNotFoundException("Transfert introuvable"));
        return TransfertConverter.toResponse(transfert);
    }

    @Override
    @Transactional(readOnly = true)
    public TransfertResponse getByCodeRetrait(String codeRetrait) {
        String codeNormalise = codeGenerator.normaliseCodeRetrait(codeRetrait);
        Transfert transfert = transfertRepository
                .findByCodeRetraitNormalise(codeNormalise)
                .orElseThrow(() -> new TransfertNotFoundException("Transfert introuvable"));
        return TransfertConverter.toResponse(transfert);
    }

    @Override
    @Transactional(readOnly = true)
    public TransfertResponse getByTelephoneBeneficiaire(String telephone) {
        List<Transfert> resultats = transfertRepository.findByBeneficiairePhone(telephone);
        if (resultats.isEmpty()) {
            throw new TransfertNotFoundException("Aucun transfert trouvé pour ce numéro");
        }
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

    @Override
    @Transactional(readOnly = true)
    public BigDecimal commissionsAgent(String email, LocalDate debut, LocalDate fin) {
        return transfertRepository.sumCommissionsAgent(
                email,
                debut.atStartOfDay(),
                fin.atTime(23, 59, 59)
        );
    }

    // ── Écriture ──────────────────────────────────────────────────────────────

    @Override
    public TransfertResponse creerTransfert(CreateTransfertRequest request) {

        if (request.getMontant() == null || request.getMontant().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Montant invalide");
        }

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
                ? agent.getAgence()
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

        // Auto-reset plafond if it's a new day
        if (agenceEnvoi.getDateDernierReset() == null
                || !LocalDate.now().equals(agenceEnvoi.getDateDernierReset())) {
            agenceEnvoi.setMontantTraiteAujourdhui(BigDecimal.ZERO);
            agenceEnvoi.setDateDernierReset(LocalDate.now());
        }

        // Vérification du plafond journalier de l'agence
        BigDecimal montantActuel = agenceEnvoi.getMontantTraiteAujourdhui() != null
                ? agenceEnvoi.getMontantTraiteAujourdhui()
                : BigDecimal.ZERO;
        BigDecimal totalApres = montantActuel.add(request.getMontant());
        if (agenceEnvoi.getPlafondJournalier() != null
                && totalApres.compareTo(agenceEnvoi.getPlafondJournalier()) > 0) {
            BigDecimal disponible = agenceEnvoi.getPlafondJournalier().subtract(montantActuel);
            throw new IllegalArgumentException(
                "Plafond journalier de l'agence dépassé. Disponible : " + disponible + " MAD");
        }

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
            frais = request.getMontant().multiply(BigDecimal.valueOf(0.05));
        }

        BigDecimal commissionAgence = grilleTarifaire != null && grilleTarifaire.getPartAgence() != null
                ? grilleTarifaire.getPartAgence()
                : BigDecimal.ZERO;

        BigDecimal montantNetMAD = request.getMontant().subtract(frais).subtract(commissionAgence);
        BigDecimal montantRecu;

        if (corridor != null
                && corridor.getPaysSource() != null && corridor.getPaysSource().getDevise() != null
                && corridor.getPaysDestination() != null && corridor.getPaysDestination().getDevise() != null
                && corridor.getPaysDestination().getDevise().getTauxVersEuro().compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal taux = corridor.getPaysSource().getDevise().getTauxVersEuro()
                    .divide(corridor.getPaysDestination().getDevise().getTauxVersEuro(), 4, RoundingMode.HALF_UP);
            montantRecu = montantNetMAD.multiply(taux).setScale(2, RoundingMode.HALF_UP);
        } else {
            montantRecu = montantNetMAD;
        }

        String codeRetrait = codeGenerator.generateCodeRetrait();
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

        // Mise à jour du montant traité + soldeCaisseAgence (envoi = agence receives cash from sender)
        agenceEnvoi.setMontantTraiteAujourdhui(montantActuel.add(request.getMontant()));
        BigDecimal soldeAgence = agenceEnvoi.getSoldeCaisseAgence() != null
                ? agenceEnvoi.getSoldeCaisseAgence() : BigDecimal.ZERO;
        agenceEnvoi.setSoldeCaisseAgence(soldeAgence.add(request.getMontant()));
        agenceRepository.save(agenceEnvoi);

        enregistrerOperationCaisse(transfert, agent, TypeOperation.ENVOI, transfert.getMontantEnvoye());

        return TransfertConverter.toResponse(transfert);
    }

    @Override
    public TransfertResponse creerTransfertAvecNouveauClient(CreateTransfertAvecNouveauClientRequest request) {
        validerTransfertAvecNouveauClient(request);

        CreateTransfertAvecNouveauClientRequest.NouveauClientRequest nouveauClientRequest =
                request.getNouveauClient();

        String telephoneClient = normaliserTelephone(nouveauClientRequest.getTelephone());
        String emailClient = nouveauClientRequest.getEmail().trim();

        if (utilisateurRepository.findClientByTelephone(telephoneClient).isPresent()) {
            throw new IllegalArgumentException("Un client actif existe déjà avec ce téléphone.");
        }
        if (utilisateurRepository.existsByEmail(emailClient)) {
            throw new IllegalArgumentException("Un client actif existe déjà avec cet email.");
        }

        Client client = new Client();
        client.setNom(nouveauClientRequest.getNom().trim());
        client.setPrenom(nouveauClientRequest.getPrenom().trim());
        client.setTelephone(telephoneClient);
        client.setEmail(emailClient);
        client.setMotDePasseHash(passwordEncoder.encode(nouveauClientRequest.getMotDePasse()));
        client.setPays(paysRepository.findByNom(nouveauClientRequest.getPays().trim()).orElse(null));
        client.setRole(RoleUtilisateur.ROLE_CLIENT);
        client.setActif(true);
        client.setTwoFactorActive(false);
        client = utilisateurRepository.save(client);

        PieceIdentiteRequest pieceRequest = request.getPieceIdentite();
        pieceRequest.setNumero(pieceRequest.getNumero().trim());
        pieceRequest.setType(pieceRequest.getType().trim());
        pieceRequest.setPaysEmetteur(pieceRequest.getPaysEmetteur().trim());
        PieceIdentiteResponse piece = pieceIdentiteService.ajouterPiece(client.getId(), pieceRequest);

        CreateTransfertRequest transfertRequest = new CreateTransfertRequest();
        transfertRequest.setClientId(client.getId());
        transfertRequest.setPieceIdentiteId(piece.getId());
        transfertRequest.setAgentId(request.getAgentId());
        transfertRequest.setAgenceEnvoiId(request.getAgenceEnvoiId());
        transfertRequest.setCorridorId(request.getCorridorId());
        transfertRequest.setGrilleTarifaireId(request.getGrilleTarifaireId());
        transfertRequest.setNomBeneficiaire(request.getNomBeneficiaire().trim());
        transfertRequest.setPrenomBeneficiaire(request.getPrenomBeneficiaire().trim());
        transfertRequest.setTelephoneBeneficiaire(normaliserTelephone(request.getTelephoneBeneficiaire()));
        transfertRequest.setPaysBeneficiaire(request.getPaysBeneficiaire().trim());
        transfertRequest.setMontant(request.getMontant());

        return creerTransfert(transfertRequest);
    }

    @Override
    public void envoyerRecuParEmail(SendTransfertReceiptEmailRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La demande d'envoi du reçu est obligatoire.");
        }

        String codeRetrait = codeGenerator.normaliseCodeRetrait(request.getCodeRetrait());
        if (isBlank(codeRetrait)) {
            throw new IllegalArgumentException("Le code de retrait est obligatoire.");
        }

        Transfert transfert = transfertRepository.findByCodeRetraitNormalise(codeRetrait)
                .orElseThrow(() -> new TransfertNotFoundException("Transfert introuvable"));

        String destinataireEmail = resolveDestinataireEmail(request, transfert);
        if (isBlank(destinataireEmail)) {
            throw new IllegalArgumentException("Aucune adresse email disponible pour ce reçu.");
        }

        TransfertResponse response = TransfertConverter.toResponse(transfert);
        String html = buildReceiptEmailHtml(response, destinataireEmail.trim());

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );
            helper.setFrom(mailFrom, "Okane Transfer");
            helper.setTo(destinataireEmail.trim());
            helper.setSubject("[Okane Transfer] Reçu de votre transfert - Réf: " + response.getNumeroReference());

            String plainText = """
                    Okane Transfer - Reçu officiel de transfert

                    Merci d'avoir choisi Okane Transfer pour vos envois d'argent.

                    Code de retrait obligatoire : %s
                    Référence de transaction : %s

                    Expéditeur : %s (%s)
                    Bénéficiaire : %s (%s)
                    Pays de réception : %s
                    Date d'expiration : %s

                    Détails Financiers :
                    - Montant envoyé : %s MAD
                    - Frais de service : %s MAD
                    - Commission agence : %s MAD
                    - Montant à recevoir : %s %s

                    Ce reçu a été généré automatiquement pour la transaction %s.
                    Veuillez ne pas partager ce code de retrait avec des tiers non autorisés.
                    """.formatted(
                    response.getCodeRetrait(),
                    response.getNumeroReference(),
                    response.getNomExpediteur(),
                    response.getTelephoneExpediteur(),
                    response.getNomBeneficiaire(),
                    response.getTelephoneBeneficiaire(),
                    response.getPaysBeneficiaire(),
                    response.getExpireLe() != null ? response.getExpireLe().toLocalDate().toString() : "—",
                    formatMoney(response.getMontantEnvoye()),
                    formatMoney(response.getFrais()),
                    formatMoney(response.getPartAgence()),
                    formatMoney(response.getMontantRecu()),
                    response.getDeviseReception(),
                    response.getCodeRetrait()
            );

            helper.setText(plainText, html);
            javaMailSender.send(message);
        } catch (Exception exception) {
            throw new RuntimeException("Impossible d'envoyer le reçu par email.", exception);
        }
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

        // Convert montantRecu (dest currency) to MAD before comparing with soldeCaisse (MAD)
        BigDecimal montantRecuEnMAD = montantToMAD(transfert, transfert.getMontantRecu());
        if (soldeActuel.compareTo(montantRecuEnMAD) < 0) {
            BigDecimal manquant = montantRecuEnMAD.subtract(soldeActuel).setScale(2, RoundingMode.HALF_UP);
            throw new RuntimeException(
                "Solde caisse insuffisant pour payer ce transfert. Manque : " + manquant + " MAD");
        }

        transfert.setStatut(StatutTransfert.PAYE);
        transfert.setPayeLe(LocalDateTime.now());
        transfert.setAgenceRetrait(agenceRetrait);
        transfert.setTypePieceBeneficiaire(request.getTypePieceBeneficiaire());
        transfert.setNumeroPieceBeneficiaire(request.getNumeroPieceBeneficiaire());

        transfert = transfertRepository.save(transfert);

        // Pass original montantRecu for display; method converts to MAD internally for soldeCaisse
        enregistrerOperationCaisse(transfert, agentPaiement, TypeOperation.RETRAIT, transfert.getMontantRecu());

        return TransfertConverter.toResponse(transfert);
    }

    @Override
    public TransfertResponse annulerTransfert(Long id) {
        Transfert transfert = transfertRepository.findById(id)
                .orElseThrow(() -> new TransfertNotFoundException("Transfert introuvable"));

        transfert.setStatut(StatutTransfert.ANNULE);
        transfert = transfertRepository.save(transfert);
        return TransfertConverter.toResponse(transfert);
    }

    // ── Helpers privés ────────────────────────────────────────────────────────

    /**
     * Converts an amount in the transfert's destination devise to MAD via EUR pivot.
     * Used to keep soldeCaisse always in MAD.
     */
    private BigDecimal montantToMAD(Transfert transfert, BigDecimal montant) {
        if (transfert.getCorridor() == null) return montant;
        var corridor = transfert.getCorridor();
        if (corridor.getPaysDestination() == null || corridor.getPaysDestination().getDevise() == null) return montant;
        if (corridor.getPaysSource()      == null || corridor.getPaysSource().getDevise()      == null) return montant;

        BigDecimal tauxDest = corridor.getPaysDestination().getDevise().getTauxVersEuro(); // e.g. EUR=1.0, XOF=0.001524
        BigDecimal tauxSrc  = corridor.getPaysSource().getDevise().getTauxVersEuro();       // MAD=0.093

        if (tauxSrc.compareTo(BigDecimal.ZERO) == 0) return montant;
        // amount_dest × tauxDest / tauxSrc → MAD
        return montant.multiply(tauxDest).divide(tauxSrc, 2, RoundingMode.HALF_UP);
    }

    private void enregistrerOperationCaisse(Transfert transfert, Agent agent, TypeOperation type, BigDecimal montant) {
        if (agent == null || montant == null) {
            return;
        }

        // Store original amount (in actual currency) for display in caisse history
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

        // soldeCaisse always in MAD — convert RETRAIT (foreign currency) before updating balance
        if (type == TypeOperation.RETRAIT) {
            BigDecimal montantEnMAD = montantToMAD(transfert, montant);
            agent.setSoldeCaisse(soldeActuel.subtract(montantEnMAD));
        } else {
            agent.setSoldeCaisse(soldeActuel.add(montant));
        }

        utilisateurRepository.save(agent);
    }

    private void validerTransfertAvecNouveauClient(CreateTransfertAvecNouveauClientRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La demande de transfert est obligatoire.");
        }
        if (request.getMontant() == null || request.getMontant().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Montant invalide.");
        }
        if (request.getNouveauClient() == null) {
            throw new IllegalArgumentException("Les informations du nouveau client sont obligatoires.");
        }
        if (isBlank(request.getNouveauClient().getNom())) {
            throw new IllegalArgumentException("Le nom du client est obligatoire.");
        }
        if (isBlank(request.getNouveauClient().getPrenom())) {
            throw new IllegalArgumentException("Le prénom du client est obligatoire.");
        }
        if (isBlank(request.getNouveauClient().getEmail())) {
            throw new IllegalArgumentException("L'email du client est obligatoire.");
        }
        if (isBlank(request.getNouveauClient().getMotDePasse())) {
            throw new IllegalArgumentException("Le mot de passe du client est obligatoire.");
        }
        if (isBlank(request.getNouveauClient().getPays())) {
            throw new IllegalArgumentException("Le pays du client est obligatoire.");
        }
        validerTelephone(request.getNouveauClient().getTelephone(), "Format téléphone client invalide.");
        validerEmail(request.getNouveauClient().getEmail());
        validerMotDePasse(request.getNouveauClient().getMotDePasse());

        if (request.getPieceIdentite() == null) {
            throw new IllegalArgumentException("La pièce d'identité est obligatoire.");
        }
        if (isBlank(request.getPieceIdentite().getNumero())) {
            throw new IllegalArgumentException("Le numéro de pièce est obligatoire.");
        }
        if (isBlank(request.getPieceIdentite().getType())) {
            throw new IllegalArgumentException("Le type de pièce est obligatoire.");
        }
        if (!request.getPieceIdentite().getType().trim().matches("^(CIN|PASSEPORT|CARTE_SEJOUR|PERMIS)$")) {
            throw new IllegalArgumentException("Type de pièce invalide.");
        }
        if (isBlank(request.getPieceIdentite().getPaysEmetteur())) {
            throw new IllegalArgumentException("Le pays émetteur est obligatoire.");
        }
        if (isBlank(request.getNomBeneficiaire())) {
            throw new IllegalArgumentException("Le nom du bénéficiaire est obligatoire.");
        }
        if (isBlank(request.getPrenomBeneficiaire())) {
            throw new IllegalArgumentException("Le prénom du bénéficiaire est obligatoire.");
        }
        if (isBlank(request.getPaysBeneficiaire())) {
            throw new IllegalArgumentException("Le pays du bénéficiaire est obligatoire.");
        }
        validerTelephone(request.getTelephoneBeneficiaire(), "Format téléphone bénéficiaire invalide.");
    }

    private void validerTelephone(String telephone, String message) {
        String normalized = normaliserTelephone(telephone);
        if (!normalized.matches("^\\+?[0-9]{8,15}$")) {
            throw new IllegalArgumentException(message);
        }
    }

    private void validerEmail(String email) {
        String normalized = email == null ? "" : email.trim();
        if (!normalized.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Format email invalide.");
        }
    }

    private void validerMotDePasse(String motDePasse) {
        if (motDePasse == null || !motDePasse.matches("^(?=.*[A-Z])(?=.*[0-9]).{8,}$")) {
            throw new IllegalArgumentException(
                    "Le mot de passe doit contenir au moins 8 caractères, 1 majuscule et 1 chiffre.");
        }
    }

    private String resolveDestinataireEmail(SendTransfertReceiptEmailRequest request, Transfert transfert) {
        if (!isBlank(request.getDestinataireEmail())) {
            return request.getDestinataireEmail().trim();
        }
        if (transfert.getExpediteur() != null && transfert.getExpediteur().getClient() != null) {
            String emailClient = transfert.getExpediteur().getClient().getEmail();
            if (!isBlank(emailClient)) {
                return emailClient.trim();
            }
        }
        return null;
    }

    private String buildReceiptEmailHtml(TransfertResponse transfert, String destinataireEmail) {
        String expediteur = safe(transfert.getNomExpediteur());
        String beneficiaire = safe(transfert.getNomBeneficiaire());
        String deviseRecu = safe(transfert.getDeviseReception());
        String codeRetrait = safe(transfert.getCodeRetrait());
        String reference = safe(transfert.getNumeroReference());
        String telephoneExpediteur = safe(transfert.getTelephoneExpediteur());
        String telephoneBeneficiaire = safe(transfert.getTelephoneBeneficiaire());
        String paysReception = safe(transfert.getPaysBeneficiaire());
        String montantEnvoye = formatMoney(transfert.getMontantEnvoye());
        String frais = formatMoney(transfert.getFrais());
        String commission = formatMoney(transfert.getPartAgence());
        String montantRecu = formatMoney(transfert.getMontantRecu());
        String expiration = transfert.getExpireLe() != null
                ? transfert.getExpireLe().toLocalDate().toString()
                : "—";

        return """
                <html>
                <head>
                    <meta charset="utf-8" />
                    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                    <title>Reçu de transfert - Okane Transfer</title>
                    <style>
                        body { margin:0; padding:0; background-color:#f1f5f9;
                               font-family:'Plus Jakarta Sans',-apple-system,BlinkMacSystemFont,"Segoe UI",Roboto,Helvetica,Arial,sans-serif; }
                        .wrapper { width:100%%; background-color:#f1f5f9; padding:40px 20px; box-sizing:border-box; }
                        .container { max-width:600px; margin:0 auto; background-color:#ffffff;
                                     border-radius:16px; overflow:hidden;
                                     box-shadow:0 10px 15px -3px rgba(0,0,0,.05),0 4px 6px -4px rgba(0,0,0,.05);
                                     border:1px solid #e2e8f0; }
                        .header { background-color:#0f172a; padding:32px; text-align:center; }
                        .brand { color:#ffffff; font-size:24px; font-weight:800; letter-spacing:-.025em; margin:0; text-transform:uppercase; }
                        .brand span { color:#fbbf24; }
                        .subtitle { color:#94a3b8; font-size:11px; margin-top:6px; margin-bottom:0;
                                    font-weight:600; letter-spacing:.08em; text-transform:uppercase; }
                        .content { padding:32px; }
                        .badge-container { text-align:center; margin-bottom:24px; }
                        .badge { display:inline-block; background-color:#fef3c7; color:#d97706;
                                 font-size:12px; font-weight:700; padding:6px 14px;
                                 border-radius:9999px; border:1px solid #fde68a; }
                        .code-section { background:linear-gradient(135deg,#fef3c7 0%%,#fffbeb 100%%);
                                        border:2px dashed #f59e0b; border-radius:12px;
                                        padding:24px; text-align:center; margin-bottom:32px; }
                        .code-label { font-size:11px; text-transform:uppercase; letter-spacing:.1em;
                                      color:#b45309; font-weight:700; margin-bottom:8px; }
                        .code-value { font-family:'Courier New',Courier,monospace; font-size:36px;
                                      font-weight:800; color:#78350f; letter-spacing:4px; margin:0; }
                        .reference-text { font-size:12px; color:#64748b; text-align:center;
                                          margin-top:-16px; margin-bottom:32px; }
                        .reference-text strong { color:#334155; }
                        .section-title { font-size:13px; font-weight:700; color:#1e293b;
                                         text-transform:uppercase; letter-spacing:.05em;
                                         margin-bottom:16px; border-bottom:1px solid #f1f5f9; padding-bottom:8px; }
                        .detail-card { background-color:#f8fafc; border:1px solid #f1f5f9;
                                       border-radius:10px; padding:16px; margin-bottom:16px; }
                        .detail-card-title { font-size:10px; text-transform:uppercase; letter-spacing:.05em;
                                             color:#64748b; font-weight:600; margin-bottom:6px; }
                        .detail-card-value { font-size:15px; font-weight:700; color:#0f172a; margin:0; }
                        .detail-card-sub { font-size:13px; color:#64748b; margin-top:4px; margin-bottom:0; }
                        .invoice-table { width:100%%; border-collapse:collapse; margin-bottom:8px; }
                        .invoice-row { border-bottom:1px solid #f1f5f9; }
                        .invoice-row.total { border-top:2px solid #e2e8f0; border-bottom:none; background-color:#f8fafc; }
                        .invoice-label { padding:14px 0; font-size:14px; color:#64748b; font-weight:500; }
                        .invoice-value { padding:14px 0; font-size:14px; color:#0f172a; font-weight:700; text-align:right; }
                        .invoice-row.total .invoice-label { padding:18px 16px; color:#0f172a; font-weight:700; }
                        .invoice-row.total .invoice-value { padding:18px 16px; color:#d97706; font-size:20px; font-weight:800; text-align:right; }
                        .footer { background-color:#f8fafc; padding:24px 32px; text-align:center; border-top:1px solid #e2e8f0; }
                        .footer-text { font-size:12px; color:#64748b; line-height:1.6; margin:0; }
                        .footer-disclaimer { font-size:11px; color:#94a3b8; margin-top:12px; line-height:1.5; }
                    </style>
                </head>
                <body>
                    <div class="wrapper">
                        <div class="container">
                            <div class="header">
                                <h1 class="brand">Okane <span>Transfer</span></h1>
                                <p class="subtitle">Reçu officiel de transfert d'argent</p>
                            </div>
                            <div class="content">
                                <div class="badge-container">
                                    <span class="badge">Envoyé à %s</span>
                                </div>
                                <div class="code-section">
                                    <div class="code-label">Code de Retrait Obligatoire</div>
                                    <div class="code-value">%s</div>
                                </div>
                                <div class="reference-text">
                                    Référence de transaction : <strong>%s</strong>
                                </div>
                                <div class="section-title">Informations de Transfert</div>
                                <table style="width:100%%;border-spacing:0;margin-bottom:8px;">
                                    <tr>
                                        <td style="width:48%%;padding:0;vertical-align:top;">
                                            <div class="detail-card">
                                                <div class="detail-card-title">Expéditeur</div>
                                                <div class="detail-card-value">%s</div>
                                                <p class="detail-card-sub">%s</p>
                                            </div>
                                        </td>
                                        <td style="width:4%%;"></td>
                                        <td style="width:48%%;padding:0;vertical-align:top;">
                                            <div class="detail-card">
                                                <div class="detail-card-title">Bénéficiaire</div>
                                                <div class="detail-card-value">%s</div>
                                                <p class="detail-card-sub">%s</p>
                                            </div>
                                        </td>
                                    </tr>
                                </table>
                                <table style="width:100%%;border-spacing:0;margin-bottom:24px;">
                                    <tr>
                                        <td style="width:48%%;padding:0;vertical-align:top;">
                                            <div class="detail-card">
                                                <div class="detail-card-title">Pays de réception</div>
                                                <div class="detail-card-value">%s</div>
                                            </div>
                                        </td>
                                        <td style="width:4%%;"></td>
                                        <td style="width:48%%;padding:0;vertical-align:top;">
                                            <div class="detail-card">
                                                <div class="detail-card-title">Date d'expiration</div>
                                                <div class="detail-card-value">%s</div>
                                            </div>
                                        </td>
                                    </tr>
                                </table>
                                <div class="section-title">Détails Financiers</div>
                                <table class="invoice-table">
                                    <tr class="invoice-row">
                                        <td class="invoice-label">Montant envoyé</td>
                                        <td class="invoice-value">%s MAD</td>
                                    </tr>
                                    <tr class="invoice-row">
                                        <td class="invoice-label">Frais de service</td>
                                        <td class="invoice-value">%s MAD</td>
                                    </tr>
                                    <tr class="invoice-row">
                                        <td class="invoice-label">Commission agence</td>
                                        <td class="invoice-value">%s MAD</td>
                                    </tr>
                                    <tr class="invoice-row total">
                                        <td class="invoice-label">Montant à recevoir</td>
                                        <td class="invoice-value">%s %s</td>
                                    </tr>
                                </table>
                            </div>
                            <div class="footer">
                                <p class="footer-text">
                                    Ce reçu officiel a été généré automatiquement pour la transaction <strong>%s</strong>.
                                </p>
                                <p class="footer-disclaimer">
                                    Veuillez ne pas partager ce code de retrait avec des tiers non autorisés.
                                    Pour toute assistance, contactez notre service client.
                                </p>
                            </div>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(
                escapeHtml(destinataireEmail),
                escapeHtml(codeRetrait),
                escapeHtml(reference),
                escapeHtml(expediteur),
                escapeHtml(telephoneExpediteur),
                escapeHtml(beneficiaire),
                escapeHtml(telephoneBeneficiaire),
                escapeHtml(paysReception),
                escapeHtml(expiration),
                escapeHtml(montantEnvoye),
                escapeHtml(frais),
                escapeHtml(commission),
                escapeHtml(montantRecu),
                escapeHtml(deviseRecu),
                escapeHtml(codeRetrait)
        );
    }

    private String formatMoney(BigDecimal amount) {
        if (amount == null) return "0,00";
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String escapeHtml(String value) {
        if (value == null) return "";
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private String safe(String value) {
        return value == null ? "—" : value;
    }

    private String normaliserTelephone(String telephone) {
        if (telephone == null) return "";
        return telephone.replaceAll("\\s+", "").trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    @Override
    @Transactional(readOnly = true)
    public ClientStatsResponse getClientStats(String email) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime startOfLastMonth = startOfMonth.minusMonths(1);
        LocalDateTime endOfLastMonth = startOfMonth.minusSeconds(1);

        BigDecimal sentThisMonth = transfertRepository.sumMontantEnvoyeByClient(
                email, startOfMonth, now
        );
        BigDecimal sentLastMonth = transfertRepository.sumMontantEnvoyeByClient(
                email, startOfLastMonth, endOfLastMonth
        );

        BigDecimal change = sentThisMonth.subtract(sentLastMonth);
        Long beneficiairesCount = transfertRepository.countDistinctBeneficiaires(email);

        List<Transfert> recentTransferts = transfertRepository.findByExpediteurClientId(
                utilisateurRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"))
                        .getId()
        );

        List<BeneficiaireResponse> beneficiaires = recentTransferts.stream()
                .map(Transfert::getBeneficiaire)
                .distinct()
                .limit(4)
                .map(BeneficiaireConverter::toResponse)
                .toList();

        ClientStatsResponse response = new ClientStatsResponse();
        response.setSentThisMonth(sentThisMonth);
        response.setCurrency("MAD");
        response.setChangeVsLastMonth(change);
        response.setActiveBeneficiariesCount(beneficiairesCount.intValue());
        response.setBeneficiaries(beneficiaires);

        return response;
    }

    // Je n'ai pas implémenté DELETE /api/transferts/{id} car les transactions doivent rester toujours traçables.
    // À la place, j'ai créé un endpoint d'annulation de transaction.
}