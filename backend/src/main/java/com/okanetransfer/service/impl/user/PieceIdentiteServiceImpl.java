package com.okanetransfer.service.impl.user;

import com.okanetransfer.entity.user.Client;
import com.okanetransfer.entity.user.PieceIdentite;
import com.okanetransfer.repository.user.PieceIdentiteRepository;
import com.okanetransfer.repository.user.UtilisateurRepository;
import com.okanetransfer.service.converter.user.PieceIdentiteConverter;
import com.okanetransfer.service.dto.user.request.PieceIdentiteRequest;
import com.okanetransfer.service.dto.user.response.PieceIdentiteResponse;
import com.okanetransfer.service.facade.user.PieceIdentiteService;
import com.okanetransfer.shared.exception.AccesRefuseException;
import com.okanetransfer.shared.exception.TransfertNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PieceIdentiteServiceImpl implements PieceIdentiteService {

    private final PieceIdentiteRepository pieceIdentiteRepository;
    private final UtilisateurRepository utilisateurRepository;

    private final PieceIdentiteConverter converter;

    public PieceIdentiteServiceImpl(PieceIdentiteRepository pieceIdentiteRepository,
                                    UtilisateurRepository utilisateurRepository,
                                    PieceIdentiteConverter converter) {
        this.pieceIdentiteRepository = pieceIdentiteRepository;
        this.utilisateurRepository   = utilisateurRepository;
        this.converter               = converter;
    }

    // =========================================================================
    // Utilisé par UserServiceImpl
    // =========================================================================

    @Override
    public PieceIdentiteResponse ajouterPiece(Long clientId, PieceIdentiteRequest request) {
        Client client = utilisateurRepository.findClientById(clientId)
                .orElseThrow(() -> new TransfertNotFoundException(
                        "Client introuvable (id=" + clientId + ")"
                ));

        PieceIdentite piece = converter.toEntity(request);

        piece.setClient(client);

        List<PieceIdentite> existantes = pieceIdentiteRepository.findByClientId(clientId);
        piece.setPrincipale(existantes.isEmpty());

        return converter.toResponse(pieceIdentiteRepository.save(piece));
    }

    @Override
    public PieceIdentiteResponse definirPrincipale(Long clientId, Long pieceId) {
        // étape 1 : retirer le flag principale de toutes les pièces du client
        List<PieceIdentite> pieces = pieceIdentiteRepository.findByClientId(clientId);
        pieces.forEach(p -> {
            if (Boolean.TRUE.equals(p.getPrincipale())) {
                p.setPrincipale(false);
                pieceIdentiteRepository.save(p);
            }
        });

        PieceIdentite piece = pieceIdentiteRepository
                .findByIdAndClientId(pieceId, clientId)
                .orElseThrow(() -> new AccesRefuseException(
                        "Pièce introuvable pour ce client (pieceId=" + pieceId + ")"
                ));
        piece.setPrincipale(true);

        return converter.toResponse(pieceIdentiteRepository.save(piece));
    }

    @Override
    public void supprimerPiece(Long clientId, Long pieceId) {
        PieceIdentite piece = pieceIdentiteRepository
                .findByIdAndClientId(pieceId, clientId)
                .orElseThrow(() -> new AccesRefuseException(
                        "Pièce introuvable pour ce client (pieceId=" + pieceId + ")"
                ));

        // sécurité : interdire la suppression de la pièce principale
        if (Boolean.TRUE.equals(piece.getPrincipale())) {
            throw new IllegalArgumentException(
                    "Impossible de supprimer la pièce principale. " +
                            "Définissez d'abord une autre pièce comme principale."
            );
        }

        pieceIdentiteRepository.delete(piece);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PieceIdentiteResponse> getPieces(Long clientId) {
        return converter.toResponseList(
                pieceIdentiteRepository.findByClientId(clientId)
        );
    }

    @Override
    public void supprimerToutesPieces(Long clientId) {
        List<PieceIdentite> pieces = pieceIdentiteRepository.findByClientId(clientId);
        pieceIdentiteRepository.deleteAll(pieces);
    }

    // =========================================================================
    // Utilisé par TransfertServiceImpl
    // =========================================================================

    /**
     * Vérifie qu'une pièce appartient bien au client.
     * Appelé avant la création d'un Expediteur pour sécuriser le transfert.
     */
    @Override
    @Transactional(readOnly = true)
    public void verifierPieceAppartientAuClient(Long pieceId, Long clientId) {
        boolean existe = pieceIdentiteRepository
                .findByIdAndClientId(pieceId, clientId)
                .isPresent();

        if (!existe) {
            throw new AccesRefuseException(
                    "La pièce (id=" + pieceId + ") n'appartient pas au client (id=" + clientId + ")"
            );
        }
    }

    /**
     * Retourne l'entité PieceIdentite.
     * Utilisé par TransfertServiceImpl pour construire l'Expediteur.
     * Retourne l'entité brute — pas un DTO — car TransfertServiceImpl
     * a besoin de la relation JPA pour créer l'Expediteur.
     */
    @Override
    @Transactional(readOnly = true)
    public PieceIdentite getPieceEntity(Long pieceId, Long clientId) {
        return pieceIdentiteRepository
                .findByIdAndClientId(pieceId, clientId)
                .orElseThrow(() -> new AccesRefuseException(
                        "Pièce introuvable pour ce client (pieceId=" + pieceId + ")"
                ));
    }

}
