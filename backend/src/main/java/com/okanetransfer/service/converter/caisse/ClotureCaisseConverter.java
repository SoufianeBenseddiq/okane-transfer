package com.okanetransfer.service.converter.caisse;


import com.okanetransfer.service.dto.caisse.request.ClotureCaisseRequest;
import com.okanetransfer.service.dto.caisse.response.ClotureCaisseResponse;
import com.okanetransfer.entity.caisse.ClotureCaisse;
import com.okanetransfer.entity.user.Agent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClotureCaisseConverter {

    // soldeTheorique et ecart sont calcules dans le service, pas ici
    public ClotureCaisse toEntity(ClotureCaisseRequest request) {
        ClotureCaisse cloture = new ClotureCaisse();
//        cloture.setAgent(agent);
        cloture.setDate(request.getDate());
        cloture.setSoldeSaisi(request.getSoldeSaisi());

        return cloture;
    }

    public ClotureCaisseResponse toResponse(ClotureCaisse cloture) {
        ClotureCaisseResponse response = new ClotureCaisseResponse();
        response.setId(cloture.getId());
        response.setAgentId(cloture.getAgent().getId());
        response.setAgentNom(
                cloture.getAgent().getPrenom() + " " + cloture.getAgent().getNom()
        );
        response.setDate(cloture.getDate());
        response.setSoldeTheorique(cloture.getSoldeTheorique());
        response.setSoldeSaisi(cloture.getSoldeSaisi());
        response.setEcart(cloture.getEcart());
        response.setEcartSignale(cloture.getEcartSignale());
        response.setClotureeLe(cloture.getClotureeLe());
        return response;
    }

    public List<ClotureCaisse> toEntities(List<ClotureCaisseRequest> requests) {
        return requests.stream()
                .map(this::toEntity)
                .toList();
    }

    public List<ClotureCaisseResponse> toResponses(List<ClotureCaisse> clotureCaisses) {
        return clotureCaisses.stream()
                .map(this::toResponse)
                .toList();
    }


}