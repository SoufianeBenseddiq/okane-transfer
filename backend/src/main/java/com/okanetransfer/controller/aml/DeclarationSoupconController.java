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

import com.okanetransfer.entity.aml.DeclarationSoupcon;
import com.okanetransfer.service.converter.aml.AmlConverter;
import com.okanetransfer.service.dto.aml.response.DeclarationResponse;
import com.okanetransfer.service.facade.aml.IDeclarationSoupconService;

@RestController
@RequestMapping("/api/aml/declarations")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "AML — Déclarations de soupçon", description = "Gestion des déclarations de soupçon (DS) transmises aux autorités de lutte contre le blanchiment. Réservé Compliance / ADMIN.")
public class DeclarationSoupconController {

    private final IDeclarationSoupconService declarationService;
    private final AmlConverter amlConverter;

    public DeclarationSoupconController(IDeclarationSoupconService declarationService,
                                        AmlConverter amlConverter) {
        this.declarationService = declarationService;
        this.amlConverter = amlConverter;
    }

    @GetMapping
    @Operation(summary = "Lister toutes les déclarations de soupçon")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste retournée")
    })
    public List<DeclarationResponse> getAll() {
        return declarationService.getAll().stream()
                .map(amlConverter::toDeclarationResponse)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Trouver une déclaration de soupçon par ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Déclaration trouvée"),
        @ApiResponse(responseCode = "404", description = "Déclaration introuvable")
    })
    public DeclarationResponse getById(
            @Parameter(description = "ID de la déclaration", example = "1")
            @PathVariable("id") Long id) {
        return amlConverter.toDeclarationResponse(declarationService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Créer une déclaration de soupçon", description = "Enregistre une nouvelle DS suite à une opération suspecte détectée.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Déclaration créée"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public DeclarationResponse create(@RequestBody DeclarationSoupcon declaration) {
        return amlConverter.toDeclarationResponse(declarationService.create(declaration));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une déclaration de soupçon")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Déclaration mise à jour"),
        @ApiResponse(responseCode = "404", description = "Déclaration introuvable")
    })
    public DeclarationResponse update(
            @Parameter(description = "ID de la déclaration", example = "1")
            @PathVariable("id") Long id,
            @RequestBody DeclarationSoupcon declaration) {
        return amlConverter.toDeclarationResponse(declarationService.update(id, declaration));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une déclaration de soupçon")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Déclaration supprimée"),
        @ApiResponse(responseCode = "404", description = "Déclaration introuvable")
    })
    public void delete(
            @Parameter(description = "ID de la déclaration", example = "1")
            @PathVariable("id") Long id) {
        declarationService.delete(id);
    }
}
