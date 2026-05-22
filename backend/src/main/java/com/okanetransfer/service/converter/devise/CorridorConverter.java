package com.okanetransfer.service.converter.devise;

import com.okanetransfer.entity.devise.Corridor;
import com.okanetransfer.service.dto.devise.response.CorridorResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CorridorConverter {

    // Entity → DTO
    public CorridorResponse toResponse(Corridor corridor) {
        if (corridor == null) return null;
        CorridorResponse r = new CorridorResponse();
        r.setId(corridor.getId());
        r.setDeviseSource(corridor.getDeviseSource().getCode());
        r.setDeviseDestination(corridor.getDeviseDestination().getCode());
        r.setActif(corridor.getActif());
        r.setDateActivation(corridor.getDateActivation());
        return r;
    }

    // Liste d'entités → Liste de DTOs
    public List<CorridorResponse> toResponseList(List<Corridor> list) {
        return list.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}