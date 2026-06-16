package com.okanetransfer.controller.transfert;

import com.okanetransfer.service.dto.transfert.request.CreateTransfertRequest;
import com.okanetransfer.service.dto.transfert.request.CreateTransfertAvecNouveauClientRequest;
import com.okanetransfer.service.dto.transfert.request.PaiementRequest;
import com.okanetransfer.service.dto.transfert.request.SendTransfertReceiptEmailRequest;
import com.okanetransfer.service.dto.transfert.request.UpdateTransfertRequest;
import com.okanetransfer.service.dto.transfert.response.TransfertResponse;
import com.okanetransfer.service.dto.transfert.response.TransfertStatsResponse;
import com.okanetransfer.service.facade.transfert.ITransfertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transferts")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Transferts", description = "Création, suivi, paiement et annulation de transferts d'argent")
public class TransfertController {

    private final ITransfertService transfertService;

    public TransfertController(ITransfertService transfertService) {
        this.transfertService = transfertService;
    }

    @GetMapping
    @Operation(summary = "Lister tous les transferts", description = "Retourne l'ensemble des transferts. Réservé ADMIN / MANAGER.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste de transferts retournée")
    })
    public ResponseEntity<List<TransfertResponse>> getAll() {
        return ResponseEntity.ok(transfertService.getAllTransferts());
    }

    @GetMapping("/stats")
    @Operation(summary = "Statistiques globales des transferts",
               description = "Retourne le nombre total de transferts, le montant total, les répartitions par statut. Réservé ADMIN / MANAGER.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Statistiques retournées")
    })
    public ResponseEntity<TransfertStatsResponse> getStats() {
        return ResponseEntity.ok(transfertService.getStats());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Trouver un transfert par ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transfert trouvé"),
        @ApiResponse(responseCode = "404", description = "Transfert introuvable")
    })
    public ResponseEntity<TransfertResponse> getById(
            @Parameter(description = "ID du transfert", example = "42")
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(transfertService.getTransfertById(id));
    }

    @GetMapping("/agent/{email}/commissions")
    @Operation(summary = "Commissions d'un agent sur une période",
               description = "Retourne le montant total des commissions perçues par l'agent entre deux dates (format yyyy-MM-dd).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Montant des commissions (BigDecimal)"),
        @ApiResponse(responseCode = "404", description = "Agent introuvable")
    })
    public ResponseEntity<BigDecimal> commissionsAgent(
            @Parameter(description = "Email de l'agent", example = "agent@okane.ma")
            @PathVariable String email,
            @Parameter(description = "Date de début (yyyy-MM-dd)", example = "2025-01-01")
            @RequestParam String dateDebut,
            @Parameter(description = "Date de fin (yyyy-MM-dd)", example = "2025-12-31")
            @RequestParam String dateFin) {
        return ResponseEntity.ok(transfertService.commissionsAgent(email,
                LocalDate.parse(dateDebut), LocalDate.parse(dateFin)));
    }

    @PostMapping
    @Operation(summary = "Créer un transfert", description = "Crée un nouveau transfert pour un client existant. Retourne le transfert avec son code de retrait.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transfert créé avec code de retrait"),
        @ApiResponse(responseCode = "400", description = "Données invalides (montant, corridor, etc.)"),
        @ApiResponse(responseCode = "404", description = "Client ou bénéficiaire introuvable")
    })
    public ResponseEntity<TransfertResponse> create(@RequestBody CreateTransfertRequest request) {
        return ResponseEntity.ok(transfertService.creerTransfert(request));
    }

    @PostMapping("/avec-nouveau-client")
    @Operation(summary = "Créer un transfert avec un nouveau client",
               description = "Crée simultanément un compte client et son premier transfert. Utilisé par les agents en agence.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Client créé et transfert initié"),
        @ApiResponse(responseCode = "400", description = "Données client ou transfert invalides"),
        @ApiResponse(responseCode = "409", description = "Email client déjà existant")
    })
    public ResponseEntity<TransfertResponse> createAvecNouveauClient(
            @RequestBody @Valid CreateTransfertAvecNouveauClientRequest request) {
        return ResponseEntity.ok(transfertService.creerTransfertAvecNouveauClient(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un transfert", description = "Met à jour un transfert dont le statut est encore EN_ATTENTE.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transfert mis à jour"),
        @ApiResponse(responseCode = "400", description = "Modification impossible — statut incompatible"),
        @ApiResponse(responseCode = "404", description = "Transfert introuvable")
    })
    public ResponseEntity<TransfertResponse> update(
            @Parameter(description = "ID du transfert", example = "42")
            @PathVariable("id") Long id,
            @RequestBody UpdateTransfertRequest request) {
        return ResponseEntity.ok(transfertService.updateTransfert(id, request));
    }

    @PostMapping("/paiement")
    @Operation(summary = "Payer un transfert (retrait en agence)",
               description = "Valide le paiement au bénéficiaire via le code de retrait. Action réservée aux AGENTS.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transfert marqué PAYÉ"),
        @ApiResponse(responseCode = "400", description = "Code de retrait incorrect ou transfert non éligible"),
        @ApiResponse(responseCode = "404", description = "Transfert introuvable")
    })
    public ResponseEntity<TransfertResponse> payer(@RequestBody PaiementRequest request) {
        return ResponseEntity.ok(transfertService.payerTransfert(request));
    }

    @PutMapping("/{id}/annuler")
    @Operation(summary = "Annuler un transfert", description = "Annule un transfert EN_ATTENTE. Déclenche un remboursement.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transfert annulé"),
        @ApiResponse(responseCode = "400", description = "Annulation impossible — transfert déjà payé ou expiré"),
        @ApiResponse(responseCode = "404", description = "Transfert introuvable")
    })
    public ResponseEntity<TransfertResponse> annuler(
            @Parameter(description = "ID du transfert", example = "42")
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(transfertService.annulerTransfert(id));
    }

    @GetMapping("code/{codeRetrait}")
    @Operation(summary = "Trouver un transfert par code de retrait",
               description = "Utilisé par l'agent pour vérifier un transfert avant paiement.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transfert correspondant retourné"),
        @ApiResponse(responseCode = "404", description = "Aucun transfert pour ce code")
    })
    public ResponseEntity<TransfertResponse> getByCodeRetrait(
            @Parameter(description = "Code de retrait (ex : AB12-CD34)", example = "AB12-CD34")
            @PathVariable("codeRetrait") String codeRetrait) {
        return ResponseEntity.ok(transfertService.getByCodeRetrait(codeRetrait));
    }

    @GetMapping("/mes-transferts")
    @Operation(summary = "Transferts d'un client", description = "Retourne tous les transferts créés par le client identifié par son ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste des transferts du client")
    })
    public ResponseEntity<List<TransfertResponse>> getMesTransferts(
            @Parameter(description = "ID du client", example = "7")
            @RequestParam("clientId") Long clientId) {
        return ResponseEntity.ok(transfertService.getMesTransferts(clientId));
    }

    @GetMapping("/telephone/{telephone}")
    @Operation(summary = "Trouver un transfert par téléphone du bénéficiaire")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transfert trouvé"),
        @ApiResponse(responseCode = "404", description = "Aucun transfert pour ce numéro")
    })
    public ResponseEntity<TransfertResponse> getByTelephoneBeneficiaire(
            @Parameter(description = "Numéro de téléphone du bénéficiaire", example = "+212612345678")
            @PathVariable("telephone") String telephone) {
        return ResponseEntity.ok(transfertService.getByTelephoneBeneficiaire(telephone));
    }

    @PostMapping("/email-recu")
    @Operation(summary = "Envoyer le reçu par email",
               description = "Envoie un email de confirmation / reçu PDF au client pour un transfert donné.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Email envoyé"),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "404", description = "Transfert introuvable")
    })
    public ResponseEntity<Void> envoyerRecuParEmail(
            @RequestBody @Valid SendTransfertReceiptEmailRequest request) {
        transfertService.envoyerRecuParEmail(request);
        return ResponseEntity.ok().build();
    }
}
