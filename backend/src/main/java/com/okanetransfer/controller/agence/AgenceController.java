package com.okanetransfer.controller.agence;

import com.okanetransfer.service.dto.agence.request.AgenceRequest;
import com.okanetransfer.service.dto.agence.response.AgenceResponse;
import com.okanetransfer.service.facade.agence.AgenceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/agences/")
public class AgenceController {

    private final AgenceService agenceService;

    public AgenceController(AgenceService agenceService) {
        this.agenceService = agenceService;
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    @GetMapping("id/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_AGENT')")
    public ResponseEntity<AgenceResponse> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(agenceService.findById(id));
    }

    @GetMapping("nom/{nom}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    public ResponseEntity<AgenceResponse> findByNom(@PathVariable("nom") String nom) {
        return ResponseEntity.ok(agenceService.findByNom(nom));
    }

    @GetMapping("adresse/{adresse}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    public ResponseEntity<AgenceResponse> findByAdresse(@PathVariable("adresse") String adresse) {
        return ResponseEntity.ok(agenceService.findByAdresse(adresse));
    }

    @GetMapping("responsable/{email}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    public ResponseEntity<AgenceResponse> findByResponsable(@PathVariable("email") String email) {
        return ResponseEntity.ok(agenceService.findByResponsableEmail(email));
    }

    @GetMapping("actives")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_AGENT')")
    public ResponseEntity<List<AgenceResponse>> findByActiveTrue() {
        return ResponseEntity.ok(agenceService.findByActiveTrue());
    }

    @GetMapping("all")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    public ResponseEntity<List<AgenceResponse>> findAll() {
        return ResponseEntity.ok(agenceService.findAll());
    }

    @GetMapping("centrales")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<AgenceResponse>> findCentrales() {
        return ResponseEntity.ok(agenceService.findCentrales());
    }

    @GetMapping("mon-agence")
    @PreAuthorize("hasRole('ROLE_AGENT') or hasRole('ROLE_MANAGER')")
    public ResponseEntity<AgenceResponse> getMonAgence(Principal principal) {
        String email = principal.getName();
        try {
            return ResponseEntity.ok(agenceService.findByAgentEmail(email));
        } catch (Exception e) {
            return ResponseEntity.ok(agenceService.findByResponsableEmail(email));
        }
    }

    // ── Write ─────────────────────────────────────────────────────────────────

    @PostMapping("add-one")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AgenceResponse> save(@RequestBody @Valid AgenceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agenceService.save(request));
    }

    @PutMapping("id/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AgenceResponse> update(@PathVariable("id") Long id,
                                                  @RequestBody @Valid AgenceRequest request) {
        return ResponseEntity.ok(agenceService.update(id, request));
    }

    @PutMapping("id/{id}/toggle-active")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AgenceResponse> toggleActive(@PathVariable("id") Long id) {
        return ResponseEntity.ok(agenceService.toggleActive(id));
    }

    @DeleteMapping("id/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long id) {
        agenceService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
