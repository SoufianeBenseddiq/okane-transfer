package com.okanetransfer.controller.devise;

import com.okanetransfer.service.facade.devise.IPaysService;
import com.okanetransfer.service.dto.devise.request.PaysRequest;
import com.okanetransfer.service.dto.devise.response.PaysResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/pays")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Pays (Admin)", description = "Gestion des pays supportés — création et modification (ADMIN uniquement)")
public class AdminPaysController {

    private final IPaysService paysService;

    public AdminPaysController(IPaysService paysService) {
        this.paysService = paysService;
    }

    @GetMapping
    @Operation(summary = "Lister tous les pays")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste retournée")
    })
    public ResponseEntity<List<PaysResponse>> getAll() {
        return ResponseEntity.ok(paysService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Trouver un pays par ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pays trouvé"),
        @ApiResponse(responseCode = "404", description = "Pays introuvable")
    })
    public ResponseEntity<PaysResponse> getById(
            @Parameter(description = "ID du pays", example = "1")
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(paysService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Créer un pays", description = "Ajoute un nouveau pays à la plateforme. Réservé ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pays créé"),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "409", description = "Pays déjà existant")
    })
    public ResponseEntity<PaysResponse> creer(@Valid @RequestBody PaysRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paysService.creer(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un pays")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pays mis à jour"),
        @ApiResponse(responseCode = "404", description = "Pays introuvable")
    })
    public ResponseEntity<PaysResponse> modifier(
            @Parameter(description = "ID du pays", example = "1")
            @PathVariable("id") Long id,
            @Valid @RequestBody PaysRequest request) {
        return ResponseEntity.ok(paysService.modifier(id, request));
    }
}
