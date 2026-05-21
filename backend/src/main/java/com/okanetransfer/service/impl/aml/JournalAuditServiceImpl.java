package com.okanetransfer.service.impl.aml;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.okanetransfer.entity.aml.JournalAudit;
import com.okanetransfer.repository.aml.JournalAuditRepository;
import com.okanetransfer.service.facade.aml.IJournalAuditService;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class JournalAuditServiceImpl implements IJournalAuditService {

    private final JournalAuditRepository journalAuditRepository;

    public JournalAuditServiceImpl(JournalAuditRepository journalAuditRepository) {
        this.journalAuditRepository = journalAuditRepository;
    }

    @Override
    public JournalAudit create(JournalAudit journalAudit) {
        return journalAuditRepository.save(journalAudit);
    }

    @Override
    public JournalAudit update(Long id, JournalAudit journalAudit) {
        JournalAudit existing = journalAuditRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("JournalAudit introuvable pour l'id: " + id));
        existing.setActeur(journalAudit.getActeur());
        existing.setAction(journalAudit.getAction());
        existing.setEntiteCible(journalAudit.getEntiteCible());
        existing.setIdCible(journalAudit.getIdCible());
        existing.setDetailAvant(journalAudit.getDetailAvant());
        existing.setDetailApres(journalAudit.getDetailApres());
        existing.setIpAdresse(journalAudit.getIpAdresse());
        return journalAuditRepository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public JournalAudit getById(Long id) {
        return journalAuditRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("JournalAudit introuvable pour l'id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<JournalAudit> getAll() {
        return journalAuditRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        if (!journalAuditRepository.existsById(id)) {
            throw new EntityNotFoundException("JournalAudit introuvable pour l'id: " + id);
        }
        journalAuditRepository.deleteById(id);
    }
}
