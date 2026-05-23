package com.okanetransfer.service.impl.caisse;

import com.okanetransfer.entity.caisse.CaisseOperation;
import com.okanetransfer.entity.caisse.ClotureCaisse;
import com.okanetransfer.entity.user.Agent;
import com.okanetransfer.repository.caisse.CaisseOperationRepository;
//import com.okanetransfer.repository.user.AgentRepository;
import com.okanetransfer.service.converter.caisse.CaisseOperationConverter;
import com.okanetransfer.service.dto.caisse.response.CaisseOperationResponse;
import com.okanetransfer.service.facade.caisse.CaisseOperationService;
import com.okanetransfer.shared.enums.TypeOperation;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CaisseOperationServiceImpl implements CaisseOperationService {

    @Override
    public List<CaisseOperationResponse> findByAgentEmail(String email) {
//        if (agentService.findByEmail(email)) {
//            throw new EntityNotFoundException("Agent introuvable avec l'email : " + email);
//        }
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
//        Agent agent = agentService.findByEmail(agentEmail)
//                .orElseThrow(() ->
//                        new EntityNotFoundException(
//                                "Agent introuvable : " + agentEmail
//                        ));
        Agent agent= new Agent();
        CaisseOperation operation = new CaisseOperation();

        operation.setAgent(agent);
        operation.setMontant(montantInitial);
        operation.setType(TypeOperation.OUVERTURE);
        operation.setDateHeure(LocalDateTime.now());

        return converter.toResponse(
                repository.save(operation)
        );

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
    public List<CaisseOperationResponse> historiqueOperations(String agentEmail) {

        return converter.toResponses(
                repository.findByAgentEmail(agentEmail)
        );
    }

    @Override
    public BigDecimal calculerSoldeTheorique(String agentEmail, LocalDateTime debut, LocalDateTime fin) {
        // 1. Récupération des opérations de la journée depuis la BDD
        List<CaisseOperation> operations = repository
                .findByAgentEmailAndDateHeureBetweenOrderByDateHeureAsc(agentEmail, debut, fin);

        BigDecimal solde = BigDecimal.ZERO;

        // 2. Calcul cumulé selon le type d'opération
        for (CaisseOperation op : operations) {
            switch (op.getType()) {
                case OUVERTURE:
                case ENVOI:
                case AJUSTEMENT:
                    solde = solde.add(op.getMontant());
                    break;

                case RETRAIT:
                    solde = solde.subtract(op.getMontant());
                    break;

                case CLOTURE:
                    // La clôture constate le solde physique, elle n'impacte pas le calcul théorique en soi
                    break;
            }
        }

        return solde;
    }


    private final CaisseOperationRepository repository;
    private final CaisseOperationConverter converter;

    public CaisseOperationServiceImpl(CaisseOperationRepository repository,
                                      CaisseOperationConverter converter) {
        this.repository = repository;
        this.converter = converter;
    }
}