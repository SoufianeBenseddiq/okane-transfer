package com.okanetransfer.controller.user;

import com.okanetransfer.service.dto.user.request.PieceIdentiteRequest;
import com.okanetransfer.service.dto.user.response.PieceIdentiteResponse;
import com.okanetransfer.service.facade.user.PieceIdentiteService;
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
@RequestMapping("/api/utilisateurs/{clientId}/pieces")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Pièces d'identité", description = "Gestion des documents d'identité (CIN, passeport, etc.) attachés à un utilisateur client")
public class PieceIdentiteController {

    private final PieceIdentiteService pieceIdentiteService;

    public PieceIdentiteController(PieceIdentiteService pieceIdentiteService) {
        this.pieceIdentiteService = pieceIdentiteService;
    }

    @GetMapping
    @Operation(summary = "Lister les pièces d'identité d'un client")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste des pièces retournée"),
        @ApiResponse(responseCode = "404", description = "Client introuvable")
    })
    public ResponseEntity<List<PieceIdentiteResponse>> findAll(
            @Parameter(description = "ID du client", example = "7")
            @PathVariable("clientId") Long clientId) {
        return ResponseEntity.ok(pieceIdentiteService.getPieces(clientId));
    }

    @PostMapping
    @Operation(summary = "Ajouter une pièce d'identité", description = "Associe un nouveau document d'identité à un client.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pièce ajoutée"),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "404", description = "Client introuvable")
    })
    public ResponseEntity<PieceIdentiteResponse> create(
            @Parameter(description = "ID du client", example = "7")
            @PathVariable("clientId") Long clientId,
            @Valid @RequestBody PieceIdentiteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pieceIdentiteService.ajouterPiece(clientId, request));
    }

    @PutMapping("/{pieceId}/principale")
    @Operation(summary = "Définir la pièce principale", description = "Marque une pièce comme document principal de vérification d'identité du client.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pièce principale définie"),
        @ApiResponse(responseCode = "404", description = "Client ou pièce introuvable")
    })
    public ResponseEntity<PieceIdentiteResponse> definirPrincipale(
            @Parameter(description = "ID du client", example = "7")
            @PathVariable("clientId") Long clientId,
            @Parameter(description = "ID de la pièce", example = "3")
            @PathVariable("pieceId") Long pieceId) {
        return ResponseEntity.ok(pieceIdentiteService.definirPrincipale(clientId, pieceId));
    }

    @DeleteMapping("/{pieceId}")
    @Operation(summary = "Supprimer une pièce d'identité")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Pièce supprimée"),
        @ApiResponse(responseCode = "404", description = "Client ou pièce introuvable")
    })
    public ResponseEntity<Void> supprimer(
            @Parameter(description = "ID du client", example = "7")
            @PathVariable("clientId") Long clientId,
            @Parameter(description = "ID de la pièce", example = "3")
            @PathVariable("pieceId") Long pieceId) {
        pieceIdentiteService.supprimerPiece(clientId, pieceId);
        return ResponseEntity.noContent().build();
    }
}
