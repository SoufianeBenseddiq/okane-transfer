package com.okanetransfer.controller.mobilemoney;

import com.okanetransfer.service.dto.mobilemoney.request.CreateTransfertMobileMoneyRequest;
import com.okanetransfer.service.dto.mobilemoney.request.UpdateTransfertMobileMoneyRequest;
import com.okanetransfer.service.dto.mobilemoney.response.TransfertMobileMoneyResponse;
import com.okanetransfer.service.facade.mobilemoney.ITransfertMobileMoneyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transferts-mobile-money")
public class TransfertMobileMoneyController {

    private final ITransfertMobileMoneyService transfertMobileMoneyService;

    public TransfertMobileMoneyController(ITransfertMobileMoneyService transfertMobileMoneyService) {
        this.transfertMobileMoneyService = transfertMobileMoneyService;
    }

    @GetMapping
    public ResponseEntity<List<TransfertMobileMoneyResponse>> getAll() {
        return ResponseEntity.ok(transfertMobileMoneyService.getAllTransfertsMobileMoney());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransfertMobileMoneyResponse> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(transfertMobileMoneyService.getTransfertMobileMoneyById(id));
    }

    @PostMapping
    public ResponseEntity<TransfertMobileMoneyResponse> create(@RequestBody CreateTransfertMobileMoneyRequest request) {
        return ResponseEntity.ok(transfertMobileMoneyService.createTransfertMobileMoney(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransfertMobileMoneyResponse> update(
            @PathVariable("id") Long id,
            @RequestBody UpdateTransfertMobileMoneyRequest request) {
        return ResponseEntity.ok(transfertMobileMoneyService.updateTransfertMobileMoney(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        transfertMobileMoneyService.deleteTransfertMobileMoney(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/transfert/{transfertId}")
    public ResponseEntity<TransfertMobileMoneyResponse> getByTransfertId(@PathVariable("transfertId") Long transfertId) {
        return ResponseEntity.ok(transfertMobileMoneyService.getByTransfertId(transfertId));
    }

    @GetMapping("/operateur/{operateur}")
    public ResponseEntity<List<TransfertMobileMoneyResponse>> getByOperateur(@PathVariable("operateur") String operateur) {
        return ResponseEntity.ok(transfertMobileMoneyService.getByOperateur(operateur));
    }

    @GetMapping("/statut/{statutMobile}")
    public ResponseEntity<List<TransfertMobileMoneyResponse>> getByStatutMobile(@PathVariable("statutMobile") String statutMobile) {
        return ResponseEntity.ok(transfertMobileMoneyService.getByStatutMobile(statutMobile));
    }

    @GetMapping("/reference/{referenceOperateur}")
    public ResponseEntity<TransfertMobileMoneyResponse> getByReferenceOperateur(@PathVariable("referenceOperateur") String referenceOperateur) {
        return ResponseEntity.ok(transfertMobileMoneyService.getByReferenceOperateur(referenceOperateur));
    }
}
