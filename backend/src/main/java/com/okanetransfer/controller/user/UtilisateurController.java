package com.okanetransfer.controller.user;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.okanetransfer.entity.user.Utilisateur;
import com.okanetransfer.service.dto.user.request.CreateUserRequest;
import com.okanetransfer.service.dto.user.request.UpdateProfilRequest;
import com.okanetransfer.service.dto.user.response.UserResponse;
import com.okanetransfer.service.facade.user.UtilisateurService;
import com.okanetransfer.shared.enums.RoleUtilisateur;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    // ── ADMIN ────────────────────────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(utilisateurService.creerUtilisateur(request));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll(
            @RequestParam(required = false) String role) {
        return ResponseEntity.ok(utilisateurService.getAll(role));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(utilisateurService.getById(id));
    }

    @PutMapping("/{id}/desactiver")
    public ResponseEntity<Void> desactiver(@PathVariable Long id) {
        utilisateurService.desactiver(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/reactiver")
    public ResponseEntity<Void> reactiver(@PathVariable Long id) {
        utilisateurService.reactiver(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/agence/{agenceId}")
    //@PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_ADMIN')")
    public ResponseEntity<List<UserResponse>> findByAgence(
            @PathVariable Long agenceId,
            @RequestParam(required = false) RoleUtilisateur role) {
        return ResponseEntity.ok(utilisateurService.findByAgence(agenceId, role));
    }

    // ── CLIENT — profil ───────────────────────────────────────────────────────

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(Authentication auth) {
        Utilisateur current = utilisateurService.getCurrentUser(auth);
        return ResponseEntity.ok(utilisateurService.getById(current.getId()));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateProfil(
            Authentication auth,
            @Valid @RequestBody UpdateProfilRequest request) {
        Utilisateur current = utilisateurService.getCurrentUser(auth);
        return ResponseEntity.ok(utilisateurService.updateProfil(current.getId(), request));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> demanderEffacement(Authentication auth) {
        Utilisateur current = utilisateurService.getCurrentUser(auth);
        utilisateurService.demanderEffacement(current.getId());
        return ResponseEntity.noContent().build();
    }
}
