package com.okanetransfer.service.impl.aml;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.okanetransfer.entity.aml.JournalAudit;
import com.okanetransfer.entity.user.Utilisateur;
import com.okanetransfer.repository.aml.JournalAuditRepository;
import com.okanetransfer.service.facade.aml.IAuditService;

@Service
@Transactional
public class AuditServiceImpl implements IAuditService {

    private final JournalAuditRepository journalAuditRepository;

    public AuditServiceImpl(JournalAuditRepository journalAuditRepository) {
        this.journalAuditRepository = journalAuditRepository;
    }

    @Override
    public JournalAudit log(Utilisateur acteur, String action, String entite, Long idCible,
                           String detailAvant, String detailApres, String ipAdresse) {
        JournalAudit audit = new JournalAudit();
        audit.setActeur(acteur);
        audit.setAction(action);
        audit.setEntiteCible(entite);
        audit.setIdCible(idCible);
        audit.setDetailAvant(detailAvant);
        audit.setDetailApres(detailApres);
        audit.setIpAdresse(ipAdresse);
        
        return journalAuditRepository.save(audit);
    }

    @Override
    public JournalAudit log(Utilisateur acteur, String action, String entite, Long idCible) {
        return log(acteur, action, entite, idCible, null, null, null);
    }
}
