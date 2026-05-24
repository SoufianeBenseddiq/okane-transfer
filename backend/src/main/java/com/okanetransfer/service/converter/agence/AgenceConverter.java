package com.okanetransfer.service.converter.agence;

import com.okanetransfer.service.dto.agence.request.AgenceRequest;
import com.okanetransfer.service.dto.agence.response.AgenceResponse;
import com.okanetransfer.entity.agence.Agence;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AgenceConverter {

    public Agence toEntity(AgenceRequest request) {
        Agence agence = new Agence();
        agence.setNom(request.getNom());
        agence.setAdresse(request.getAdresse());
        agence.setPays(request.getPays());
        agence.setPlafondJournalier(request.getPlafondJournalier());
        return agence;
    }

    public AgenceResponse toResponse(Agence agence) {
        AgenceResponse response = new AgenceResponse();
        response.setId(agence.getId());
        response.setNom(agence.getNom());
        response.setAdresse(agence.getAdresse());
        response.setPays(agence.getPays());
        response.setPlafondJournalier(agence.getPlafondJournalier());
        response.setMontantTraiteAujourdhui(agence.getMontantTraiteAujourdhui());
        response.setActive(agence.getActive());


        if (agence.getResponsable() != null) {
            response.setResponsableNom(
                    agence.getResponsable().getPrenom() + " " + agence.getResponsable().getNom()
            );
            response.setResponsableId(agence.getResponsable().getId());
        }

        return response;
    }

    public List<Agence> toEntities(List<AgenceRequest> agenceRequests) {
        return agenceRequests.stream()
                .map(this::toEntity)
                .toList();
    }

    public List<AgenceResponse> toResponses(List<Agence> agences) {
        return agences.stream()
                .map(this::toResponse)
                .toList();
    }

    public void updateEntity(Agence agence, AgenceRequest request) {
        agence.setNom(request.getNom());
        agence.setAdresse(request.getAdresse());
        agence.setPays(request.getPays());
        agence.setPlafondJournalier(request.getPlafondJournalier());
    }


}