package com.okanetransfer.controller.transfert;


import com.okanetransfer.service.dto.transfert.request.CreateTransfertRequest;
import com.okanetransfer.service.dto.transfert.request.PaiementRequest;
import com.okanetransfer.service.dto.transfert.request.UpdateTransfertRequest;
import com.okanetransfer.service.dto.transfert.response.TransfertResponse;
import com.okanetransfer.service.facade.transfert.ITransfertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transferts")
public class TransfertController {

    private final ITransfertService transfertService;

    public TransfertController(
            ITransfertService transfertService) {

        this.transfertService = transfertService;
    }

    @GetMapping
    public ResponseEntity<List<TransfertResponse>> getAll() {
        return ResponseEntity.ok(
                transfertService.getAllTransferts()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransfertResponse> getById(
            @PathVariable("id") Long id) {

        return ResponseEntity.ok(
                transfertService.getTransfertById(id)
        );
    }

    @PostMapping
    public ResponseEntity<TransfertResponse> create(
            @RequestBody CreateTransfertRequest request){

        return ResponseEntity.ok(
                transfertService.creerTransfert(request)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransfertResponse> update(
            @PathVariable("id") Long id,
            @RequestBody UpdateTransfertRequest request) {

        return ResponseEntity.ok(
                transfertService.updateTransfert(id, request)
        );
    }

    @PostMapping("/paiement")
    public ResponseEntity<TransfertResponse> payer(
            @RequestBody PaiementRequest request) {

        return ResponseEntity.ok(
                transfertService.payerTransfert(request)
        );
    }

    @PutMapping("/{id}/annuler")
    public ResponseEntity<TransfertResponse> annuler(
            @PathVariable("id") Long id) {

        return ResponseEntity.ok(
                transfertService.annulerTransfert(id)
        );
    }

    @GetMapping("code/{codeRetrait}")
    public ResponseEntity<TransfertResponse> getByCodeRetrait(
            @PathVariable("codeRetrait") String codeRetrait) {

        return ResponseEntity.ok(
                transfertService.getByCodeRetrait(codeRetrait)
        );
    }

    @GetMapping("/mes-transferts")
    public ResponseEntity<List<TransfertResponse>> getMesTransferts(
            @RequestParam("clientId") Long clientId) {

        return ResponseEntity.ok(
                transfertService.getMesTransferts(clientId)
        );
    }

}
