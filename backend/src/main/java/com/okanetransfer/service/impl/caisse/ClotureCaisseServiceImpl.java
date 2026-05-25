package com.okanetransfer.service.impl.caisse;

import com.okanetransfer.entity.caisse.ClotureCaisse;
import com.okanetransfer.entity.user.Agent;
import com.okanetransfer.repository.caisse.CaisseOperationRepository;
import com.okanetransfer.repository.caisse.ClotureCaisseRepository;
//import com.okanetransfer.repository.user.AgentRepository;
import com.okanetransfer.service.converter.caisse.ClotureCaisseConverter;
import com.okanetransfer.service.dto.caisse.request.ClotureCaisseRequest;
import com.okanetransfer.service.dto.caisse.response.CaisseOperationResponse;
import com.okanetransfer.service.dto.caisse.response.ClotureCaisseResponse;
import com.okanetransfer.service.facade.caisse.CaisseOperationService;
import com.okanetransfer.service.facade.caisse.ClotureCaisseService;
import com.okanetransfer.service.facade.user.UtilisateurService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClotureCaisseServiceImpl implements ClotureCaisseService {

    @Override
    public ClotureCaisseResponse findByAgentEmailAndDate(String email, LocalDate date) {
        ClotureCaisse cloture = repository.findByAgentEmailAndDate(email, date);
        if (cloture == null) {
            throw new EntityNotFoundException(
                    "Clôture introuvable pour l'agent : " + email + " à la date : " + date);
        }
        return converter.toResponse(cloture);
    }

    @Override
    public List<ClotureCaisseResponse> findByEcartSignaleTrue() {
        return converter.toResponses(
                repository.findByEcartSignaleTrue()
        );
    }

    @Override
    public List<ClotureCaisseResponse> findByAgentEmailAndEcartSignaleTrue(String email) {
        //        if (agentRepository.findByEmail(email)) {
//            throw new EntityNotFoundException("Agent introuvable avec l'email : " + email);
//        }
        return converter.toResponses(
                repository.findByAgentEmailAndEcartSignaleTrue(email)
        );
    }

    @Override
    public ClotureCaisseResponse save(ClotureCaisseRequest request) {
        if (repository.findByAgentEmailAndDate(request.getAgentEmail(), request.getDate()) != null) {
            throw new IllegalArgumentException(
                    "Une clôture existe déjà pour cet agent à la date : " + request.getDate());
        }


        Agent agent = utilisateurService.getAgentByEmail(request.getAgentEmail());
        if (agent == null) {
            throw new EntityNotFoundException("Agent introuvable : " + request.getAgentEmail());
        }


        ClotureCaisse cloture = converter.toEntity(request);

        // calcul solde théorique sur la journée
        BigDecimal soldeTheorique = caisseOperationService
                .consulterSoldeDuJour(agent.getEmail());

        cloture.setSoldeTheorique(soldeTheorique);
        cloture.setEcart(request.getSoldeSaisi().subtract(soldeTheorique));

        // signaler automatiquement si écart non nul
        cloture.setEcartSignale(cloture.getEcart().compareTo(BigDecimal.ZERO) != 0);

        return converter.toResponse(repository.save(cloture));
    }

    @Override
    public ClotureCaisseResponse update(ClotureCaisseRequest request) {
        ClotureCaisse existing = repository.findByAgentEmailAndDate(
                request.getAgentEmail(), request.getDate());

        if (existing == null) {
            throw new EntityNotFoundException(
                    "Clôture introuvable pour l'agent : " + request.getAgentEmail()
                            + " à la date : " + request.getDate());
        }

        existing.setSoldeSaisi(request.getSoldeSaisi());

        // recalcul de l'écart
        BigDecimal ecart = request.getSoldeSaisi().subtract(existing.getSoldeTheorique());
        existing.setEcart(ecart);
        existing.setEcartSignale(ecart.compareTo(BigDecimal.ZERO) != 0);

        return converter.toResponse(repository.save(existing));
    }

    @Override
    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException(
                    "Clôture introuvable avec l'id : " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public List<ClotureCaisseResponse> findAll() {
        return converter.toResponses(repository.findAll());
    }

    @Override
    public ClotureCaisseResponse rapportCloture(String agentEmail, LocalDate date) {

        ClotureCaisse cloture = repository.findByAgentEmailAndDate(agentEmail, date);

        if (cloture == null) {
            throw new EntityNotFoundException(
                    "Aucune clôture trouvée"
            );
        }

        return converter.toResponse(cloture);
    }

    @Override
    public ClotureCaisseResponse cloturerCaisse(ClotureCaisseRequest request) {

//        Agent agent = agentService.findByEmail(request.getAgentEmail())
//                .orElseThrow(() ->
//                        new EntityNotFoundException(
//                                "Agent introuvable : "
//                                        + request.getAgentEmail()
//                        ));
        Agent agent= new Agent();

        if (repository.findByAgentEmailAndDate(request.getAgentEmail(), request.getDate()) != null) {
            throw new IllegalArgumentException(
                    "Une clôture existe déjà pour cette date"
            );
        }

        BigDecimal soldeTheorique = caisseOperationService.consulterSolde(request.getAgentEmail());

        ClotureCaisse cloture = new ClotureCaisse();

        cloture.setAgent(agent);
        cloture.setDate(request.getDate());
        cloture.setSoldeTheorique(soldeTheorique);
        cloture.setSoldeSaisi(request.getSoldeSaisi());

        BigDecimal ecart = request.getSoldeSaisi()
                .subtract(soldeTheorique);

        cloture.setEcart(ecart);

        cloture.setEcartSignale(
                ecart.compareTo(BigDecimal.ZERO) != 0
        );

        return converter.toResponse(repository.save(cloture));
    }

    private final ClotureCaisseRepository repository;
    private final ClotureCaisseConverter converter;
    private final UtilisateurService utilisateurService;
    private final CaisseOperationService caisseOperationService;

    public ClotureCaisseServiceImpl(ClotureCaisseRepository repository,
                                    ClotureCaisseConverter converter,
                                    CaisseOperationService caisseOperationService,
                                    UtilisateurService utilisateurService) {
        this.repository = repository;
        this.converter = converter;
        this.caisseOperationService = caisseOperationService;
        this.utilisateurService = utilisateurService;
    }
}