package com.okanetransfer.controller.aml;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.okanetransfer.entity.aml.RegleAML;
import com.okanetransfer.service.facade.aml.IRegleAMLService;

@RestController
@RequestMapping("/api/aml/regles")
public class RegleAMLController {

    private final IRegleAMLService regleAMLService;

    public RegleAMLController(IRegleAMLService regleAMLService) {
        this.regleAMLService = regleAMLService;
    }

    @GetMapping
    public List<RegleAML> getAll() {
        return regleAMLService.getAll();
    }

    @GetMapping("/{id}")
    public RegleAML getById(@PathVariable Long id) {
        return regleAMLService.getById(id);
    }

    @PostMapping
    public RegleAML create(@RequestBody RegleAML regleAML) {
        return regleAMLService.create(regleAML);
    }

    @PutMapping("/{id}")
    public RegleAML update(@PathVariable Long id, @RequestBody RegleAML regleAML) {
        return regleAMLService.update(id, regleAML);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        regleAMLService.delete(id);
    }
}
