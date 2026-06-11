package com.okanetransfer.shared.enums;

public enum TypeNotification {
    ALERTE, // in-app alert (bell) — AML declarations, OFAC matches, system events
    SMS,    // envoyé via gateway SMS (ex: Twilio) au numéro de téléphone
    EMAIL,  // envoyé via SMTP au mail de l'utilisateur
    PUSH    // notification push sur l'application mobile du client
}
