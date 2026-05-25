package com.okanetransfer.service.impl.user;

import com.okanetransfer.entity.user.*;
import com.okanetransfer.repository.user.UtilisateurRepository;
import com.okanetransfer.service.converter.user.UtilisateurConverter;
import com.okanetransfer.service.dto.user.request.CreateUserRequest;
import com.okanetransfer.service.dto.user.request.PieceIdentiteRequest;
import com.okanetransfer.service.dto.user.request.UpdateProfilRequest;
import com.okanetransfer.service.dto.user.response.PieceIdentiteResponse;
import com.okanetransfer.service.dto.user.response.UserResponse;
import com.okanetransfer.service.facade.user.PieceIdentiteService;
import com.okanetransfer.service.facade.user.UtilisateurService;
import com.okanetransfer.shared.enums.RoleUtilisateur;
import com.okanetransfer.shared.exception.AccesRefuseException;
import com.okanetransfer.shared.exception.TransfertNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Implémentation du service utilisateur.
 *
 * Dépendances :
 *   - UtilisateurRepository   → CRUD utilisateurs (seul repo autorisé ici)
 *   - IPieceIdentiteService   → toutes les opérations sur les pièces d'identité
 *   - PasswordEncoder         → hachage BCrypt du mot de passe à la création
 *
 * RÈGLE RESPECTÉE : UserServiceImpl n'injecte JAMAIS PieceIdentiteRepository.
 * Toutes les opérations sur les pièces passent par IPieceIdentiteService.
 */
@Service
@Transactional
public class UtilisateurServiceImpl implements UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;

    // injection de l'interface — jamais de PieceIdentiteRepository directement
    private final PieceIdentiteService pieceIdentiteService;

    private final PasswordEncoder passwordEncoder;

    private final UtilisateurConverter utilisateurConverter;

    public UtilisateurServiceImpl(UtilisateurRepository utilisateurRepository,
                                  PieceIdentiteService pieceIdentiteService,
                                  PasswordEncoder passwordEncoder,
                                  UtilisateurConverter utilisateurConverter) {
        this.utilisateurRepository = utilisateurRepository;
        this.pieceIdentiteService  = pieceIdentiteService;
        this.passwordEncoder       = passwordEncoder;
        this.utilisateurConverter  = utilisateurConverter;
    }

    // =========================================================================
    // Méthodes pour les autres membres (M4, M5)
    // =========================================================================

    /**
     * Récupère l'utilisateur connecté depuis le contexte Spring Security.
     * authentication.getName() = email extrait du JWT par JwtAuthFilter.
     */
    @Override
    @Transactional(readOnly = true)
    public Utilisateur getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return utilisateurRepository
                .findByEmail(email)
                .orElseThrow(() -> new AccesRefuseException(
                        "Utilisateur introuvable pour l'email : " + email
                ));
    }

    /**
     * Récupère l'agent connecté pour la saisie d'un transfert.
     */
    @Override
    @Transactional(readOnly = true)
    public Agent getAgentByEmail(String email) {
        return utilisateurRepository
                .findAgentByEmail(email)
                .orElseThrow(() -> new AccesRefuseException(
                        "Agent introuvable ou inactif : " + email
                ));
    }

    /**
     * Cherche un client par téléphone.
     * Retourne NULL si aucun compte — cas nominal, pas une erreur.
     */
    @Override
    @Transactional(readOnly = true)
    public Client findClientByTelephone(String telephone) {
        // orElse(null) intentionnel — l'expéditeur peut ne pas avoir de compte
        return utilisateurRepository
                .findClientByTelephone(telephone)
                .orElse(null);
    }

    /**
     * Récupère un client par email.
     */
    @Override
    @Transactional(readOnly = true)
    public Client getClientByEmail(String email) {
        return utilisateurRepository
                .findClientByEmail(email)
                .orElseThrow(() -> new AccesRefuseException(
                        "Client introuvable : " + email
                ));
    }

    // =========================================================================
    // ADMIN — gestion des utilisateurs
    // =========================================================================

    /**
     * Crée un utilisateur selon son rôle.
     * Instancie la bonne sous-classe JPA.
     * Hache le mot de passe BCrypt (force 12 défini dans AppConfig).
     */
    @Override
    public UserResponse creerUtilisateur(CreateUserRequest request) {

        // vérifier l'unicité de l'email
        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "Cet email est déjà utilisé : " + request.getEmail()
            );
        }

        // instancier la bonne sous-classe selon le rôle
        Utilisateur utilisateur = switch (request.getRole()) {
            case ROLE_ADMIN   -> new Administrateur();
            case ROLE_MANAGER -> new Manager();
            case ROLE_AGENT   -> new Agent();
            case ROLE_CLIENT  -> new Client();
        };

        utilisateurConverter.applyCreate(utilisateur, request, passwordEncoder);

        return utilisateurConverter.toResponse(utilisateurRepository.save(utilisateur));
    }

    /**
     * Désactive un compte (soft delete).
     */
    @Override
    public void desactiver(Long userId) {
        Utilisateur user = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new TransfertNotFoundException(
                        "Utilisateur introuvable (id=" + userId + ")"
                ));
        user.setActif(false);
        utilisateurRepository.save(user);
    }

    /**
     * Réactive un compte désactivé.
     */
    @Override
    public void reactiver(Long userId) {
        Utilisateur user = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new TransfertNotFoundException(
                        "Utilisateur introuvable (id=" + userId + ")"
                ));
        user.setActif(true);
        utilisateurRepository.save(user);
    }

    /**
     * Liste tous les utilisateurs avec filtre optionnel par rôle.
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAll(String role) {
        List<Utilisateur> users;

        if (role != null && !role.isBlank()) {
            users = utilisateurRepository.findByRole(
                    RoleUtilisateur.valueOf(role.toUpperCase())
            );
        } else {
            users = utilisateurRepository.findAll();
        }

        return utilisateurConverter.toResponseList(users);
    }

    /**
     * Récupère un utilisateur par son id.
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(Long userId) {
        return utilisateurConverter.toResponse(
                utilisateurRepository.findById(userId)
                        .orElseThrow(() -> new TransfertNotFoundException(
                                "Utilisateur introuvable (id=" + userId + ")"
                        ))
        );
    }

    // =========================================================================
    // CLIENT — gestion du profil
    // =========================================================================

    /**
     * Mise à jour partielle du profil.
     * Seuls les champs non-null sont mis à jour.
     */
    @Override
    public UserResponse updateProfil(Long clientId, UpdateProfilRequest request) {
        Utilisateur user = utilisateurRepository.findById(clientId)
                .orElseThrow(() -> new TransfertNotFoundException(
                        "Client introuvable (id=" + clientId + ")"
                ));

        utilisateurConverter.applyUpdate(user, request);

        return utilisateurConverter.toResponse(utilisateurRepository.save(user));
    }

    // =========================================================================
    // CLIENT — délégation complète vers IPieceIdentiteService
    // UserServiceImpl ne touche jamais PieceIdentiteRepository
    // =========================================================================

    /**
     * Délègue à IPieceIdentiteService — pas de logique ici.
     */
    @Override
    public PieceIdentiteResponse ajouterPiece(Long clientId, PieceIdentiteRequest request) {
        return pieceIdentiteService.ajouterPiece(clientId, request);
    }

    /**
     * Délègue à IPieceIdentiteService — pas de logique ici.
     */
    @Override
    public PieceIdentiteResponse definirPrincipale(Long clientId, Long pieceId) {
        return pieceIdentiteService.definirPrincipale(clientId, pieceId);
    }

    /**
     * Délègue à IPieceIdentiteService — pas de logique ici.
     */
    @Override
    public void supprimerPiece(Long clientId, Long pieceId) {
        pieceIdentiteService.supprimerPiece(clientId, pieceId);
    }

    /**
     * Délègue à IPieceIdentiteService — pas de logique ici.
     */
    @Override
    public List<PieceIdentiteResponse> getPieces(Long clientId) {
        return pieceIdentiteService.getPieces(clientId);
    }

    // =========================================================================
    // CLIENT — droit à l'effacement RGPD
    // =========================================================================

    /**
     * Pseudonymisation RGPD.
     * Délègue la suppression des pièces à IPieceIdentiteService.
     * UserServiceImpl ne touche pas PieceIdentiteRepository.
     */
    @Override
    public void demanderEffacement(Long clientId) {
        Utilisateur user = utilisateurRepository.findById(clientId)
                .orElseThrow(() -> new TransfertNotFoundException(
                        "Client introuvable (id=" + clientId + ")"
                ));

        // pseudonymisation de toutes les données personnelles
        user.setNom("SUPPRIME");
        user.setPrenom("SUPPRIME");
        user.setEmail("deleted_" + clientId + "@supprime.local");
        user.setTelephone("0000000000");
        user.setPays("XX");
        user.setMotDePasseHash("SUPPRIME");   // hash invalide → connexion impossible
        user.setActif(false);

        // délégation à IPieceIdentiteService pour la suppression des pièces
        // UserServiceImpl ne touche pas PieceIdentiteRepository directement
        pieceIdentiteService.supprimerToutesPieces(clientId);

        utilisateurRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> searchClients(String query) {
        if (query == null || query.isBlank()) return List.of();
        return utilisateurRepository.searchClients(query.trim())
                .stream()
                .map(utilisateurConverter::toResponse)
                .toList();
    }
}
