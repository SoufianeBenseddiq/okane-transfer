package com.okanetransfer.controller.caisse;


import com.okanetransfer.service.dto.caisse.response.CaisseOperationResponse;
import com.okanetransfer.service.facade.caisse.CaisseOperationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/caisse-operations/")
public class CaisseOperationController {

    private final CaisseOperationService caisseOperationService;

    public CaisseOperationController(CaisseOperationService caisseOperationService) {
        this.caisseOperationService = caisseOperationService;
    }

    // AGENT consulte ses propres opérations, MANAGER et ADMIN consultent celles de n'importe quel agent
    @GetMapping("agent/{email}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_AGENT')")
    public ResponseEntity<List<CaisseOperationResponse>> findByAgentEmail(
            @PathVariable String email) {
        return ResponseEntity.ok(caisseOperationService.findByAgentEmail(email));
    }

    // ADMIN uniquement peut supprimer une opération
    @DeleteMapping("id/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        caisseOperationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("all")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    public ResponseEntity<List<CaisseOperationResponse>> findAll() {
        return ResponseEntity.ok(caisseOperationService.findAll());
    }

    @PostMapping("ouvrir")
    @PreAuthorize("hasRole('ROLE_AGENT')")
    public ResponseEntity<CaisseOperationResponse> ouvrirCaisse(@RequestParam String agentEmail, @RequestParam BigDecimal montantInitial) {
        return ResponseEntity.ok(
                caisseOperationService.ouvrirCaisse(agentEmail, montantInitial)
        );
    }

    @GetMapping("agent/{email}/solde")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_AGENT')")
    public ResponseEntity<BigDecimal> consulterSolde(@PathVariable String email) {
        return ResponseEntity.ok(
                caisseOperationService.consulterSolde(email)
        );
    }

    @GetMapping("{email}/operations")
    @PreAuthorize("hasRole('ROLE_AGENT')")
    public ResponseEntity<List<CaisseOperationResponse>> historiqueOperations(@PathVariable String email) {
        return ResponseEntity.ok(
                caisseOperationService.historiqueOperations(email)
        );
    }

    @GetMapping("{agentEmail}/{debut}/{fin}/solde")
    public BigDecimal calculerSoldeTheorique(@PathVariable("agentEmail") String agentEmail, @PathVariable("debut") LocalDateTime debut,@PathVariable("fin")  LocalDateTime fin) {
        return caisseOperationService.calculerSoldeTheorique(agentEmail, debut, fin);
    }
}
