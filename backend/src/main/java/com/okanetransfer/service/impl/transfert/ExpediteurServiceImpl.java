package com.okanetransfer.service.impl.transfert;

import com.okanetransfer.entity.transfert.Expediteur;
import com.okanetransfer.entity.user.Client;
import com.okanetransfer.entity.user.PieceIdentite;
import com.okanetransfer.repository.transfert.ExpediteurRepository;
import com.okanetransfer.repository.user.UtilisateurRepository;
import com.okanetransfer.service.converter.transfert.ExpediteurConverter;
import com.okanetransfer.service.dto.transfert.request.CreateExpediteurRequest;
import com.okanetransfer.service.dto.transfert.request.UpdateExpediteurRequest;
import com.okanetransfer.service.dto.transfert.response.ExpediteurResponse;
import com.okanetransfer.service.facade.transfert.IExpediteurService;
import com.okanetransfer.service.facade.user.PieceIdentiteService;
import com.okanetransfer.shared.exception.TransfertNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ExpediteurServiceImpl implements IExpediteurService {

    private final ExpediteurRepository expediteurRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PieceIdentiteService pieceIdentiteService;

    public ExpediteurServiceImpl(
            ExpediteurRepository expediteurRepository,
            UtilisateurRepository utilisateurRepository,
            PieceIdentiteService pieceIdentiteService) {

        this.expediteurRepository = expediteurRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.pieceIdentiteService = pieceIdentiteService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpediteurResponse> getAllExpediteurs() {
        return expediteurRepository.findAll()
                .stream()
                .map(ExpediteurConverter::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ExpediteurResponse getExpediteurById(Long id) {
        Expediteur expediteur = findExpediteur(id);
        return ExpediteurConverter.toResponse(expediteur);
    }

    @Override
    public ExpediteurResponse createExpediteur(CreateExpediteurRequest request) {

        Client client = findClient(request.getClientId());
        PieceIdentite piece = pieceIdentiteService.getPieceEntity(
                request.getPieceIdentiteId(),
                request.getClientId()
        );

        Expediteur expediteur = new Expediteur();
        expediteur.setClient(client);
        expediteur.setPieceConfirmee(piece);

        return ExpediteurConverter.toResponse(
                expediteurRepository.save(expediteur)
        );
    }

    @Override
    public ExpediteurResponse updateExpediteur(Long id, UpdateExpediteurRequest request) {
        Expediteur expediteur = findExpediteur(id);

        Client client = findClient(request.getClientId());
        PieceIdentite piece = pieceIdentiteService.getPieceEntity(
                request.getPieceIdentiteId(),
                request.getClientId()
        );

        expediteur.setClient(client);
        expediteur.setPieceConfirmee(piece);

        expediteur = expediteurRepository.save(expediteur);

        return ExpediteurConverter.toResponse(expediteur);
    }

    @Override
    public void deleteExpediteur(Long id) {
        Expediteur expediteur = findExpediteur(id);
        expediteurRepository.delete(expediteur);
    }

    private Expediteur findExpediteur(Long id) {
        return expediteurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expediteur introuvable avec id: " + id));
    }

    private Client findClient(Long clientId) {
        return utilisateurRepository.findClientById(clientId)
                .orElseThrow(() -> new TransfertNotFoundException(
                        "Client introuvable avec id: " + clientId
                ));
    }

}
