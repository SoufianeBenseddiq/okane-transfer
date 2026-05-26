package com.okanetransfer.service.converter.transfert;


import com.okanetransfer.entity.transfert.Transfert;
import com.okanetransfer.entity.user.Client;
import com.okanetransfer.service.dto.transfert.response.TransfertResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TransfertConverter {

    private TransfertConverter() {
    }

//    public static TransfertResponse toResponse(Transfert t) {
//
//        TransfertResponse r = new TransfertResponse();
//
//        r.setCodeRetrait(t.getCodeRetrait());
//        r.setNumeroReference(t.getNumeroReference());
//        r.setMontantEnvoye(t.getMontantEnvoye());
//        r.setMontantRecu(t.getMontantRecu());
//        r.setFrais(t.getFrais());
//        r.setStatut(t.getStatut().name());
//        r.setCreeLe(t.getCreeLe());
//
//        return r;
//    }

    public static TransfertResponse toResponse(Transfert t) {

        TransfertResponse r = new TransfertResponse();

        // ── Identifiants ──────────────────────────────────────────────────────
        r.setCodeRetrait(t.getCodeRetrait());
        r.setNumeroReference(t.getNumeroReference());

        // ── Montants ──────────────────────────────────────────────────────────
        r.setMontantEnvoye(t.getMontantEnvoye());
        r.setMontantRecu(t.getMontantRecu());
        r.setFrais(t.getFrais());
        r.setPartAgence(t.getGrilleTarifaire() != null ? t.getGrilleTarifaire().getPartAgence() : java.math.BigDecimal.ZERO);

        // ── Statut & Dates ────────────────────────────────────────────────────
        r.setStatut(t.getStatut().name());
        r.setCreeLe(t.getCreeLe());
        r.setExpireLe(t.getCreeLe() != null ? t.getCreeLe().plusDays(30) : null);

        // ── Bénéficiaire ──────────────────────────────────────────────────────
        if (t.getBeneficiaire() != null) {
            r.setNomBeneficiaire(
                    t.getBeneficiaire().getNom() + " " + t.getBeneficiaire().getPrenom()
            );
            r.setPaysBeneficiaire(t.getBeneficiaire().getPays());
            r.setVilleBeneficiaire(t.getBeneficiaire().getPays()); // pas de ville dans l'entité
            r.setTelephoneBeneficiaire(t.getBeneficiaire().getTelephone());
        }

        // ── Expéditeur ────────────────────────────────────────────────────────
        // nom/prénom viennent de Expediteur → Client (→ Utilisateur si héritage)
        if (t.getExpediteur() != null && t.getExpediteur().getClient() != null) {
            Client client = t.getExpediteur().getClient();
            r.setNomExpediteur(client.getNom() + " " + client.getPrenom());
            r.setPaysExpediteur(client.getPays());
            r.setVilleExpediteur(client.getPays());
            r.setTelephoneExpediteur(client.getTelephone());
        }

        // ── Agence d'envoi ────────────────────────────────────────────────────
        if (t.getAgenceEnvoi() != null) {
            r.setAgenceEnvoi(t.getAgenceEnvoi().getNom());
        }

        // ── Corridor → devise de réception ────────────────────────────────────
        if (t.getCorridor() != null && t.getCorridor().getDeviseDestination() != null) {
            r.setDeviseReception(t.getCorridor().getDeviseDestination().getCode()); // ex: "XOF"

            // Taux MAD → devise destination calculé via tauxVersEuro des deux devises
            // taux = tauxVersEuro(source) / tauxVersEuro(destination)
            BigDecimal tauxSource = t.getCorridor().getDeviseSource() != null
                    ? t.getCorridor().getDeviseSource().getTauxVersEuro()
                    : null;
            BigDecimal tauxDest = t.getCorridor().getDeviseDestination().getTauxVersEuro();

            if (tauxSource != null && tauxDest != null
                    && tauxDest.compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal taux = tauxSource.divide(tauxDest, 4, RoundingMode.HALF_UP);
                r.setTauxChange(taux);
            }
        }

        return r;
    }
}