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

import com.okanetransfer.entity.aml.JournalAudit;
import com.okanetransfer.service.converter.aml.AmlConverter;
import com.okanetransfer.service.dto.aml.response.AuditResponse;
import com.okanetransfer.service.facade.aml.IJournalAuditService;

@RestController
@RequestMapping("/api/aml/audit")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "AML — Journal d'audit", description = "Consultation et gestion du journal d'audit Anti-Blanchiment (AML/LCB-FT). Réservé ADMIN / Compliance.")
public class JournalAuditController {

    private final IJournalAuditService journalAuditService;
    private final AmlConverter amlConverter;

    public JournalAuditController(IJournalAuditService journalAuditService,
                                  AmlConverter amlConverter) {
        this.journalAuditService = journalAuditService;
        this.amlConverter = amlConverter;
    }

    @GetMapping
    @Operation(summary = "Lister toutes les entrées du journal d'audit")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Journal retourné")
    })
    public List<AuditResponse> getAll() {
        return journalAuditService.getAll().stream()
                .map(amlConverter::toAuditResponse)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Trouver une entrée d'audit par ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Entrée trouvée"),
        @ApiResponse(responseCode = "404", description = "Entrée introuvable")
    })
    public AuditResponse getById(
            @Parameter(description = "ID de l'entrée d'audit", example = "1")
            @PathVariable Long id) {
        return amlConverter.toAuditResponse(journalAuditService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Créer une entrée d'audit", description = "Enregistre manuellement un événement dans le journal d'audit.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Entrée créée"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public AuditResponse create(@RequestBody JournalAudit journalAudit) {
        return amlConverter.toAuditResponse(journalAuditService.create(journalAudit));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une entrée d'audit")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Entrée mise à jour"),
        @ApiResponse(responseCode = "404", description = "Entrée introuvable")
    })
    public AuditResponse update(
            @Parameter(description = "ID de l'entrée d'audit", example = "1")
            @PathVariable Long id,
            @RequestBody JournalAudit journalAudit) {
        return amlConverter.toAuditResponse(journalAuditService.update(id, journalAudit));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une entrée d'audit")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Entrée supprimée"),
        @ApiResponse(responseCode = "404", description = "Entrée introuvable")
    })
    public void delete(
            @Parameter(description = "ID de l'entrée d'audit", example = "1")
            @PathVariable Long id) {
        journalAuditService.delete(id);
    }
}
