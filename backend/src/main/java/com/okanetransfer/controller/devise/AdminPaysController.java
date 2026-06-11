package com.okanetransfer.controller.devise;

import com.okanetransfer.service.facade.devise.IPaysService;
import com.okanetransfer.service.dto.devise.request.PaysRequest;
import com.okanetransfer.service.dto.devise.response.PaysResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/pays")
public class AdminPaysController {

    private final IPaysService paysService;

    public AdminPaysController(IPaysService paysService) {
        this.paysService = paysService;
    }

    @GetMapping
    public ResponseEntity<List<PaysResponse>> getAll() {
        return ResponseEntity.ok(paysService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaysResponse> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(paysService.getById(id));
    }

    @PostMapping
    public ResponseEntity<PaysResponse> creer(@Valid @RequestBody PaysRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paysService.creer(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaysResponse> modifier(
            @PathVariable("id") Long id,
            @Valid @RequestBody PaysRequest request) {
        return ResponseEntity.ok(paysService.modifier(id, request));
    }
}
