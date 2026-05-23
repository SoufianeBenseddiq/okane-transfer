package com.okanetransfer.controller.caisse;


import com.okanetransfer.service.dto.caisse.request.ClotureCaisseRequest;
import com.okanetransfer.service.dto.caisse.response.ClotureCaisseResponse;
import com.okanetransfer.service.facade.caisse.ClotureCaisseService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/clotures-caisse/")
public class ClotureCaisseController {

    private final ClotureCaisseService clotureCaisseService;

    public ClotureCaisseController(ClotureCaisseService clotureCaisseService) {
        this.clotureCaisseService = clotureCaisseService;
    }

    // AGENT consulte sa propre clôture, MANAGER et ADMIN consultent n'importe laquelle
    @GetMapping("agent/{email}/date/{date}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_AGENT')")
    public ResponseEntity<ClotureCaisseResponse> findByAgentEmailAndDate(
            @PathVariable String email,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(clotureCaisseService.findByAgentEmailAndDate(email, date));
    }

    // MANAGER et ADMIN voient les écarts signalés
    @GetMapping("ecarts")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    public ResponseEntity<List<ClotureCaisseResponse>> findByEcartSignaleTrue() {
        return ResponseEntity.ok(clotureCaisseService.findByEcartSignaleTrue());
    }

    @GetMapping("agent/{email}/ecarts")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    public ResponseEntity<List<ClotureCaisseResponse>> findByAgentEmailAndEcartSignaleTrue(
            @PathVariable String email) {
        return ResponseEntity.ok(clotureCaisseService.findByAgentEmailAndEcartSignaleTrue(email));
    }

    // AGENT crée sa propre clôture
    @PostMapping("add-one")
    @PreAuthorize("hasRole('ROLE_AGENT')")
    public ResponseEntity<ClotureCaisseResponse> save(
            @RequestBody @Valid ClotureCaisseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clotureCaisseService.save(request));
    }

    // MANAGER peut corriger une clôture
    @PutMapping("update")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    public ResponseEntity<ClotureCaisseResponse> update(
            @RequestBody @Valid ClotureCaisseRequest request) {
        return ResponseEntity.ok(clotureCaisseService.update(request));
    }

    // ADMIN uniquement peut supprimer
    @DeleteMapping("id/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        clotureCaisseService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("all")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    public ResponseEntity<List<ClotureCaisseResponse>> findAll() {
        return ResponseEntity.ok(clotureCaisseService.findAll());
    }

    @GetMapping("/{email}/rapport")
    @PreAuthorize("hasRole('ROLE_AGENT')")
    public ResponseEntity<ClotureCaisseResponse> rapportCloture(@PathVariable String email, @RequestParam String date) {
        return ResponseEntity.ok(
                clotureCaisseService.rapportCloture(email, LocalDate.parse(date))
        );
    }

    @PostMapping("/{email}/cloturer")
    @PreAuthorize("hasRole('ROLE_AGENT')")
    public ResponseEntity<ClotureCaisseResponse> cloturerCaisse(@PathVariable String email, @RequestBody ClotureCaisseRequest request) {
        request.setAgentEmail(email);

        return ResponseEntity.ok(
                clotureCaisseService.cloturerCaisse(request)
        );
    }
}