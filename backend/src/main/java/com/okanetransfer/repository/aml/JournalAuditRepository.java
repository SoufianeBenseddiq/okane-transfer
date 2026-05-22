package com.okanetransfer.repository.aml;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.okanetransfer.entity.aml.JournalAudit;

@Repository
public interface JournalAuditRepository extends JpaRepository<JournalAudit, Long> {
}
