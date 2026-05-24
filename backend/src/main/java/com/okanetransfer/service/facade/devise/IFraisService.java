package com.okanetransfer.service.facade.devise;

import com.okanetransfer.service.dto.devise.request.GrilleTarifaireRequest;
import com.okanetransfer.service.dto.devise.response.FraisResult;
import com.okanetransfer.service.dto.devise.response.GrilleTarifaireResponse;

import java.math.BigDecimal;
import java.util.List;

public interface IFraisService {

    FraisResult calculerFrais(BigDecimal montant, Long corridorId);

    com.okanetransfer.entity.devise.Corridor getCorridorActif(String codeSource, String codeDestination);

    void creerGrille(GrilleTarifaireRequest request);

    List<GrilleTarifaireResponse> getGrillesByCorridor(Long corridorId);

    GrilleTarifaireResponse updateGrille(Long id, GrilleTarifaireRequest request);

    void deleteGrille(Long id);
}
