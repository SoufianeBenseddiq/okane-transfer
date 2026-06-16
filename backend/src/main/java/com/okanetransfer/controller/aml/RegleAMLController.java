package com.okanetransfer.controller.aml;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.okanetransfer.entity.aml.RegleAML;
import com.okanetransfer.service.facade.aml.IRegleAMLService;

@RestController
@RequestMapping("/api/aml/regles")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "AML — Règles", description = "Gestion des règles Anti-Blanchiment (seuils, alertes automatiques). Réservé Compliance / ADMIN.")
public class RegleAMLController {

    private final IRegleAMLService regleAMLService;

    public RegleAMLController(IRegleAMLService regleAMLService) {
        this.regleAMLService = regleAMLService;
    }

    @GetMapping
    @Operation(summary = "Lister toutes les règles AML")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Règles retournées")
    })
    public List<RegleAML> getAll() {
        return regleAMLService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Trouver une règle AML par ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Règle trouvée"),
        @ApiResponse(responseCode = "404", description = "Règle introuvable")
    })
    public RegleAML getById(
            @Parameter(description = "ID de la règle", example = "1")
            @PathVariable Long id) {
        return regleAMLService.getById(id);
    }

    @PostMapping
    @Operation(summary = "Créer une règle AML", description = "Définit un nouveau seuil ou critère de détection de blanchiment.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Règle créée"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public RegleAML create(@RequestBody RegleAML regleAML) {
        return regleAMLService.create(regleAML);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une règle AML")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Règle mise à jour"),
        @ApiResponse(responseCode = "404", description = "Règle introuvable")
    })
    public RegleAML update(
            @Parameter(description = "ID de la règle", example = "1")
            @PathVariable Long id,
            @RequestBody RegleAML regleAML) {
        return regleAMLService.update(id, regleAML);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une règle AML")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Règle supprimée"),
        @ApiResponse(responseCode = "404", description = "Règle introuvable")
    })
    public void delete(
            @Parameter(description = "ID de la règle", example = "1")
            @PathVariable Long id) {
        regleAMLService.delete(id);
    }
}
