package com.okanetransfer.service.impl.devise;

import com.okanetransfer.service.converter.devise.DeviseConverter;
import com.okanetransfer.entity.devise.Devise;
import com.okanetransfer.entity.devise.HistoriqueTaux;
import com.okanetransfer.repository.devise.DeviseRepository;
import com.okanetransfer.repository.devise.HistoriqueTauxRepository;
import com.okanetransfer.service.facade.devise.IDeviseService;
import com.okanetransfer.service.dto.devise.request.DeviseRequest;
import com.okanetransfer.service.dto.devise.response.DeviseResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class DeviseServiceImpl implements IDeviseService {

    private final DeviseRepository deviseRepository;
    private final HistoriqueTauxRepository historiqueTauxRepository;
    private final DeviseConverter deviseConverter;

    public DeviseServiceImpl(DeviseRepository deviseRepository,
                             HistoriqueTauxRepository historiqueTauxRepository,
                             DeviseConverter deviseConverter) {
        this.deviseRepository = deviseRepository;
        this.historiqueTauxRepository = historiqueTauxRepository;
        this.deviseConverter = deviseConverter;
    }

    @Override
    public DeviseResponse creer(DeviseRequest request) {
        if (deviseRepository.existsByCode(request.getCode().toUpperCase())) {
            throw new IllegalArgumentException("Une devise avec le code " + request.getCode() + " existe déjà.");
        }
        Devise devise = deviseConverter.toEntity(request);
        return deviseConverter.toResponse(deviseRepository.save(devise));
    }

    @Override
    @Transactional(readOnly = true)
    public DeviseResponse getById(Long id) {
        Devise devise = deviseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Devise introuvable avec l'id : " + id));
        return deviseConverter.toResponse(devise);
    }

    @Override
    @Transactional(readOnly = true)
    public DeviseResponse getByCode(String code) {
        Devise devise = deviseRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Devise introuvable avec le code : " + code));
        return deviseConverter.toResponse(devise);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeviseResponse> getAll() {
        return deviseConverter.toResponseList(deviseRepository.findAll());
    }

    @Override
    public DeviseResponse modifier(Long id, DeviseRequest request) {
        Devise devise = deviseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Devise introuvable avec l'id : " + id));

        // Sauvegarder l'ancien taux dans l'historique avant modification
        if (!devise.getTauxVersEuro().equals(request.getTauxVersEuro())) {
            HistoriqueTaux historique = new HistoriqueTaux();
            historique.setDevise(devise);
            historique.setAncienTaux(devise.getTauxVersEuro());
            historique.setNouveauTaux(request.getTauxVersEuro());
            historique.setDate(LocalDateTime.now());
            historique.setSource(request.getSourceTaux());
            historiqueTauxRepository.save(historique);
        }

        devise.setNom(request.getNom());
        devise.setSymbole(request.getSymbole());
        devise.setTauxVersEuro(request.getTauxVersEuro());
        devise.setSourceTaux(request.getSourceTaux());
        devise.setDerniereMaj(LocalDateTime.now());

        return deviseConverter.toResponse(deviseRepository.save(devise));
    }

    @Override
    public void activer(Long id) {
        Devise devise = deviseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Devise introuvable avec l'id : " + id));
        devise.setActive(true);
        deviseRepository.save(devise);
    }

    @Override
    public void desactiver(Long id) {
        Devise devise = deviseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Devise introuvable avec l'id : " + id));
        devise.setActive(false);
        deviseRepository.save(devise);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTauxConversion(String codeSource, String codeDestination) {
        Devise source = deviseRepository.findByCode(codeSource.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Devise source introuvable : " + codeSource));
        Devise destination = deviseRepository.findByCode(codeDestination.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Devise destination introuvable : " + codeDestination));

        // Conversion via Euro comme devise pivot
        // taux = tauxVersEuro(source) / tauxVersEuro(destination)
        return source.getTauxVersEuro()
                .divide(destination.getTauxVersEuro(), 6, RoundingMode.HALF_UP);
    }
}