package com.okanetransfer.controller.agence;

import com.okanetransfer.service.dto.agence.request.AgenceRequest;
import com.okanetransfer.service.dto.agence.response.AgenceResponse;
import com.okanetransfer.service.facade.agence.AgenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/agences/")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Agences", description = "Gestion du réseau d'agences (centrales, régionales, locales)")
public class AgenceController {

    private final AgenceService agenceService;

    public AgenceController(AgenceService agenceService) {
        this.agenceService = agenceService;
    }

    @GetMapping("id/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_AGENT')")
    @Operation(summary = "Trouver une agence par ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Agence trouvée"),
        @ApiResponse(responseCode = "404", description = "Agence introuvable")
    })
    public ResponseEntity<AgenceResponse> findById(
            @Parameter(description = "ID de l'agence", example = "1")
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(agenceService.findById(id));
    }

    @GetMapping("nom/{nom}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    @Operation(summary = "Trouver une agence par nom")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Agence trouvée"),
        @ApiResponse(responseCode = "404", description = "Agence introuvable")
    })
    public ResponseEntity<AgenceResponse> findByNom(
            @Parameter(description = "Nom de l'agence", example = "Agence Casablanca Centre")
            @PathVariable("nom") String nom) {
        return ResponseEntity.ok(agenceService.findByNom(nom));
    }

    @GetMapping("adresse/{adresse}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    @Operation(summary = "Trouver une agence par adresse")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Agence trouvée"),
        @ApiResponse(responseCode = "404", description = "Agence introuvable")
    })
    public ResponseEntity<AgenceResponse> findByAdresse(
            @Parameter(description = "Adresse de l'agence", example = "12 rue Mohammed V")
            @PathVariable("adresse") String adresse) {
        return ResponseEntity.ok(agenceService.findByAdresse(adresse));
    }

    @GetMapping("responsable/{email}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    @Operation(summary = "Trouver l'agence d'un responsable", description = "Retourne l'agence dont le manager est identifié par son email.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Agence trouvée"),
        @ApiResponse(responseCode = "404", description = "Aucune agence pour ce responsable")
    })
    public ResponseEntity<AgenceResponse> findByResponsable(
            @Parameter(description = "Email du responsable d'agence", example = "manager@okane.ma")
            @PathVariable("email") String email) {
        return ResponseEntity.ok(agenceService.findByResponsableEmail(email));
    }

    @GetMapping("actives")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_AGENT')")
    @Operation(summary = "Lister les agences actives")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste des agences actives")
    })
    public ResponseEntity<List<AgenceResponse>> findByActiveTrue() {
        return ResponseEntity.ok(agenceService.findByActiveTrue());
    }

    @GetMapping("all")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    @Operation(summary = "Lister toutes les agences", description = "Inclut les agences actives et inactives. Réservé ADMIN / MANAGER.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste complète des agences")
    })
    public ResponseEntity<List<AgenceResponse>> findAll() {
        return ResponseEntity.ok(agenceService.findAll());
    }

    @GetMapping("centrales")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Lister les agences centrales", description = "Retourne uniquement les agences de niveau CENTRALE. Réservé ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste des agences centrales")
    })
    public ResponseEntity<List<AgenceResponse>> findCentrales() {
        return ResponseEntity.ok(agenceService.findCentrales());
    }

    @GetMapping("mon-agence")
    @PreAuthorize("hasRole('ROLE_AGENT') or hasRole('ROLE_MANAGER')")
    @Operation(summary = "Agence du compte connecté", description = "Retourne l'agence à laquelle l'agent ou le manager connecté est rattaché.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Agence de l'utilisateur connecté"),
        @ApiResponse(responseCode = "404", description = "Aucune agence associée")
    })
    public ResponseEntity<AgenceResponse> getMonAgence(Principal principal) {
        String email = principal.getName();
        try {
            return ResponseEntity.ok(agenceService.findByAgentEmail(email));
        } catch (Exception e) {
            return ResponseEntity.ok(agenceService.findByResponsableEmail(email));
        }
    }

    @PostMapping("add-one")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Créer une agence", description = "Ajoute une nouvelle agence au réseau. Réservé ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Agence créée"),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "409", description = "Agence déjà existante avec ce nom")
    })
    public ResponseEntity<AgenceResponse> save(@RequestBody @Valid AgenceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agenceService.save(request));
    }

    @PutMapping("id/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Modifier une agence", description = "Met à jour les informations d'une agence existante. Réservé ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Agence mise à jour"),
        @ApiResponse(responseCode = "404", description = "Agence introuvable")
    })
    public ResponseEntity<AgenceResponse> update(
            @Parameter(description = "ID de l'agence", example = "1")
            @PathVariable("id") Long id,
            @RequestBody @Valid AgenceRequest request) {
        return ResponseEntity.ok(agenceService.update(id, request));
    }

    @PutMapping("id/{id}/toggle-active")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Activer / désactiver une agence", description = "Bascule le statut actif/inactif de l'agence.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Statut basculé"),
        @ApiResponse(responseCode = "404", description = "Agence introuvable")
    })
    public ResponseEntity<AgenceResponse> toggleActive(
            @Parameter(description = "ID de l'agence", example = "1")
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(agenceService.toggleActive(id));
    }

    @DeleteMapping("id/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Supprimer une agence", description = "Suppression définitive. Réservé ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Agence supprimée"),
        @ApiResponse(responseCode = "404", description = "Agence introuvable")
    })
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "ID de l'agence", example = "1")
            @PathVariable("id") Long id) {
        agenceService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
