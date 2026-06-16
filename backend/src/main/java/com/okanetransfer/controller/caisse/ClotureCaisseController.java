package com.okanetransfer.controller.caisse;

import com.okanetransfer.service.dto.caisse.request.ClotureCaisseRequest;
import com.okanetransfer.service.dto.caisse.response.ClotureCaisseResponse;
import com.okanetransfer.service.facade.caisse.ClotureCaisseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/clotures-caisse/")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Caisse — Clôtures", description = "Clôture journalière de caisse : rapport de fin de journée de l'agent, détection des écarts")
public class ClotureCaisseController {

    private final ClotureCaisseService clotureCaisseService;

    public ClotureCaisseController(ClotureCaisseService clotureCaisseService) {
        this.clotureCaisseService = clotureCaisseService;
    }

    @GetMapping("agent/{email}/date/{date}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_AGENT')")
    @Operation(summary = "Clôture d'un agent pour une date donnée", description = "Retourne la clôture de caisse d'un agent pour la date spécifiée (format yyyy-MM-dd). Renvoie 404 si aucune clôture n'a encore été enregistrée.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Clôture trouvée"),
        @ApiResponse(responseCode = "404", description = "Aucune clôture pour cette date")
    })
    public ResponseEntity<ClotureCaisseResponse> findByAgentEmailAndDate(
            @Parameter(description = "Email de l'agent", example = "agent@okane.ma")
            @PathVariable("email") String email,
            @Parameter(description = "Date de clôture (yyyy-MM-dd)", example = "2025-06-15")
            @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            return ResponseEntity.ok(clotureCaisseService.findByAgentEmailAndDate(email, date));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("ecarts")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    @Operation(summary = "Clôtures avec écart signalé", description = "Liste toutes les clôtures où un écart de caisse a été détecté. Réservé MANAGER / ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Clôtures avec écart retournées")
    })
    public ResponseEntity<List<ClotureCaisseResponse>> findByEcartSignaleTrue() {
        return ResponseEntity.ok(clotureCaisseService.findByEcartSignaleTrue());
    }

    @GetMapping("agent/{email}/ecarts")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    @Operation(summary = "Écarts de caisse d'un agent spécifique")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Clôtures avec écart de l'agent retournées")
    })
    public ResponseEntity<List<ClotureCaisseResponse>> findByAgentEmailAndEcartSignaleTrue(
            @Parameter(description = "Email de l'agent", example = "agent@okane.ma")
            @PathVariable("email") String email) {
        return ResponseEntity.ok(clotureCaisseService.findByAgentEmailAndEcartSignaleTrue(email));
    }

    @PostMapping("add-one")
    @PreAuthorize("hasRole('ROLE_AGENT')")
    @Operation(summary = "Enregistrer une clôture de caisse", description = "L'agent soumet son rapport de fin de journée avec le montant compté en caisse.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Clôture enregistrée"),
        @ApiResponse(responseCode = "400", description = "Données invalides ou clôture déjà existante pour cette date")
    })
    public ResponseEntity<ClotureCaisseResponse> save(
            @RequestBody @Valid ClotureCaisseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clotureCaisseService.save(request));
    }

    @PutMapping("update")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    @Operation(summary = "Corriger une clôture de caisse", description = "Permet à un manager de corriger une clôture déjà soumise. Réservé MANAGER / ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Clôture corrigée"),
        @ApiResponse(responseCode = "404", description = "Clôture introuvable")
    })
    public ResponseEntity<ClotureCaisseResponse> update(
            @RequestBody @Valid ClotureCaisseRequest request) {
        return ResponseEntity.ok(clotureCaisseService.update(request));
    }

    @DeleteMapping("id/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Supprimer une clôture", description = "Suppression définitive. Réservé ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Clôture supprimée"),
        @ApiResponse(responseCode = "404", description = "Clôture introuvable")
    })
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "ID de la clôture", example = "1")
            @PathVariable Long id) {
        clotureCaisseService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("all")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    @Operation(summary = "Lister toutes les clôtures", description = "Vue globale pour ADMIN / MANAGER.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste retournée")
    })
    public ResponseEntity<List<ClotureCaisseResponse>> findAll() {
        return ResponseEntity.ok(clotureCaisseService.findAll());
    }

    @GetMapping("/{email}/rapport")
    @PreAuthorize("hasRole('ROLE_AGENT')")
    @Operation(summary = "Rapport de clôture d'un agent pour une date", description = "Génère le rapport de clôture de caisse pour la date demandée.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Rapport retourné"),
        @ApiResponse(responseCode = "404", description = "Aucune clôture pour cette date")
    })
    public ResponseEntity<ClotureCaisseResponse> rapportCloture(
            @Parameter(description = "Email de l'agent", example = "agent@okane.ma")
            @PathVariable("email") String email,
            @Parameter(description = "Date (yyyy-MM-dd)", example = "2025-06-15")
            @RequestParam("date") String date) {
        return ResponseEntity.ok(clotureCaisseService.rapportCloture(email, LocalDate.parse(date)));
    }

    @PostMapping("/{email}/cloturer")
    @PreAuthorize("hasRole('ROLE_AGENT')")
    @Operation(summary = "Clôturer la caisse du jour", description = "Action de fin de journée : l'agent déclare le montant physique et génère la clôture officielle.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Caisse clôturée"),
        @ApiResponse(responseCode = "400", description = "Clôture déjà effectuée ou données invalides")
    })
    public ResponseEntity<ClotureCaisseResponse> cloturerCaisse(
            @Parameter(description = "Email de l'agent", example = "agent@okane.ma")
            @PathVariable("email") String email,
            @RequestBody ClotureCaisseRequest request) {
        request.setAgentEmail(email);
        return ResponseEntity.ok(clotureCaisseService.cloturerCaisse(request));
    }
}
