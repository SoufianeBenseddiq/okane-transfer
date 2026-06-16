package com.okanetransfer.controller.user;

import com.okanetransfer.entity.user.Utilisateur;
import com.okanetransfer.service.dto.user.request.AdminUpdateUserRequest;
import com.okanetransfer.service.dto.user.request.CreateUserRequest;
import com.okanetransfer.service.dto.user.request.UpdateProfilRequest;
import com.okanetransfer.service.dto.user.response.UserResponse;
import com.okanetransfer.service.facade.user.UtilisateurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Utilisateurs", description = "Gestion des comptes utilisateurs (admin) et profil du compte connecté (client/agent/manager)")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    // ── ADMIN ────────────────────────────────────────────────────────────────

    @PostMapping
    @Operation(summary = "Créer un utilisateur", description = "Crée un nouveau compte (CLIENT, AGENT, MANAGER, ADMIN). Réservé ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Utilisateur créé"),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "409", description = "Email déjà utilisé")
    })
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(utilisateurService.creerUtilisateur(request));
    }

    @GetMapping
    @Operation(summary = "Lister les utilisateurs",
               description = "Retourne tous les utilisateurs. Filtrer par rôle avec le paramètre `role` (ex: CLIENT, AGENT, MANAGER).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste retournée")
    })
    public ResponseEntity<List<UserResponse>> findAll(
            @Parameter(description = "Filtrer par rôle : CLIENT, AGENT, MANAGER, ADMIN", example = "CLIENT")
            @RequestParam(name = "role", required = false) String role) {
        return ResponseEntity.ok(utilisateurService.getAll(role));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Trouver un utilisateur par ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Utilisateur trouvé"),
        @ApiResponse(responseCode = "404", description = "Utilisateur introuvable")
    })
    public ResponseEntity<UserResponse> findById(
            @Parameter(description = "ID de l'utilisateur", example = "1")
            @PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(utilisateurService.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un utilisateur (admin)", description = "Mise à jour complète d'un compte par un administrateur.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Utilisateur mis à jour"),
        @ApiResponse(responseCode = "404", description = "Utilisateur introuvable")
    })
    public ResponseEntity<UserResponse> adminUpdate(
            @Parameter(description = "ID de l'utilisateur", example = "1")
            @PathVariable(name = "id") Long id,
            @Valid @RequestBody AdminUpdateUserRequest request) {
        return ResponseEntity.ok(utilisateurService.adminUpdate(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un utilisateur", description = "Suppression définitive. Réservé ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Utilisateur supprimé"),
        @ApiResponse(responseCode = "404", description = "Utilisateur introuvable")
    })
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "ID de l'utilisateur", example = "1")
            @PathVariable(name = "id") Long id) {
        utilisateurService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/desactiver")
    @Operation(summary = "Désactiver un compte", description = "Bloque la connexion de l'utilisateur sans le supprimer.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Compte désactivé"),
        @ApiResponse(responseCode = "404", description = "Utilisateur introuvable")
    })
    public ResponseEntity<Void> desactiver(
            @Parameter(description = "ID de l'utilisateur", example = "1")
            @PathVariable(name = "id") Long id) {
        utilisateurService.desactiver(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/reactiver")
    @Operation(summary = "Réactiver un compte", description = "Rétablit l'accès d'un compte précédemment désactivé.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Compte réactivé"),
        @ApiResponse(responseCode = "404", description = "Utilisateur introuvable")
    })
    public ResponseEntity<Void> reactiver(
            @Parameter(description = "ID de l'utilisateur", example = "1")
            @PathVariable(name = "id") Long id) {
        utilisateurService.reactiver(id);
        return ResponseEntity.noContent().build();
    }

    // ── CLIENT — profil ───────────────────────────────────────────────────────

    @GetMapping("/me")
    @Operation(summary = "Profil du compte connecté", description = "Retourne les informations du compte correspondant au JWT actuel.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Profil retourné"),
        @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    public ResponseEntity<UserResponse> getMe(Authentication auth) {
        Utilisateur current = utilisateurService.getCurrentUser(auth);
        return ResponseEntity.ok(utilisateurService.getById(current.getId()));
    }

    @PutMapping("/me")
    @Operation(summary = "Mettre à jour son profil", description = "Modifie nom, prénom, téléphone ou pays du compte connecté.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Profil mis à jour"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<UserResponse> updateProfil(
            Authentication auth,
            @Valid @RequestBody UpdateProfilRequest request) {
        Utilisateur current = utilisateurService.getCurrentUser(auth);
        return ResponseEntity.ok(utilisateurService.updateProfil(current.getId(), request));
    }

    @DeleteMapping("/me")
    @Operation(summary = "Demander l'effacement de son compte", description = "Soumet une demande RGPD d'effacement du compte connecté.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Demande enregistrée")
    })
    public ResponseEntity<Void> demanderEffacement(Authentication auth) {
        Utilisateur current = utilisateurService.getCurrentUser(auth);
        utilisateurService.demanderEffacement(current.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search/{q}")
    @Operation(summary = "Rechercher des utilisateurs", description = "Recherche par nom, prénom ou email (correspondance partielle).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Résultats retournés")
    })
    public ResponseEntity<List<UserResponse>> search(
            @Parameter(description = "Terme de recherche (nom, prénom ou email)", example = "dupont")
            @PathVariable(name = "q") String q) {
        return ResponseEntity.ok(utilisateurService.searchClients(q));
    }
}
