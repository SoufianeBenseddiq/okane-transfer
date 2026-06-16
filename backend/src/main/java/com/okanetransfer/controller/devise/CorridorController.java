package com.okanetransfer.controller.devise;

import com.okanetransfer.service.facade.devise.ICorridorService;
import com.okanetransfer.service.facade.devise.IFraisService;
import com.okanetransfer.service.dto.devise.request.CorridorRequest;
import com.okanetransfer.service.dto.devise.request.GrilleTarifaireRequest;
import com.okanetransfer.service.dto.devise.response.CorridorResponse;
import com.okanetransfer.service.dto.devise.response.FraisResult;
import com.okanetransfer.service.dto.devise.response.GrilleTarifaireResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/admin/corridors")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Corridors & Tarifs", description = "Gestion des corridors de transfert (paires pays/devise) et de leurs grilles tarifaires (ADMIN)")
public class CorridorController {

    private final ICorridorService corridorService;
    private final IFraisService fraisService;

    public CorridorController(ICorridorService corridorService, IFraisService fraisService) {
        this.corridorService = corridorService;
        this.fraisService = fraisService;
    }

    @GetMapping
    @Operation(summary = "Lister tous les corridors")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste des corridors retournée")
    })
    public ResponseEntity<List<CorridorResponse>> getAll() {
        return ResponseEntity.ok(corridorService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Trouver un corridor par ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Corridor trouvé"),
        @ApiResponse(responseCode = "404", description = "Corridor introuvable")
    })
    public ResponseEntity<CorridorResponse> getById(
            @Parameter(description = "ID du corridor", example = "1")
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(corridorService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Créer un corridor", description = "Définit un nouveau couloir de transfert entre deux pays/devises.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Corridor créé"),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "409", description = "Ce corridor existe déjà")
    })
    public ResponseEntity<CorridorResponse> creer(@RequestBody CorridorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(corridorService.creer(request));
    }

    @PatchMapping("/{id}/activer")
    @Operation(summary = "Activer un corridor", description = "Rend le corridor disponible pour les transferts.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Corridor activé"),
        @ApiResponse(responseCode = "404", description = "Corridor introuvable")
    })
    public ResponseEntity<Void> activer(
            @Parameter(description = "ID du corridor", example = "1")
            @PathVariable("id") Long id) {
        corridorService.activer(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/desactiver")
    @Operation(summary = "Désactiver un corridor")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Corridor désactivé"),
        @ApiResponse(responseCode = "404", description = "Corridor introuvable")
    })
    public ResponseEntity<Void> desactiver(
            @Parameter(description = "ID du corridor", example = "1")
            @PathVariable("id") Long id) {
        corridorService.desactiver(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/frais")
    @Operation(summary = "Calculer les frais pour un montant", description = "Retourne les frais applicables et le montant converti pour un transfert via ce corridor.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Frais calculés"),
        @ApiResponse(responseCode = "404", description = "Corridor introuvable ou grille tarifaire manquante")
    })
    public ResponseEntity<FraisResult> calculerFrais(
            @Parameter(description = "ID du corridor", example = "1")
            @PathVariable("id") Long id,
            @Parameter(description = "Montant à envoyer (devise source)", example = "500.00")
            @RequestParam("montant") BigDecimal montant) {
        return ResponseEntity.ok(fraisService.calculerFrais(montant, id));
    }

    @PostMapping("/grilles")
    @Operation(summary = "Créer une grille tarifaire", description = "Ajoute une tranche de frais (ex : 0–500 € → 5 €) à un corridor.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Grille tarifaire créée"),
        @ApiResponse(responseCode = "400", description = "Données invalides ou chevauchement de tranches")
    })
    public ResponseEntity<Void> creerGrille(@RequestBody GrilleTarifaireRequest request) {
        fraisService.creerGrille(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}/grilles")
    @Operation(summary = "Lister les grilles tarifaires d'un corridor")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Grilles retournées"),
        @ApiResponse(responseCode = "404", description = "Corridor introuvable")
    })
    public ResponseEntity<List<GrilleTarifaireResponse>> getGrillesByCorridor(
            @Parameter(description = "ID du corridor", example = "1")
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(fraisService.getGrillesByCorridor(id));
    }

    @PutMapping("/grilles/{id}")
    @Operation(summary = "Modifier une grille tarifaire")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Grille mise à jour"),
        @ApiResponse(responseCode = "404", description = "Grille introuvable")
    })
    public ResponseEntity<GrilleTarifaireResponse> updateGrille(
            @Parameter(description = "ID de la grille tarifaire", example = "3")
            @PathVariable("id") Long id,
            @RequestBody GrilleTarifaireRequest request) {
        return ResponseEntity.ok(fraisService.updateGrille(id, request));
    }

    @DeleteMapping("/grilles/{id}")
    @Operation(summary = "Supprimer une grille tarifaire")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Grille supprimée"),
        @ApiResponse(responseCode = "404", description = "Grille introuvable")
    })
    public ResponseEntity<Void> deleteGrille(
            @Parameter(description = "ID de la grille tarifaire", example = "3")
            @PathVariable("id") Long id) {
        fraisService.deleteGrille(id);
        return ResponseEntity.noContent().build();
    }
}
