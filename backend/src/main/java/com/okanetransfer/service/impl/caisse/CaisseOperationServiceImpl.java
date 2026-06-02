package com.okanetransfer.service.impl.caisse;

import com.okanetransfer.entity.agence.Agence;
import com.okanetransfer.entity.caisse.CaisseOperation;
import com.okanetransfer.entity.caisse.ClotureCaisse;
import com.okanetransfer.entity.user.Agent;
import com.okanetransfer.repository.agence.AgenceRepository;
import com.okanetransfer.repository.caisse.CaisseOperationRepository;
import com.okanetransfer.repository.user.UtilisateurRepository;
import com.okanetransfer.service.converter.caisse.CaisseOperationConverter;
import com.okanetransfer.service.dto.caisse.response.CaisseOperationResponse;
import com.okanetransfer.service.facade.caisse.CaisseOperationService;
import com.okanetransfer.service.facade.user.UtilisateurService;
import com.okanetransfer.shared.enums.TypeOperation;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CaisseOperationServiceImpl implements CaisseOperationService {

    @Override
    @Transactional(readOnly = true)
    public List<CaisseOperationResponse> findByAgentEmail(String email) {
        if (utilisateurService.getAgentByEmail(email) == null) {
            throw new EntityNotFoundException("Agent introuvable avec l'email : " + email);
        }
        return converter.toResponses(
                repository.findByAgentEmail(email)
        );
    }

    @Override
    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException(
                    "Opération introuvable avec l'id : " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public List<CaisseOperationResponse> findAll() {
        return converter.toResponses(repository.findAll());
    }

    @Override
    public CaisseOperationResponse ouvrirCaisse(String agentEmail, BigDecimal montantInitial) {
        Agent agent = utilisateurService.getAgentByEmail(agentEmail);
        if (agent == null) {
            throw new EntityNotFoundException("Agent introuvable : " + agentEmail);
        }

        // Check soldeCaisseAgence has enough to cover the float
        Agence agence = agent.getAgence();
        if (agence != null) {
            BigDecimal pool = agence.getSoldeCaisseAgence() != null
                    ? agence.getSoldeCaisseAgence() : BigDecimal.ZERO;
            if (montantInitial.compareTo(pool) > 0) {
                throw new IllegalArgumentException(
                    "Fonds insuffisants dans la caisse agence. Disponible : " + pool + " MAD");
            }
            agence.setSoldeCaisseAgence(pool.subtract(montantInitial));
            agenceRepository.save(agence);
        }

        // Set agent's starting balance
        agent.setSoldeCaisse(montantInitial);
        utilisateurRepository.save(agent);

        CaisseOperation operation = new CaisseOperation();
        operation.setAgent(agent);
        operation.setMontant(montantInitial);
        operation.setType(TypeOperation.OUVERTURE);
        operation.setDateHeure(LocalDateTime.now());

        return converter.toResponse(repository.save(operation));
    }

    @Override
    public BigDecimal consulterSolde(String agentEmail) {

        List<CaisseOperation> operations =
                repository.findByAgentEmail(agentEmail);

        BigDecimal solde = BigDecimal.ZERO;

        for (CaisseOperation op : operations) {

            switch (op.getType()) {

                case ENVOI:
                case OUVERTURE:
                    solde = solde.add(op.getMontant());
                    break;

                case RETRAIT:
                    solde = solde.subtract(op.getMontant());
                    break;

                case AJUSTEMENT:
                    solde = solde.add(op.getMontant());
                    break;

                case CLOTURE:
                    break;
            }
        }

        return solde;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaisseOperationResponse> historiqueOperations(String agentEmail) {

        return converter.toResponses(
                repository.findByAgentEmail(agentEmail)
        );
    }

    @Override
    public BigDecimal consulterSoldeDuJour(String agentEmail) {
        Agent agent = utilisateurService.getAgentByEmail(agentEmail);
        if (agent == null) throw new EntityNotFoundException("Agent introuvable : " + agentEmail);
        // soldeCaisse is the authoritative real-time balance (maintained by ouverture/envoi/retrait/clôture)
        return agent.getSoldeCaisse() != null ? agent.getSoldeCaisse() : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal consulterSoldePourDate(String agentEmail, LocalDate date) {
        Agent agent = (Agent) utilisateurService.getAgentByEmail(agentEmail);
        if (agent == null) {
            throw new EntityNotFoundException("Agent introuvable : " + agentEmail);
        }

        LocalDateTime debut = date.atStartOfDay();
        LocalDateTime fin   = date.plusDays(1).atStartOfDay().minusNanos(1);

        return repository.calculerSoldeTheorique(agent.getId(), debut, fin);
    }
    @Override
    @Transactional(readOnly = true)
    public List<CaisseOperationResponse> operationsDuJour(String agentEmail) {
        LocalDateTime debut = LocalDate.now().atStartOfDay();
        LocalDateTime fin   = LocalDate.now().atTime(23, 59, 59);
        return converter.toResponses(
                repository.findByAgentEmailAndDateHeureBetweenOrderByDateHeureAsc(agentEmail, debut, fin)
        );
    }

    @Override
    public List<CaisseOperationResponse> historiqueFiltre(
            String email, LocalDate dateDebut, LocalDate dateFin) {

        LocalDateTime from = dateDebut.atStartOfDay();
        LocalDateTime to   = dateFin.atTime(23, 59, 59);

        return repository.findByAgentEmailAndDateHeureBetweenOrderByDateHeureDesc(email, from, to)
                .stream()
                .map(converter::toResponse)
                .collect(Collectors.toList());
    }


    private final CaisseOperationRepository repository;
    private final CaisseOperationConverter  converter;
    private final UtilisateurService        utilisateurService;
    private final UtilisateurRepository     utilisateurRepository;
    private final AgenceRepository          agenceRepository;

    public CaisseOperationServiceImpl(CaisseOperationRepository repository,
                                      CaisseOperationConverter converter,
                                      UtilisateurService utilisateurService,
                                      UtilisateurRepository utilisateurRepository,
                                      AgenceRepository agenceRepository) {
        this.repository            = repository;
        this.converter             = converter;
        this.utilisateurService    = utilisateurService;
        this.utilisateurRepository = utilisateurRepository;
        this.agenceRepository      = agenceRepository;
    }
}