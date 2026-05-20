package com.okanetransfer.shared.enums;

public enum StatutTransfert {
    EN_ATTENTE,   // transfert créé, en attente de retrait par le bénéficiaire
    PAYE,         // bénéficiaire a retiré les fonds au guichet
    ANNULE,       // annulé par l'agent ou le manager avant retrait
    EXPIRE,       // délai de retrait dépassé (ex: 30 jours sans retrait)
    BLOQUE        // suspendu suite à un contrôle AML/KYC
}
