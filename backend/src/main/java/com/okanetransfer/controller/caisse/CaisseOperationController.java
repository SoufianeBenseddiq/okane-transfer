package com.okanetransfer.controller.caisse;

import com.okanetransfer.service.dto.caisse.response.CaisseOperationResponse;
import com.okanetransfer.service.facade.caisse.CaisseOperationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/caisse-operations/")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Caisse — Opérations", description = "Suivi des opérations de caisse des agents (entrées/sorties d'espèces liées aux transferts)")
public class CaisseOperationController {

    private final CaisseOperationService caisseOperationService;

    public CaisseOperationController(CaisseOperationService caisseOperationService) {
        this.caisseOperationService = caisseOperationService;
    }

    @GetMapping("agent/{email}")
    @Operation(summary = "Opérations d'un agent", description = "Retourne toutes les opérations de caisse d'un agent identifié par son email.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Opérations retournées")
    })
    public ResponseEntity<List<CaisseOperationResponse>> findByAgentEmail(
            @Parameter(description = "Email de l'agent", example = "agent@okane.ma")
            @PathVariable("email") String email) {
        return ResponseEntity.ok(caisseOperationService.findByAgentEmail(email));
    }

    @DeleteMapping("id/{id}")
    @Operation(summary = "Supprimer une opération de caisse", description = "Suppression définitive. Réservé ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Opération supprimée"),
        @ApiResponse(responseCode = "404", description = "Opération introuvable")
    })
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "ID de l'opération", example = "1")
            @PathVariable("id") Long id) {
        caisseOperationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("all")
    @Operation(summary = "Lister toutes les opérations de caisse", description = "Vue globale pour ADMIN / MANAGER.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste retournée")
    })
    public ResponseEntity<List<CaisseOperationResponse>> findAll() {
        return ResponseEntity.ok(caisseOperationService.findAll());
    }

    @PostMapping("ouvrir")
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Ouvrir la caisse d'un agent", description = "Initialise la caisse avec un montant de départ. Réservé MANAGER / ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Caisse ouverte"),
        @ApiResponse(responseCode = "400", description = "Caisse déjà ouverte ou données invalides")
    })
    public ResponseEntity<CaisseOperationResponse> ouvrirCaisse(
            @Parameter(description = "Email de l'agent", example = "agent@okane.ma")
            @RequestParam("agentEmail") String agentEmail,
            @Parameter(description = "Montant initial en caisse", example = "10000.00")
            @RequestParam("montantInitial") BigDecimal montantInitial) {
        return ResponseEntity.ok(caisseOperationService.ouvrirCaisse(agentEmail, montantInitial));
    }

    @GetMapping("agent/{email}/solde")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_AGENT')")
    @Operation(summary = "Solde actuel de la caisse d'un agent")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Solde retourné (BigDecimal)"),
        @ApiResponse(responseCode = "404", description = "Agent introuvable")
    })
    public ResponseEntity<BigDecimal> consulterSolde(
            @Parameter(description = "Email de l'agent", example = "agent@okane.ma")
            @PathVariable("email") String email) {
        return ResponseEntity.ok(caisseOperationService.consulterSolde(email));
    }

    @GetMapping("{email}/operations")
    @PreAuthorize("hasRole('ROLE_AGENT')")
    @Operation(summary = "Historique complet des opérations de l'agent connecté")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Opérations retournées")
    })
    public ResponseEntity<List<CaisseOperationResponse>> historiqueOperations(
            @Parameter(description = "Email de l'agent", example = "agent@okane.ma")
            @PathVariable("email") String email) {
        return ResponseEntity.ok(caisseOperationService.historiqueOperations(email));
    }

    @GetMapping("agent/{email}/solde-jour")
    @PreAuthorize("hasRole('ROLE_AGENT')")
    @Operation(summary = "Solde de la caisse pour la journée en cours")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Solde du jour retourné")
    })
    public ResponseEntity<BigDecimal> consulterSoldeDuJour(
            @Parameter(description = "Email de l'agent", example = "agent@okane.ma")
            @PathVariable("email") String email) {
        return ResponseEntity.ok(caisseOperationService.consulterSoldeDuJour(email));
    }

    @GetMapping("agent/{email}/today")
    @PreAuthorize("hasRole('ROLE_AGENT')")
    @Operation(summary = "Opérations de caisse du jour")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Opérations du jour retournées")
    })
    public ResponseEntity<List<CaisseOperationResponse>> operationsDuJour(
            @Parameter(description = "Email de l'agent", example = "agent@okane.ma")
            @PathVariable("email") String email) {
        return ResponseEntity.ok(caisseOperationService.operationsDuJour(email));
    }

    @GetMapping("agent/{email}/historique")
    @PreAuthorize("hasRole('ROLE_AGENT')")
    @Operation(summary = "Historique filtré par période", description = "Retourne les opérations de caisse de l'agent entre deux dates (format yyyy-MM-dd).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Opérations filtrées retournées")
    })
    public ResponseEntity<List<CaisseOperationResponse>> historiqueFiltre(
            @Parameter(description = "Email de l'agent", example = "agent@okane.ma")
            @PathVariable("email") String email,
            @Parameter(description = "Date de début (yyyy-MM-dd)", example = "2025-01-01")
            @RequestParam("dateDebut") String dateDebut,
            @Parameter(description = "Date de fin (yyyy-MM-dd)", example = "2025-12-31")
            @RequestParam("dateFin") String dateFin) {
        LocalDate from = LocalDate.parse(dateDebut);
        LocalDate to   = LocalDate.parse(dateFin);
        return ResponseEntity.ok(caisseOperationService.historiqueFiltre(email, from, to));
    }
}
