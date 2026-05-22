package com.okanetransfer.service.facade.devise;

import com.okanetransfer.service.dto.devise.request.GrilleTarifaireRequest;
import com.okanetransfer.service.dto.devise.response.FraisResult;

import java.math.BigDecimal;

public interface IFraisService {

    // Calcule les frais selon le montant et le corridor (utilisé par TransfertService)
    FraisResult calculerFrais(BigDecimal montant, Long corridorId);

    // Récupère le corridor actif entre deux devises (utilisé par TransfertService)
    com.okanetransfer.entity.devise.Corridor getCorridorActif(String codeSource, String codeDestination);
    void creerGrille(GrilleTarifaireRequest request);}