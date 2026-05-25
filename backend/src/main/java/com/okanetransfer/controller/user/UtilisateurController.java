package com.okanetransfer.controller.user;

import com.okanetransfer.entity.user.Utilisateur;
import com.okanetransfer.service.dto.user.request.CreateUserRequest;
import com.okanetransfer.service.dto.user.request.UpdateProfilRequest;
import com.okanetransfer.service.dto.user.response.UserResponse;
import com.okanetransfer.service.facade.user.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

//    @GetMapping("/search/{q}")
//    public ResponseEntity<List<UserResponse>> search(
//            @PathVariable("q") String q) {
//        return ResponseEntity.ok(utilisateurService.searchClients(q));
//    }
@GetMapping("/search/{q}")
public ResponseEntity<List<UserResponse>> search(@PathVariable(name = "q") String q) {
    return ResponseEntity.ok(utilisateurService.searchClients(q));
}
}
