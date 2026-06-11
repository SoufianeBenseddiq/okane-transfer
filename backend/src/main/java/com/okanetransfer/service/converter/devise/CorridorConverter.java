package com.okanetransfer.service.converter.devise;

import com.okanetransfer.entity.devise.Corridor;
import com.okanetransfer.entity.devise.Devise;
import com.okanetransfer.entity.devise.Pays;
import com.okanetransfer.service.dto.devise.response.CorridorResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CorridorConverter {

    private final PaysConverter paysConverter;

    public CorridorConverter(PaysConverter paysConverter) {
        this.paysConverter = paysConverter;
    }

    public CorridorResponse toResponse(Corridor corridor) {
        if (corridor == null) return null;
        CorridorResponse r = new CorridorResponse();
        r.setId(corridor.getId());
        r.setActif(corridor.getActif());
        r.setDateActivation(corridor.getDateActivation());
        r.setPaysSource(paysConverter.toResponse(corridor.getPaysSource()));
        r.setPaysDestination(paysConverter.toResponse(corridor.getPaysDestination()));
        // Devise derived from pays — kept in response for frontend compatibility
        r.setDeviseSource(deviseCode(corridor.getPaysSource()));
        r.setDeviseDestination(deviseCode(corridor.getPaysDestination()));
        return r;
    }

    public List<CorridorResponse> toResponseList(List<Corridor> list) {
        return list.stream().map(this::toResponse).collect(Collectors.toList());
    }

    private String deviseCode(Pays pays) {
        if (pays == null) return null;
        Devise d = pays.getDevise();
        return d != null ? d.getCode() : null;
    }
}
