package com.okanetransfer.shared.util;


import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DateUtil {

    // vérifie si un transfert est expiré
    // ex: isExpire(creeLe, 30) → true si créé il y a plus de 30 jours
    public boolean isExpire(LocalDateTime creeLe, int joursMax) {
        return creeLe.isBefore(
                LocalDateTime.now().minusDays(joursMax)
        );
    }

    // retourne le début de la journée à 00:00:00
    // ex: debutJournee(2026-05-17) → 2026-05-17T00:00:00
    // utilisé dans les requêtes de rapport journalier
    public LocalDateTime debutJournee(LocalDate date) {
        return date.atStartOfDay();
    }

    // retourne la fin de la journée à 23:59:59
    // ex: finJournee(2026-05-17) → 2026-05-17T23:59:59
    // utilisé dans les requêtes de rapport journalier
    public LocalDateTime finJournee(LocalDate date) {
        return date.atTime(23, 59, 59);
    }

    // retourne le début du mois
    // ex: debutMois(5, 2026) → 2026-05-01T00:00:00
    // utilisé dans les rapports mensuels
    public LocalDateTime debutMois(int mois, int annee) {
        return LocalDate.of(annee, mois, 1).atStartOfDay();
    }

    // retourne la fin du mois (dernier jour à 23:59:59)
    // ex: finMois(5, 2026) → 2026-05-31T23:59:59
    public LocalDateTime finMois(int mois, int annee) {
        LocalDate premierJour = LocalDate.of(annee, mois, 1);
        LocalDate dernierJour = premierJour.withDayOfMonth(
                premierJour.lengthOfMonth()
        );
        return dernierJour.atTime(23, 59, 59);
    }

    // vérifie si une date est aujourd'hui
    // utilisé pour filtrer les opérations de caisse du jour
    public boolean isAujourdhui(LocalDateTime dateHeure) {
        return dateHeure.toLocalDate().equals(LocalDate.now());
    }
}