package com.okanetransfer.service.converter.agence;

import com.okanetransfer.service.dto.agence.request.AgenceRequest;
import com.okanetransfer.service.dto.agence.response.AgenceResponse;
import com.okanetransfer.entity.agence.Agence;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class AgenceConverter {

    public Agence toEntity(AgenceRequest request) {
        Agence agence = new Agence();
        agence.setNom(request.getNom());
        agence.setAdresse(request.getAdresse());
        agence.setPays(request.getPays());
        agence.setPlafondJournalier(request.getPlafondJournalier());
        agence.setEstCentrale(Boolean.TRUE.equals(request.getEstCentrale()));
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
        response.setEstCentrale(agence.getEstCentrale());

        if (agence.getAgenceCentrale() != null) {
            response.setAgenceCentraleId(agence.getAgenceCentrale().getId());
            response.setAgenceCentraleNom(agence.getAgenceCentrale().getNom());
        }

        if (agence.getResponsable() != null) {
            response.setResponsableNom(
                    agence.getResponsable().getPrenom() + " " + agence.getResponsable().getNom()
            );
            response.setResponsableId(agence.getResponsable().getId());
        }

        BigDecimal plafond = agence.getPlafondJournalier();
        BigDecimal traite  = agence.getMontantTraiteAujourdhui();
        if (plafond != null && plafond.compareTo(BigDecimal.ZERO) > 0 && traite != null) {
            int pct = traite.multiply(BigDecimal.valueOf(100))
                           .divide(plafond, 0, RoundingMode.HALF_UP)
                           .intValue();
            response.setPourcentagePlafond(Math.min(pct, 100));
        } else {
            response.setPourcentagePlafond(0);
        }

        return response;
    }

    public List<Agence> toEntities(List<AgenceRequest> agenceRequests) {
        return agenceRequests.stream().map(this::toEntity).toList();
    }

    public List<AgenceResponse> toResponses(List<Agence> agences) {
        return agences.stream().map(this::toResponse).toList();
    }

    public void updateEntity(Agence agence, AgenceRequest request) {
        agence.setNom(request.getNom());
        agence.setAdresse(request.getAdresse());
        agence.setPays(request.getPays());
        agence.setPlafondJournalier(request.getPlafondJournalier());
        agence.setEstCentrale(Boolean.TRUE.equals(request.getEstCentrale()));
    }
}
