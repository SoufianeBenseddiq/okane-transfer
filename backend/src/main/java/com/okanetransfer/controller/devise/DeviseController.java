package com.okanetransfer.controller.devise;

import com.okanetransfer.service.facade.devise.IDeviseService;
import com.okanetransfer.service.dto.devise.request.DeviseRequest;
import com.okanetransfer.service.dto.devise.response.DeviseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/devises")
public class DeviseController {

    private final IDeviseService deviseService;

    public DeviseController(IDeviseService deviseService) {
        this.deviseService = deviseService;
    }

    @GetMapping
    public ResponseEntity<List<DeviseResponse>> getAll() {
        return ResponseEntity.ok(deviseService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviseResponse> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(deviseService.getById(id));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<DeviseResponse> getByCode(@PathVariable("code") String code) {
        return ResponseEntity.ok(deviseService.getByCode(code));
    }

    @PostMapping
    public ResponseEntity<DeviseResponse> creer(@RequestBody DeviseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(deviseService.creer(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviseResponse> modifier(
            @PathVariable("id") Long id,
            @RequestBody DeviseRequest request) {
        return ResponseEntity.ok(deviseService.modifier(id, request));
    }

    @PatchMapping("/{id}/activer")
    public ResponseEntity<Void> activer(@PathVariable("id") Long id) {
        deviseService.activer(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/desactiver")
    public ResponseEntity<Void> desactiver(@PathVariable("id") Long id) {
        deviseService.desactiver(id);
        return ResponseEntity.noContent().build();
    }

}