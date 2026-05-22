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

import com.okanetransfer.entity.aml.DeclarationSoupcon;
import com.okanetransfer.service.converter.aml.AmlConverter;
import com.okanetransfer.service.dto.aml.response.DeclarationResponse;
import com.okanetransfer.service.facade.aml.IDeclarationSoupconService;

@RestController
@RequestMapping("/api/aml/declarations")
public class DeclarationSoupconController {

    private final IDeclarationSoupconService declarationService;
    private final AmlConverter amlConverter;

    public DeclarationSoupconController(IDeclarationSoupconService declarationService,
                                       AmlConverter amlConverter) {
        this.declarationService = declarationService;
        this.amlConverter = amlConverter;
    }

    @GetMapping
    public List<DeclarationResponse> getAll() {
        return declarationService.getAll().stream()
                .map(amlConverter::toDeclarationResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public DeclarationResponse getById(@PathVariable Long id) {
        return amlConverter.toDeclarationResponse(declarationService.getById(id));
    }

    @PostMapping
    public DeclarationResponse create(@RequestBody DeclarationSoupcon declaration) {
        return amlConverter.toDeclarationResponse(declarationService.create(declaration));
    }

    @PutMapping("/{id}")
    public DeclarationResponse update(@PathVariable Long id, @RequestBody DeclarationSoupcon declaration) {
        return amlConverter.toDeclarationResponse(declarationService.update(id, declaration));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        declarationService.delete(id);
    }
}
