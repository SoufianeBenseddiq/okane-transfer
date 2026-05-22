package com.okanetransfer.controller.devise;

import com.okanetransfer.service.facade.devise.ICorridorService;
import com.okanetransfer.service.facade.devise.IFraisService;
import com.okanetransfer.service.dto.devise.request.CorridorRequest;
import com.okanetransfer.service.dto.devise.request.GrilleTarifaireRequest;
import com.okanetransfer.service.dto.devise.response.CorridorResponse;
import com.okanetransfer.service.dto.devise.response.FraisResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/admin/corridors")
public class CorridorController {

    private final ICorridorService corridorService;
    private final IFraisService fraisService;

    public CorridorController(ICorridorService corridorService, IFraisService fraisService) {
        this.corridorService = corridorService;
        this.fraisService = fraisService;
    }

    @GetMapping
    public ResponseEntity<List<CorridorResponse>> getAll() {
        return ResponseEntity.ok(corridorService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CorridorResponse> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(corridorService.getById(id));
    }

    @PostMapping
    public ResponseEntity<CorridorResponse> creer(@RequestBody CorridorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(corridorService.creer(request));
    }

    @PatchMapping("/{id}/activer")
    public ResponseEntity<Void> activer(@PathVariable("id") Long id) {
        corridorService.activer(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/desactiver")
    public ResponseEntity<Void> desactiver(@PathVariable("id") Long id) {
        corridorService.desactiver(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/frais")
    public ResponseEntity<FraisResult> calculerFrais(
            @PathVariable("id") Long id,
            @RequestParam("montant") BigDecimal montant) {
        return ResponseEntity.ok(fraisService.calculerFrais(montant, id));
    }

    @PostMapping("/grilles")
    public ResponseEntity<Void> creerGrille(@RequestBody GrilleTarifaireRequest request) {
        fraisService.creerGrille(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}