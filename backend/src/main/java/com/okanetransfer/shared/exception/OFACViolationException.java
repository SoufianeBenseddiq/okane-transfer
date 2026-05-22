package com.okanetransfer.shared.exception;

// 403 — le nom de l'expéditeur ou du bénéficiaire est sur la liste de surveillance OFAC
// le transfert est bloqué automatiquement
// ex : "Ahmed Fictif" est dans la liste noire → transfert refusé
public class OFACViolationException extends RuntimeException {

    private final String nomSuspecte;

    public OFACViolationException(String nomSuspecte) {
        super(String.format(
            "Transfert bloqué : '%s' figure sur la liste de surveillance.",
            nomSuspecte
        ));
        this.nomSuspecte = nomSuspecte;
    }

    public String getNomSuspecte() { return nomSuspecte; }
}
