package com.okanetransfer.controller.mobilemoney;

import com.okanetransfer.service.dto.mobilemoney.request.CreateTransfertMobileMoneyRequest;
import com.okanetransfer.service.dto.mobilemoney.request.UpdateTransfertMobileMoneyRequest;
import com.okanetransfer.service.dto.mobilemoney.response.TransfertMobileMoneyResponse;
import com.okanetransfer.service.facade.mobilemoney.ITransfertMobileMoneyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transferts-mobile-money")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Mobile Money", description = "Transferts via Mobile Money (Wave, Orange Money, MTN, etc.) — couche complémentaire aux transferts classiques")
public class TransfertMobileMoneyController {

    private final ITransfertMobileMoneyService transfertMobileMoneyService;

    public TransfertMobileMoneyController(ITransfertMobileMoneyService transfertMobileMoneyService) {
        this.transfertMobileMoneyService = transfertMobileMoneyService;
    }

    @GetMapping
    @Operation(summary = "Lister tous les transferts Mobile Money")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste retournée")
    })
    public ResponseEntity<List<TransfertMobileMoneyResponse>> getAll() {
        return ResponseEntity.ok(transfertMobileMoneyService.getAllTransfertsMobileMoney());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Trouver un transfert Mobile Money par ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transfert trouvé"),
        @ApiResponse(responseCode = "404", description = "Transfert introuvable")
    })
    public ResponseEntity<TransfertMobileMoneyResponse> getById(
            @Parameter(description = "ID du transfert Mobile Money", example = "1")
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(transfertMobileMoneyService.getTransfertMobileMoneyById(id));
    }

    @PostMapping
    @Operation(summary = "Créer un transfert Mobile Money", description = "Initie un paiement via un opérateur Mobile Money. Le transfert classique doit déjà exister.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transfert Mobile Money créé"),
        @ApiResponse(responseCode = "400", description = "Données invalides ou opérateur non supporté"),
        @ApiResponse(responseCode = "404", description = "Transfert classique associé introuvable")
    })
    public ResponseEntity<TransfertMobileMoneyResponse> create(
            @RequestBody CreateTransfertMobileMoneyRequest request) {
        return ResponseEntity.ok(transfertMobileMoneyService.createTransfertMobileMoney(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un transfert Mobile Money", description = "Met à jour le statut ou la référence opérateur d'un transfert Mobile Money.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transfert mis à jour"),
        @ApiResponse(responseCode = "404", description = "Transfert introuvable")
    })
    public ResponseEntity<TransfertMobileMoneyResponse> update(
            @Parameter(description = "ID du transfert Mobile Money", example = "1")
            @PathVariable("id") Long id,
            @RequestBody UpdateTransfertMobileMoneyRequest request) {
        return ResponseEntity.ok(transfertMobileMoneyService.updateTransfertMobileMoney(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un transfert Mobile Money")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Transfert supprimé"),
        @ApiResponse(responseCode = "404", description = "Transfert introuvable")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID du transfert Mobile Money", example = "1")
            @PathVariable("id") Long id) {
        transfertMobileMoneyService.deleteTransfertMobileMoney(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/transfert/{transfertId}")
    @Operation(summary = "Trouver par ID du transfert classique associé")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transfert Mobile Money trouvé"),
        @ApiResponse(responseCode = "404", description = "Aucun transfert Mobile Money pour ce transfert")
    })
    public ResponseEntity<TransfertMobileMoneyResponse> getByTransfertId(
            @Parameter(description = "ID du transfert classique", example = "42")
            @PathVariable("transfertId") Long transfertId) {
        return ResponseEntity.ok(transfertMobileMoneyService.getByTransfertId(transfertId));
    }

    @GetMapping("/operateur/{operateur}")
    @Operation(summary = "Filtrer par opérateur Mobile Money", description = "Exemples d'opérateur : WAVE, ORANGE_MONEY, MTN.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transferts retournés")
    })
    public ResponseEntity<List<TransfertMobileMoneyResponse>> getByOperateur(
            @Parameter(description = "Nom de l'opérateur", example = "WAVE")
            @PathVariable("operateur") String operateur) {
        return ResponseEntity.ok(transfertMobileMoneyService.getByOperateur(operateur));
    }

    @GetMapping("/statut/{statutMobile}")
    @Operation(summary = "Filtrer par statut Mobile Money", description = "Exemples : PENDING, SUCCESS, FAILED.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transferts retournés")
    })
    public ResponseEntity<List<TransfertMobileMoneyResponse>> getByStatutMobile(
            @Parameter(description = "Statut Mobile Money", example = "SUCCESS")
            @PathVariable("statutMobile") String statutMobile) {
        return ResponseEntity.ok(transfertMobileMoneyService.getByStatutMobile(statutMobile));
    }

    @GetMapping("/reference/{referenceOperateur}")
    @Operation(summary = "Trouver par référence opérateur", description = "La référence unique assignée par l'opérateur Mobile Money après confirmation.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transfert trouvé"),
        @ApiResponse(responseCode = "404", description = "Aucun transfert pour cette référence")
    })
    public ResponseEntity<TransfertMobileMoneyResponse> getByReferenceOperateur(
            @Parameter(description = "Référence de l'opérateur", example = "WV-2025-ABC123")
            @PathVariable("referenceOperateur") String referenceOperateur) {
        return ResponseEntity.ok(transfertMobileMoneyService.getByReferenceOperateur(referenceOperateur));
    }
}
