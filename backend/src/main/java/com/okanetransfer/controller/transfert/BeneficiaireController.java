package com.okanetransfer.Controller.transfert;

import com.okanetransfer.service.dto.transfert.request.CreateBeneficiaireRequest;
import com.okanetransfer.service.dto.transfert.request.UpdateBeneficiaireRequest;
import com.okanetransfer.service.dto.transfert.response.BeneficiaireResponse;
import com.okanetransfer.service.facade.transfert.IBeneficiaireService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/beneficiaires")
public class BeneficiaireController {

    private final IBeneficiaireService beneficiaireService;

    public BeneficiaireController(IBeneficiaireService beneficiaireService) {
        this.beneficiaireService = beneficiaireService;
    }

    @GetMapping
    public ResponseEntity<List<BeneficiaireResponse>> getAll() {
        return ResponseEntity.ok(
                beneficiaireService.getAllBeneficiaires()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BeneficiaireResponse> getById(
            @PathVariable("id") Long id) {

        return ResponseEntity.ok(
                beneficiaireService.getBeneficiaireById(id)
        );
    }

    @PostMapping
    public ResponseEntity<BeneficiaireResponse> create(
            @Valid @RequestBody CreateBeneficiaireRequest request) {

        return ResponseEntity.ok(
                beneficiaireService.createBeneficiaire(request)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<BeneficiaireResponse> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateBeneficiaireRequest request) {

        return ResponseEntity.ok(
                beneficiaireService.updateBeneficiaire(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable("id") Long id) {

        beneficiaireService.deleteBeneficiaire(id);
        return ResponseEntity.noContent().build();
    }
}
