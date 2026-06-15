package com.okanetransfer.service.facade.devise;

import com.okanetransfer.service.dto.devise.response.DeviseResponse;

import java.util.List;

public interface ITauxChangeService {

    /**
     * Récupère les derniers taux EUR → devise depuis l'API externe et met à
     * jour Devise.tauxVersEuro (+ historique) pour chaque devise connue dont
     * le code est couvert par l'API.
     */
    List<DeviseResponse> mettreAJourTauxDepuisApi();
}
