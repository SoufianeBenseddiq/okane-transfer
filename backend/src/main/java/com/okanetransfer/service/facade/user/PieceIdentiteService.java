package com.okanetransfer.service.facade.user;

import com.okanetransfer.entity.user.PieceIdentite;
import com.okanetransfer.service.dto.user.request.PieceIdentiteRequest;
import com.okanetransfer.service.dto.user.response.PieceIdentiteResponse;

import java.util.List;

/**
 * Interface du service des pièces d'identité.
 *
 * Contrat public utilisé par :
 *   - UserServiceImpl        → toutes les opérations sur les pièces du client
 *   - TransfertServiceImpl   → verifierPieceAppartientAuClient (Membre 4)
 *
 * Règle : personne n'injecte PieceIdentiteRepository directement sauf cette classe.
 */
public interface PieceIdentiteService {

    // =========================================================================
    // Utilisé par UserServiceImpl
    // =========================================================================

    /**
     * Ajoute une nouvelle pièce d'identité à un client.
     * Si c'est la première pièce → définie principale automatiquement.
     * Le numéro est chiffré AES-256 par CryptoConverter avant stockage.
     *
     * @param clientId id du client propriétaire
     * @param request  données de la pièce à ajouter
     * @return PieceIdentiteResponse avec numéro déchiffré
     */
    PieceIdentiteResponse ajouterPiece(Long clientId, PieceIdentiteRequest request);

    /**
     * Définit une pièce comme principale.
     * Retire le flag principale de toutes les autres pièces du client.
     *
     * @param clientId id du client
     * @param pieceId  id de la pièce à définir comme principale
     * @return PieceIdentiteResponse mise à jour
     * @throws com.okanetransfer.shared.exception.AccesRefuseException si hors périmètre
     */
    PieceIdentiteResponse definirPrincipale(Long clientId, Long pieceId);

    /**
     * Supprime une pièce d'identité.
     * Interdit si c'est la pièce principale.
     *
     * @param clientId id du client
     * @param pieceId  id de la pièce à supprimer
     * @throws IllegalArgumentException si tentative de suppression de la pièce principale
     * @throws com.okanetransfer.shared.exception.AccesRefuseException si hors périmètre
     */
    void supprimerPiece(Long clientId, Long pieceId);

    /**
     * Retourne toutes les pièces d'identité d'un client.
     * Les numéros sont déchiffrés automatiquement par CryptoConverter.
     *
     * @param clientId id du client
     * @return liste de PieceIdentiteResponse
     */
    List<PieceIdentiteResponse> getPieces(Long clientId);

    /**
     * Supprime toutes les pièces d'un client.
     * Appelé uniquement lors de la demande d'effacement RGPD.
     *
     * @param clientId id du client
     */
    void supprimerToutesPieces(Long clientId);

    // =========================================================================
    // Utilisé par TransfertServiceImpl (Membre 4)
    // =========================================================================

    /**
     * Vérifie qu'une pièce d'identité appartient bien à un client donné.
     * Appelé par TransfertServiceImpl avant de créer un Expediteur.
     *
     * @param pieceId  id de la pièce choisie par l'agent
     * @param clientId id du client expéditeur
     * @throws com.okanetransfer.shared.exception.AccesRefuseException si la pièce n'appartient pas à ce client
     */
    void verifierPieceAppartientAuClient(Long pieceId, Long clientId);

    /**
     * Récupère l'entité PieceIdentite par son id et son client.
     * Utilisé par TransfertServiceImpl pour construire l'Expediteur.
     *
     * @param pieceId  id de la pièce
     * @param clientId id du client
     * @return l'entité PieceIdentite
     * @throws com.okanetransfer.shared.exception.AccesRefuseException si introuvable ou hors périmètre
     */
    PieceIdentite getPieceEntity(Long pieceId, Long clientId);
}
