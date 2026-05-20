package com.okanetransfer.shared.enums;

public enum RoleUtilisateur {
    ROLE_ADMIN,     // administrateur central : configure tout, supervise tout
    ROLE_MANAGER,   // responsable d'agence : gère son équipe et valide les cas sensibles
    ROLE_AGENT,     // agent au guichet : crée et paie les transferts
    ROLE_CLIENT     // client self-service : consulte ses transferts en ligne
}
