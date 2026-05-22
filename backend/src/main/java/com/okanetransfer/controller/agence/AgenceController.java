package com.okanetransfer.controller.agence;


import com.okanetransfer.service.dto.agence.request.AgenceRequest;
import com.okanetransfer.service.dto.agence.response.AgenceResponse;
import com.okanetransfer.service.facade.agence.AgenceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agences/")
public class AgenceController {

    private final AgenceService agenceService;

    public AgenceController(AgenceService agenceService) {
        this.agenceService = agenceService;
    }

    // ADMIN et MANAGER peuvent chercher une agence
    @GetMapping("nom/{nom}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    public ResponseEntity<AgenceResponse> findByNom(@PathVariable String nom) {
        return ResponseEntity.ok(agenceService.findByNom(nom));
    }

    @GetMapping("adresse/{adresse}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    public ResponseEntity<AgenceResponse> findByAdresse(@PathVariable String adresse) {
        return ResponseEntity.ok(agenceService.findByAdresse(adresse));
    }

    @GetMapping("responsable/{email}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    public ResponseEntity<AgenceResponse> findByResponsable(@PathVariable String email) {
        return ResponseEntity.ok(agenceService.findByResponsableEmail(email));
    }

    // ADMIN, MANAGER et AGENT peuvent voir les agences actives
    @GetMapping("actives")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_AGENT')")
    public ResponseEntity<List<AgenceResponse>> findByActiveTrue() {
        return ResponseEntity.ok(agenceService.findByActiveTrue());
    }

    // ADMIN uniquement pour créer, modifier, supprimer
    @PostMapping("add-one")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AgenceResponse> save(@RequestBody @Valid AgenceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(agenceService.save(request));
    }

    @PutMapping("id/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AgenceResponse> update(@PathVariable Long id,
                                                 @RequestBody @Valid AgenceRequest request) {
        return ResponseEntity.ok(agenceService.update(id, request));
    }

    @DeleteMapping("id/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        agenceService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("all")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    public ResponseEntity<List<AgenceResponse>> findAll() {
        return ResponseEntity.ok(agenceService.findAll());
    }

}