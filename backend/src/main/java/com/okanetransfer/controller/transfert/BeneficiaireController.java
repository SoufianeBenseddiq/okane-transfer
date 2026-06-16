package com.okanetransfer.controller.transfert;

import com.okanetransfer.service.dto.transfert.request.CreateBeneficiaireRequest;
import com.okanetransfer.service.dto.transfert.request.UpdateBeneficiaireRequest;
import com.okanetransfer.service.dto.transfert.response.BeneficiaireResponse;
import com.okanetransfer.service.facade.transfert.IBeneficiaireService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/beneficiaires")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Bénéficiaires", description = "Gestion des bénéficiaires de transferts (destinataires)")
public class BeneficiaireController {

    private final IBeneficiaireService beneficiaireService;

    public BeneficiaireController(IBeneficiaireService beneficiaireService) {
        this.beneficiaireService = beneficiaireService;
    }

    @GetMapping
    @Operation(summary = "Lister tous les bénéficiaires")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste retournée")
    })
    public ResponseEntity<List<BeneficiaireResponse>> getAll() {
        return ResponseEntity.ok(beneficiaireService.getAllBeneficiaires());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Trouver un bénéficiaire par ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Bénéficiaire trouvé"),
        @ApiResponse(responseCode = "404", description = "Bénéficiaire introuvable")
    })
    public ResponseEntity<BeneficiaireResponse> getById(
            @Parameter(description = "ID du bénéficiaire", example = "1")
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(beneficiaireService.getBeneficiaireById(id));
    }

    @PostMapping
    @Operation(summary = "Créer un bénéficiaire", description = "Enregistre un nouveau bénéficiaire pour les transferts futurs.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Bénéficiaire créé"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<BeneficiaireResponse> create(
            @Valid @RequestBody CreateBeneficiaireRequest request) {
        return ResponseEntity.ok(beneficiaireService.createBeneficiaire(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un bénéficiaire")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Bénéficiaire mis à jour"),
        @ApiResponse(responseCode = "404", description = "Bénéficiaire introuvable")
    })
    public ResponseEntity<BeneficiaireResponse> update(
            @Parameter(description = "ID du bénéficiaire", example = "1")
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateBeneficiaireRequest request) {
        return ResponseEntity.ok(beneficiaireService.updateBeneficiaire(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un bénéficiaire")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Bénéficiaire supprimé"),
        @ApiResponse(responseCode = "404", description = "Bénéficiaire introuvable")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID du bénéficiaire", example = "1")
            @PathVariable("id") Long id) {
        beneficiaireService.deleteBeneficiaire(id);
        return ResponseEntity.noContent().build();
    }
}
