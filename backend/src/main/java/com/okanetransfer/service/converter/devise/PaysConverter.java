package com.okanetransfer.service.converter.devise;

import com.okanetransfer.entity.devise.Pays;
import com.okanetransfer.service.dto.devise.response.PaysResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PaysConverter {

    public PaysResponse toResponse(Pays pays) {
        if (pays == null) return null;
        PaysResponse r = new PaysResponse();
        r.setId(pays.getId());
        r.setNom(pays.getNom());
        r.setCodeIso(pays.getCodeIso());
        r.setIndicatifTel(pays.getIndicatifTel());
        r.setFormatTel(pays.getFormatTel());
        r.setLongueurTel(pays.getLongueurTel());
        if (pays.getDevise() != null) {
            r.setDeviseCode(pays.getDevise().getCode());
            r.setDeviseNom(pays.getDevise().getNom());
            r.setDeviseSymbole(pays.getDevise().getSymbole());
        }
        return r;
    }

    public List<PaysResponse> toResponseList(List<Pays> list) {
        return list.stream().map(this::toResponse).collect(Collectors.toList());
    }
}
