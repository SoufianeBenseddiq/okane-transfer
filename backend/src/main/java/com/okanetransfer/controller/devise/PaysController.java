package com.okanetransfer.controller.devise;

import com.okanetransfer.service.facade.devise.IPaysService;
import com.okanetransfer.service.dto.devise.response.PaysResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pays")
public class PaysController {

    private final IPaysService paysService;

    public PaysController(IPaysService paysService) {
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
}
