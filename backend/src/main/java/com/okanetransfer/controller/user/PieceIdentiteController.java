package com.okanetransfer.controller.user;

import com.okanetransfer.service.dto.user.request.PieceIdentiteRequest;
import com.okanetransfer.service.dto.user.response.PieceIdentiteResponse;
import com.okanetransfer.service.facade.user.PieceIdentiteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs/{clientId}/pieces")
public class PieceIdentiteController {

    private final PieceIdentiteService pieceIdentiteService;

    public PieceIdentiteController(PieceIdentiteService pieceIdentiteService) {
        this.pieceIdentiteService = pieceIdentiteService;
    }

    @GetMapping
    public ResponseEntity<List<PieceIdentiteResponse>> findAll(@PathVariable Long clientId) {
        return ResponseEntity.ok(pieceIdentiteService.getPieces(clientId));
    }

    @PostMapping
    public ResponseEntity<PieceIdentiteResponse> create(
            @PathVariable Long clientId,
            @Valid @RequestBody PieceIdentiteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pieceIdentiteService.ajouterPiece(clientId, request));
    }

    @PutMapping("/{pieceId}/principale")
    public ResponseEntity<PieceIdentiteResponse> definirPrincipale(
            @PathVariable Long clientId,
            @PathVariable Long pieceId) {
        return ResponseEntity.ok(pieceIdentiteService.definirPrincipale(clientId, pieceId));
    }

    @DeleteMapping("/{pieceId}")
    public ResponseEntity<Void> supprimer(
            @PathVariable Long clientId,
            @PathVariable Long pieceId) {
        pieceIdentiteService.supprimerPiece(clientId, pieceId);
        return ResponseEntity.noContent().build();
    }
}
