package com.okanetransfer.service.facade.aml;

import java.util.List;

import com.okanetransfer.entity.aml.JournalAudit;

public interface IJournalAuditService {

    JournalAudit create(JournalAudit journalAudit);

    JournalAudit update(Long id, JournalAudit journalAudit);

    JournalAudit getById(Long id);

    List<JournalAudit> getAll();

    void delete(Long id);
}
