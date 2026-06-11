package com.okanetransfer.service.impl.devise;

import com.okanetransfer.entity.devise.Devise;
import com.okanetransfer.entity.devise.Pays;
import com.okanetransfer.repository.devise.DeviseRepository;
import com.okanetransfer.repository.devise.PaysRepository;
import com.okanetransfer.service.converter.devise.PaysConverter;
import com.okanetransfer.service.dto.devise.request.PaysRequest;
import com.okanetransfer.service.dto.devise.response.PaysResponse;
import com.okanetransfer.service.facade.devise.IPaysService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PaysServiceImpl implements IPaysService {

    private final PaysRepository   paysRepository;
    private final DeviseRepository deviseRepository;
    private final PaysConverter    paysConverter;

    public PaysServiceImpl(PaysRepository paysRepository,
                           DeviseRepository deviseRepository,
                           PaysConverter paysConverter) {
        this.paysRepository   = paysRepository;
        this.deviseRepository = deviseRepository;
        this.paysConverter    = paysConverter;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaysResponse> getAll() {
        return paysConverter.toResponseList(paysRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public PaysResponse getById(Long id) {
        return paysConverter.toResponse(
            paysRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pays introuvable : " + id))
        );
    }

    @Override
    public PaysResponse creer(PaysRequest request) {
        if (paysRepository.existsByNom(request.getNom())) {
            throw new IllegalArgumentException("Un pays avec ce nom existe déjà : " + request.getNom());
        }
        Devise devise = deviseRepository.findById(request.getDeviseId())
                .orElseThrow(() -> new EntityNotFoundException("Devise introuvable : " + request.getDeviseId()));

        Pays pays = new Pays();
        applyRequest(pays, request, devise);
        return paysConverter.toResponse(paysRepository.save(pays));
    }

    @Override
    public PaysResponse modifier(Long id, PaysRequest request) {
        Pays pays = paysRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pays introuvable : " + id));
        Devise devise = deviseRepository.findById(request.getDeviseId())
                .orElseThrow(() -> new EntityNotFoundException("Devise introuvable : " + request.getDeviseId()));

        applyRequest(pays, request, devise);
        return paysConverter.toResponse(paysRepository.save(pays));
    }

    private void applyRequest(Pays pays, PaysRequest request, Devise devise) {
        pays.setNom(request.getNom());
        pays.setCodeIso(request.getCodeIso().toUpperCase());
        pays.setIndicatifTel(request.getIndicatifTel());
        pays.setFormatTel(request.getFormatTel());
        pays.setLongueurTel(request.getLongueurTel());
        pays.setDevise(devise);
    }
}
