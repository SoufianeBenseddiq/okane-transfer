package com.okanetransfer.shared.exception;

// 400 — le corridor entre les deux devises/pays est désactivé ou inexistant
// ex : corridor MAD → XOF désactivé par l'admin
// ex : corridor MAD → JPY n'existe pas dans le système
public class CorridorInactifException extends RuntimeException {

    public CorridorInactifException(String deviseSource, String deviseDestination) {
        super(String.format(
            "Le corridor %s → %s est inactif ou inexistant.",
            deviseSource, deviseDestination
        ));
    }
}
