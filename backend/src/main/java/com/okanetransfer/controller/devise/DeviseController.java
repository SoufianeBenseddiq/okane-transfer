package com.okanetransfer.controller.devise;

import com.okanetransfer.service.facade.devise.IDeviseService;
import com.okanetransfer.service.dto.devise.request.DeviseRequest;
import com.okanetransfer.service.dto.devise.response.DeviseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/devises")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Devises", description = "Gestion des devises supportées par la plateforme (ADMIN)")
public class DeviseController {

    private final IDeviseService deviseService;

    public DeviseController(IDeviseService deviseService) {
        this.deviseService = deviseService;
    }

    @GetMapping
    @Operation(summary = "Lister toutes les devises")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste retournée")
    })
    public ResponseEntity<List<DeviseResponse>> getAll() {
        return ResponseEntity.ok(deviseService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Trouver une devise par ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Devise trouvée"),
        @ApiResponse(responseCode = "404", description = "Devise introuvable")
    })
    public ResponseEntity<DeviseResponse> getById(
            @Parameter(description = "ID de la devise", example = "1")
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(deviseService.getById(id));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Trouver une devise par code ISO", description = "Exemple : EUR, USD, MAD.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Devise trouvée"),
        @ApiResponse(responseCode = "404", description = "Devise introuvable")
    })
    public ResponseEntity<DeviseResponse> getByCode(
            @Parameter(description = "Code ISO de la devise", example = "EUR")
            @PathVariable("code") String code) {
        return ResponseEntity.ok(deviseService.getByCode(code));
    }

    @PostMapping
    @Operation(summary = "Créer une devise", description = "Ajoute une nouvelle devise à la plateforme. Réservé ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Devise créée"),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "409", description = "Code devise déjà existant")
    })
    public ResponseEntity<DeviseResponse> creer(@RequestBody DeviseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(deviseService.creer(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une devise")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Devise mise à jour"),
        @ApiResponse(responseCode = "404", description = "Devise introuvable")
    })
    public ResponseEntity<DeviseResponse> modifier(
            @Parameter(description = "ID de la devise", example = "1")
            @PathVariable("id") Long id,
            @RequestBody DeviseRequest request) {
        return ResponseEntity.ok(deviseService.modifier(id, request));
    }

    @PatchMapping("/{id}/activer")
    @Operation(summary = "Activer une devise", description = "Rend la devise disponible pour les transferts.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Devise activée"),
        @ApiResponse(responseCode = "404", description = "Devise introuvable")
    })
    public ResponseEntity<Void> activer(
            @Parameter(description = "ID de la devise", example = "1")
            @PathVariable("id") Long id) {
        deviseService.activer(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/desactiver")
    @Operation(summary = "Désactiver une devise", description = "Retire la devise des transferts disponibles.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Devise désactivée"),
        @ApiResponse(responseCode = "404", description = "Devise introuvable")
    })
    public ResponseEntity<Void> desactiver(
            @Parameter(description = "ID de la devise", example = "1")
            @PathVariable("id") Long id) {
        deviseService.desactiver(id);
        return ResponseEntity.noContent().build();
    }
}
