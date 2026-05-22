package com.okanetransfer.shared.exception;

import java.math.BigDecimal;

// 400 — le montant du transfert dépasse le plafond journalier de l'agence
// ex : agence a traité 190 000 MAD sur 200 000 MAD de plafond
//      → un transfert de 15 000 MAD est refusé
public class PlafondDepasseException extends RuntimeException {

    private final BigDecimal montantDemande;
    private final BigDecimal montantDisponible;

    public PlafondDepasseException(String nomAgence,
                                   BigDecimal montantDemande,
                                   BigDecimal montantDisponible) {
        super(String.format(
            "Plafond journalier dépassé pour l'agence '%s'. " +
            "Demandé : %s MAD — Disponible : %s MAD",
            nomAgence, montantDemande, montantDisponible
        ));
        this.montantDemande    = montantDemande;
        this.montantDisponible = montantDisponible;
    }

    public BigDecimal getMontantDemande() { return montantDemande; }
    public BigDecimal getMontantDisponible() { return montantDisponible; }
}
