package com.okanetransfer.shared.exception;

// 400 — le code de retrait est invalide pour l'une des raisons suivantes :
// - code introuvable en base
// - transfert déjà payé (statut PAYE)
// - transfert annulé (statut ANNULE)
// - transfert expiré (statut EXPIRE)
// - transfert bloqué (statut BLOQUE)
public class CodeRetraitInvalideException extends RuntimeException {

    private final String code;
    private final String raison;

    public CodeRetraitInvalideException(String code, String raison) {
        super(String.format(
            "Code de retrait invalide '%s' : %s",
            code, raison
        ));
        this.code   = code;
        this.raison = raison;
    }

    // constructeur court pour "code introuvable"
    public CodeRetraitInvalideException(String code) {
        this(code, "code introuvable ou invalide");
    }

    public String getCode() { return code; }
    public String getRaison() { return raison; }
}
