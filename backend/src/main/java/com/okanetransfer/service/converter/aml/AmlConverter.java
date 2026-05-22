package com.okanetransfer.service.converter.aml;

import com.okanetransfer.entity.aml.DeclarationSoupcon;
import com.okanetransfer.entity.aml.JournalAudit;
import com.okanetransfer.service.dto.aml.response.AuditResponse;
import com.okanetransfer.service.dto.aml.response.DeclarationResponse;
import org.springframework.stereotype.Component;

@Component
public class AmlConverter {

    public DeclarationResponse toDeclarationResponse(DeclarationSoupcon declaration) {
        if (declaration == null) {
            return null;
        }

        return new DeclarationResponse(
                declaration.getId(),
                declaration.getTransfert() != null ? declaration.getTransfert().getId() : null,
                declaration.getRegle() != null ? declaration.getRegle().getId() : null,
                declaration.getMotif(),
                declaration.getMontantTotal(),
                declaration.getGenereLe(),
                declaration.getTraitee()
        );
    }

    public AuditResponse toAuditResponse(JournalAudit journalAudit) {
        if (journalAudit == null) {
            return null;
        }

        return new AuditResponse(
                journalAudit.getId(),
                journalAudit.getActeur() != null ? journalAudit.getActeur().getId() : null,
                journalAudit.getAction(),
                journalAudit.getEntiteCible(),
                journalAudit.getIdCible(),
                journalAudit.getDetailAvant(),
                journalAudit.getDetailApres(),
                journalAudit.getDateHeure(),
                journalAudit.getIpAdresse()
        );
    }
}
