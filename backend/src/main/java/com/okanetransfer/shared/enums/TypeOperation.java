package com.okanetransfer.shared.enums;

public enum TypeOperation {
    ENVOI,        // agent a créé un transfert → caisse créditée du montant + frais
    RETRAIT,      // agent a payé un transfert → caisse débitée du montant remis
    OUVERTURE,    // ouverture de caisse en début de journée
    AJUSTEMENT,   // correction manuelle validée par le manager
    CLOTURE       // clôture de caisse en fin de journée
}
