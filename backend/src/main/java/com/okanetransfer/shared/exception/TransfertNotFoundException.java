package com.okanetransfer.shared.exception;

// 404 — ressource introuvable
// nommée "TransfertNotFoundException" par convention du projet
// mais utilisée pour toute entité introuvable :
// ex : transfert introuvable par code ou référence
// ex : utilisateur introuvable par id ou email
// ex : agence introuvable par id
public class TransfertNotFoundException extends RuntimeException {

    public TransfertNotFoundException(String message) {
        super(message);
    }
}
