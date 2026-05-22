package com.okanetransfer.shared.exception;

// 403 — tentative d'accès à une ressource non autorisée
// ex : manager qui tente d'accéder à une agence qui n'est pas la sienne
// ex : pièce d'identité qui n'appartient pas au client
// ex : mot de passe incorrect à la connexion
public class AccesRefuseException extends RuntimeException {

    public AccesRefuseException(String message) {
        super(message);
    }
}
