package com.okanetransfer.service.converter.devise;

import com.okanetransfer.entity.devise.Devise;
import com.okanetransfer.service.dto.devise.request.DeviseRequest;
import com.okanetransfer.service.dto.devise.response.DeviseResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DeviseConverter {

    // Entity → DTO (envoi au frontend)
    public DeviseResponse toResponse(Devise devise) {
        if (devise == null) return null;
        DeviseResponse r = new DeviseResponse();
        r.setId(devise.getId());
        r.setCode(devise.getCode());
        r.setNom(devise.getNom());
        r.setSymbole(devise.getSymbole());
        r.setActive(devise.getActive());
        r.setTauxVersEuro(devise.getTauxVersEuro());
        r.setDerniereMaj(devise.getDerniereMaj());
        r.setSourceTaux(devise.getSourceTaux());
        return r;
    }

    // DTO → Entity (sauvegarde en base)
    public Devise toEntity(DeviseRequest request) {
        Devise devise = new Devise();
        devise.setCode(request.getCode().toUpperCase());
        devise.setNom(request.getNom());
        devise.setSymbole(request.getSymbole());
        devise.setTauxVersEuro(request.getTauxVersEuro());
        devise.setSourceTaux(request.getSourceTaux());
        devise.setActive(true);
        devise.setDerniereMaj(LocalDateTime.now());
        return devise;
    }

    // Liste d'entités → Liste de DTOs
    public List<DeviseResponse> toResponseList(List<Devise> list) {
        return list.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}