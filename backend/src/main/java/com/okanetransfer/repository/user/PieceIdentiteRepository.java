package com.okanetransfer.repository.user;

import com.okanetransfer.entity.user.Client;
import com.okanetransfer.entity.user.PieceIdentite;
import com.okanetransfer.shared.enums.TypePiece;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PieceIdentiteRepository extends JpaRepository<PieceIdentite, Long> {

    // toutes les pièces d'un client (espace profil)
    List<PieceIdentite> findByClient(Client client);

    List<PieceIdentite> findByClientId(Long clientId);

    // la pièce principale d'un client (utilisée par défaut lors d'un transfert)
    @Query("SELECT p FROM PieceIdentite p WHERE p.client.id = :clientId AND p.principale = true")
    Optional<PieceIdentite> findPrincipaleByClientId(@Param("clientId") Long clientId);

    // vérifier qu'une pièce appartient bien à un client
    // utilisé par TransfertServiceImpl avant de créer un Expediteur
    Optional<PieceIdentite> findByIdAndClient(Long id, Client client);

    // version avec clientId directement
    @Query("SELECT p FROM PieceIdentite p WHERE p.id = :pieceId AND p.client.id = :clientId")
    Optional<PieceIdentite> findByIdAndClientId(
            @Param("pieceId") Long pieceId,
            @Param("clientId") Long clientId
    );

    // vérifier si un client a déjà une pièce de ce type
    boolean existsByClientAndType(Client client, TypePiece type);

    // pièces expirées (job de nettoyage ou alerte admin)
    @Query("SELECT p FROM PieceIdentite p WHERE p.dateExpiration < :aujourd_hui")
    List<PieceIdentite> findExpirees(@Param("aujourd_hui") LocalDate aujourdhui);

    // pièces d'un client par type
    Optional<PieceIdentite> findByClientAndType(Client client, TypePiece type);
}