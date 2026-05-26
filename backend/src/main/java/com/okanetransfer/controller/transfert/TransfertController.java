package com.okanetransfer.controller.transfert;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.okanetransfer.service.dto.transfert.request.CreateTransfertRequest;
import com.okanetransfer.service.dto.transfert.request.PaiementRequest;
import com.okanetransfer.service.dto.transfert.request.UpdateTransfertRequest;
import com.okanetransfer.service.dto.transfert.response.TransfertResponse;
import com.okanetransfer.service.dto.transfert.response.TransfertStatsResponse;
import com.okanetransfer.service.facade.transfert.ITransfertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
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
                                transfertService.getAllTransferts());
        }

        @GetMapping("/{id}")
        public ResponseEntity<TransfertResponse> getById(
                        @PathVariable("id") Long id) {

                return ResponseEntity.ok(
                                transfertService.getTransfertById(id));
        }

        @PostMapping
        public ResponseEntity<TransfertResponse> create(
                        @RequestBody CreateTransfertRequest request) {

                return ResponseEntity.ok(
                                transfertService.creerTransfert(request));
        }

        @PutMapping("/{id}")
        public ResponseEntity<TransfertResponse> update(
                        @PathVariable("id") Long id,
                        @RequestBody UpdateTransfertRequest request) {

                return ResponseEntity.ok(
                                transfertService.updateTransfert(id, request));
        }

        @PostMapping("/paiement")
        public ResponseEntity<TransfertResponse> payer(
                        @RequestBody PaiementRequest request) {

                return ResponseEntity.ok(
                                transfertService.payerTransfert(request));
        }

        @PutMapping("/{id}/annuler")
        public ResponseEntity<TransfertResponse> annuler(
                        @PathVariable("id") Long id) {

                return ResponseEntity.ok(
                                transfertService.annulerTransfert(id));
        }

        @GetMapping("code/{codeRetrait}")
        public ResponseEntity<TransfertResponse> getByCodeRetrait(
                        @PathVariable("codeRetrait") String codeRetrait) {

                return ResponseEntity.ok(
                                transfertService.getByCodeRetrait(codeRetrait));
        }

        @GetMapping("/mes-transferts")
        public ResponseEntity<List<TransfertResponse>> getMesTransferts(
                        @RequestParam("clientId") Long clientId) {

                return ResponseEntity.ok(
                                transfertService.getMesTransferts(clientId));
        }

        @GetMapping("/telephone/{telephone}")
        public ResponseEntity<TransfertResponse> getByTelephoneBeneficiaire(
                        @PathVariable("telephone") String telephone) {

                return ResponseEntity.ok(
                                transfertService.getByTelephoneBeneficiaire(telephone));
        }

        @GetMapping("/agence/{agenceId}")
        //@PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
        public ResponseEntity<List<TransfertResponse>> getByAgence(
                        @PathVariable Long agenceId,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {

                if (debut == null) {
                        debut = LocalDate.now();
                }
                if (fin == null) {
                        fin = LocalDate.now();
                }
                return ResponseEntity.ok(
                                transfertService.findByAgence(agenceId, debut, fin));
        }
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

    @GetMapping("/stats")
    public ResponseEntity<TransfertStatsResponse> getStats() {
        return ResponseEntity.ok(
                transfertService.getStats()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransfertResponse> getById(
            @PathVariable("id") Long id) {

        return ResponseEntity.ok(
                transfertService.getTransfertById(id)
        );
    }

    @GetMapping("/agent/{email}/commissions")
    public ResponseEntity<BigDecimal> commissionsAgent(
            @PathVariable String email,
            @RequestParam String dateDebut,
            @RequestParam String dateFin) {

        return ResponseEntity.ok(
                transfertService.commissionsAgent(
                        email,
                        LocalDate.parse(dateDebut),
                        LocalDate.parse(dateFin))
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
