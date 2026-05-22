package com.okanetransfer.service.facade.aml;

import com.okanetransfer.entity.aml.JournalAudit;
import com.okanetransfer.entity.user.Utilisateur;

public interface IAuditService {

    /**
     * Enregistre une action audit dans le journal
     * @param acteur l'utilisateur qui effectue l'action
     * @param action le type d'action (ex: "CREATION_TRANSFERT", "PAIEMENT")
     * @param entite le type d'entité concernée (ex: "Transfert", "DeclarationSoupcon")
     * @param idCible l'identifiant de l'entité cible
     * @param detailAvant l'état de l'entité avant modification (JSON ou null)
     * @param detailApres l'état de l'entité après modification (JSON ou null)
     * @param ipAdresse l'adresse IP de la requête (peut être null)
     * @return l'entrée du journal créée
     */
    JournalAudit log(Utilisateur acteur, String action, String entite, Long idCible,
                     String detailAvant, String detailApres, String ipAdresse);

    /**
     * Enregistre une action audit sans détails d'état
     * @param acteur l'utilisateur qui effectue l'action
     * @param action le type d'action
     * @param entite le type d'entité concernée
     * @param idCible l'identifiant de l'entité cible
     * @return l'entrée du journal créée
     */
    JournalAudit log(Utilisateur acteur, String action, String entite, Long idCible);
}
