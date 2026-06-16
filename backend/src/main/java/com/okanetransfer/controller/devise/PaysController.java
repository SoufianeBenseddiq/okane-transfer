package com.okanetransfer.controller.devise;

import com.okanetransfer.service.facade.devise.IPaysService;
import com.okanetransfer.service.dto.devise.response.PaysResponse;
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
@RequestMapping("/api/pays")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Pays", description = "Consultation de la liste des pays supportés (lecture seule — utilisez /api/admin/pays pour les modifications)")
public class PaysController {

    private final IPaysService paysService;

    public PaysController(IPaysService paysService) {
        this.paysService = paysService;
    }

    @GetMapping
    @Operation(summary = "Lister tous les pays", description = "Retourne la liste complète des pays disponibles sur la plateforme.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste des pays retournée")
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
}
