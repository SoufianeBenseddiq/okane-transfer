package com.okanetransfer.service.facade.user;

import com.okanetransfer.entity.user.Agent;
import com.okanetransfer.entity.user.Client;
import com.okanetransfer.entity.user.Utilisateur;
import com.okanetransfer.service.dto.user.request.AdminUpdateUserRequest;
import com.okanetransfer.service.dto.user.request.CreateUserRequest;
import com.okanetransfer.service.dto.user.request.PieceIdentiteRequest;
import com.okanetransfer.service.dto.user.request.UpdateProfilRequest;
import com.okanetransfer.service.dto.user.response.PieceIdentiteResponse;
import com.okanetransfer.service.dto.user.response.UserResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

/**
 * Interface du service utilisateur.
 *
 * Contrat public utilisé par :
 *   - UserController        → CRUD utilisateurs, profil
 *   - AuthController        → getCurrentUser
 *   - TransfertServiceImpl  → getAgentByEmail, findClientByTelephone  (Membre 4)
 *   - AmlServiceImpl        → getClientByEmail                        (Membre 5)
 *
 * Note : les opérations sur les pièces d'identité sont dans IPieceIdentiteService.
 * UserServiceImpl délègue vers IPieceIdentiteService — jamais vers PieceIdentiteRepository.
 *
 * IMPORTANT : les autres membres injectent cette interface, jamais UserServiceImpl.
 */
public interface UtilisateurService {

    // =========================================================================
    // Méthodes pour les autres membres (M4, M5)
    // =========================================================================

    /**
     * Récupère l'utilisateur connecté depuis le token JWT.
     * Appelé dans tous les controllers via authentication.getName() = email.
     *
     * @param authentication objet Spring Security injecté automatiquement
     * @return l'entité Utilisateur
     * @throws com.okanetransfer.shared.exception.AccesRefuseException si introuvable
     */
    Utilisateur getCurrentUser(Authentication authentication);

    /**
     * Récupère l'agent connecté par son email.
     * Utilisé par TransfertServiceImpl pour identifier l'agent qui saisit un transfert.
     *
     * @param email l'email de l'agent (= authentication.getName())
     * @return l'entité Agent avec son agence chargée
     * @throws com.okanetransfer.shared.exception.AccesRefuseException si agent introuvable ou inactif
     */
    Agent getAgentByEmail(String email);

    /**
     * Cherche un client par téléphone — retourne NULL si aucun compte trouvé.
     * Utilisé par TransfertServiceImpl pour lier l'expéditeur à son compte.
     * Ne jamais lever d'exception ici — l'expéditeur peut ne pas avoir de compte.
     *
     * @param telephone numéro de téléphone saisi par l'agent au guichet
     * @return le Client si trouvé, null sinon
     */
    Client findClientByTelephone(String telephone);

    /**
     * Récupère un client par son email.
     * Utilisé par AmlServiceImpl et l'espace client.
     *
     * @param email l'email du client
     * @return l'entité Client
     * @throws com.okanetransfer.shared.exception.AccesRefuseException si introuvable
     */
    Client getClientByEmail(String email);

    // =========================================================================
    // ADMIN — gestion des utilisateurs
    // =========================================================================

    /**
     * Crée un utilisateur selon son rôle (ADMIN, MANAGER, AGENT, CLIENT).
     * Instancie la bonne sous-classe JPA. Hache le mot de passe BCrypt.
     *
     * @param request données du nouvel utilisateur
     * @return UserResponse sans le mot de passe
     * @throws IllegalArgumentException si l'email est déjà utilisé
     */
    UserResponse creerUtilisateur(CreateUserRequest request);

    /**
     * Désactive un compte (soft delete).
     * L'utilisateur ne peut plus se connecter mais ses données sont conservées.
     *
     * @param userId id de l'utilisateur à désactiver
     */
    void desactiver(Long userId);

    /**
     * Réactive un compte précédemment désactivé.
     *
     * @param userId id de l'utilisateur à réactiver
     */
    void reactiver(Long userId);

    /**
     * Liste tous les utilisateurs avec filtre optionnel par rôle.
     *
     * @param role "ROLE_AGENT", "ROLE_CLIENT"... Null ou vide = tous
     * @return liste de UserResponse
     */
    List<UserResponse> getAll(String role);

    /**
     * Récupère le détail d'un utilisateur par son id.
     *
     * @param userId id de l'utilisateur
     * @return UserResponse
     */
    UserResponse getById(Long userId);

    UserResponse adminUpdate(Long userId, AdminUpdateUserRequest request);

    void deleteById(Long userId);

    /**
     * Affecte ou désaffecte un manager à une agence.
     * Désaffecte d'abord l'éventuel manager précédent de cette agence,
     * puis affecte le nouveau si newManagerId est non-null.
     * Appelé par AgenceServiceImpl — jamais par un controller.
     *
     * @param agenceId     id de l'agence concernée
     * @param newManagerId id du manager à affecter, null pour juste désaffecter
     */
    void updateAgenceManager(Long agenceId, Long newManagerId);

    // =========================================================================
    // CLIENT — gestion du profil
    // =========================================================================

    /**
     * Modifie le profil du client connecté.
     * Tous les champs sont optionnels — seuls les champs non-null sont mis à jour.
     *
     * @param clientId id du client (extrait du JWT dans le controller)
     * @param request  champs à modifier
     * @return UserResponse mis à jour
     */
    UserResponse updateProfil(Long clientId, UpdateProfilRequest request);

    // =========================================================================
    // CLIENT — délégation vers IPieceIdentiteService
    // Ces méthodes sont exposées ici pour simplifier l'accès depuis le controller.
    // L'implémentation délègue entièrement à IPieceIdentiteService.
    // =========================================================================

    /**
     * Ajoute une pièce d'identité — délègue à IPieceIdentiteService.
     */
    PieceIdentiteResponse ajouterPiece(Long clientId, PieceIdentiteRequest request);

    /**
     * Définit une pièce comme principale — délègue à IPieceIdentiteService.
     */
    PieceIdentiteResponse definirPrincipale(Long clientId, Long pieceId);

    /**
     * Supprime une pièce — délègue à IPieceIdentiteService.
     */
    void supprimerPiece(Long clientId, Long pieceId);

    /**
     * Liste les pièces du client — délègue à IPieceIdentiteService.
     */
    List<PieceIdentiteResponse> getPieces(Long clientId);

    // =========================================================================
    // CLIENT — droit à l'effacement RGPD
    // =========================================================================

    /**
     * Pseudonymise toutes les données personnelles du client.
     * Délègue la suppression des pièces à IPieceIdentiteService.
     * Désactive le compte.
     *
     * @param clientId id du client demandant l'effacement
     */
    void demanderEffacement(Long clientId);
    List<UserResponse> searchClients(String query);
    UserResponse changePassword(String email, String currentPassword, String newPassword);

}
