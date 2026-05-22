package com.okanetransfer.Controller.transfert;

import com.okanetransfer.service.dto.transfert.request.CreateExpediteurRequest;
import com.okanetransfer.service.dto.transfert.request.UpdateExpediteurRequest;
import com.okanetransfer.service.dto.transfert.response.ExpediteurResponse;
import com.okanetransfer.service.facade.transfert.IExpediteurService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expediteurs")
public class ExpediteurController {

    private final IExpediteurService expediteurService;

    public ExpediteurController(IExpediteurService expediteurService) {
        this.expediteurService = expediteurService;
    }

    @GetMapping
    public ResponseEntity<List<ExpediteurResponse>> getAll() {
        return ResponseEntity.ok(
                expediteurService.getAllExpediteurs()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpediteurResponse> getById(
            @PathVariable("id") Long id) {

        return ResponseEntity.ok(
                expediteurService.getExpediteurById(id)
        );
    }

    @PostMapping
    public ResponseEntity<ExpediteurResponse> create(
            @Valid @RequestBody CreateExpediteurRequest request) {

        return ResponseEntity.ok(
                expediteurService.createExpediteur(request)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpediteurResponse> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateExpediteurRequest request) {

        return ResponseEntity.ok(
                expediteurService.updateExpediteur(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable("id") Long id) {

        expediteurService.deleteExpediteur(id);
        return ResponseEntity.noContent().build();
    }
}
