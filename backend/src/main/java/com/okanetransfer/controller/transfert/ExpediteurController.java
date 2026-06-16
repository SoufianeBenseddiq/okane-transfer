package com.okanetransfer.controller.transfert;

import com.okanetransfer.service.dto.transfert.request.CreateExpediteurRequest;
import com.okanetransfer.service.dto.transfert.request.UpdateExpediteurRequest;
import com.okanetransfer.service.dto.transfert.response.ExpediteurResponse;
import com.okanetransfer.service.facade.transfert.IExpediteurService;
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
@RequestMapping("/api/expediteurs")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Expéditeurs", description = "Gestion des expéditeurs (clients envoyant un transfert)")
public class ExpediteurController {

    private final IExpediteurService expediteurService;

    public ExpediteurController(IExpediteurService expediteurService) {
        this.expediteurService = expediteurService;
    }

    @GetMapping
    @Operation(summary = "Lister tous les expéditeurs")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste retournée")
    })
    public ResponseEntity<List<ExpediteurResponse>> getAll() {
        return ResponseEntity.ok(expediteurService.getAllExpediteurs());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Trouver un expéditeur par ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Expéditeur trouvé"),
        @ApiResponse(responseCode = "404", description = "Expéditeur introuvable")
    })
    public ResponseEntity<ExpediteurResponse> getById(
            @Parameter(description = "ID de l'expéditeur", example = "1")
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(expediteurService.getExpediteurById(id));
    }

    @PostMapping
    @Operation(summary = "Créer un expéditeur", description = "Enregistre un nouvel expéditeur dans le système.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Expéditeur créé"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<ExpediteurResponse> create(
            @Valid @RequestBody CreateExpediteurRequest request) {
        return ResponseEntity.ok(expediteurService.createExpediteur(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un expéditeur")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Expéditeur mis à jour"),
        @ApiResponse(responseCode = "404", description = "Expéditeur introuvable")
    })
    public ResponseEntity<ExpediteurResponse> update(
            @Parameter(description = "ID de l'expéditeur", example = "1")
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateExpediteurRequest request) {
        return ResponseEntity.ok(expediteurService.updateExpediteur(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un expéditeur")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Expéditeur supprimé"),
        @ApiResponse(responseCode = "404", description = "Expéditeur introuvable")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de l'expéditeur", example = "1")
            @PathVariable("id") Long id) {
        expediteurService.deleteExpediteur(id);
        return ResponseEntity.noContent().build();
    }
}
