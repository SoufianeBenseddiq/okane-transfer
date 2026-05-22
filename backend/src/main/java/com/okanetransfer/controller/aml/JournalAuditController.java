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

import com.okanetransfer.entity.aml.JournalAudit;
import com.okanetransfer.service.converter.aml.AmlConverter;
import com.okanetransfer.service.dto.aml.response.AuditResponse;
import com.okanetransfer.service.facade.aml.IJournalAuditService;

@RestController
@RequestMapping("/api/aml/audit")
public class JournalAuditController {

    private final IJournalAuditService journalAuditService;
    private final AmlConverter amlConverter;

    public JournalAuditController(IJournalAuditService journalAuditService,
                                 AmlConverter amlConverter) {
        this.journalAuditService = journalAuditService;
        this.amlConverter = amlConverter;
    }

    @GetMapping
    public List<AuditResponse> getAll() {
        return journalAuditService.getAll().stream()
                .map(amlConverter::toAuditResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public AuditResponse getById(@PathVariable Long id) {
        return amlConverter.toAuditResponse(journalAuditService.getById(id));
    }

    @PostMapping
    public AuditResponse create(@RequestBody JournalAudit journalAudit) {
        return amlConverter.toAuditResponse(journalAuditService.create(journalAudit));
    }

    @PutMapping("/{id}")
    public AuditResponse update(@PathVariable Long id, @RequestBody JournalAudit journalAudit) {
        return amlConverter.toAuditResponse(journalAuditService.update(id, journalAudit));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        journalAuditService.delete(id);
    }
}
