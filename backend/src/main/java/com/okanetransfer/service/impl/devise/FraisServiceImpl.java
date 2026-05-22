package com.okanetransfer.service.impl.devise;

import com.okanetransfer.service.converter.devise.CorridorConverter;
import com.okanetransfer.entity.devise.Corridor;
import com.okanetransfer.entity.devise.Devise;
import com.okanetransfer.entity.devise.GrilleTarifaire;
import com.okanetransfer.repository.devise.CorridorRepository;
import com.okanetransfer.repository.devise.DeviseRepository;
import com.okanetransfer.repository.devise.GrilleTarifaireRepository;
import com.okanetransfer.service.dto.devise.request.GrilleTarifaireRequest;
import com.okanetransfer.service.facade.devise.ICorridorService;
import com.okanetransfer.service.facade.devise.IFraisService;
import com.okanetransfer.service.dto.devise.request.CorridorRequest;
import com.okanetransfer.service.dto.devise.response.CorridorResponse;
import com.okanetransfer.service.dto.devise.response.FraisResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class FraisServiceImpl implements IFraisService, ICorridorService {

    private final CorridorRepository corridorRepository;
    private final DeviseRepository deviseRepository;
    private final GrilleTarifaireRepository grilleTarifaireRepository;
    private final CorridorConverter corridorConverter;

    public FraisServiceImpl(CorridorRepository corridorRepository,
                            DeviseRepository deviseRepository,
                            GrilleTarifaireRepository grilleTarifaireRepository,
                            CorridorConverter corridorConverter) {
        this.corridorRepository = corridorRepository;
        this.deviseRepository = deviseRepository;
        this.grilleTarifaireRepository = grilleTarifaireRepository;
        this.corridorConverter = corridorConverter;
    }

    // ── IFraisService ─────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public FraisResult calculerFrais(BigDecimal montant, Long corridorId) {
        GrilleTarifaire grille = grilleTarifaireRepository
                .findByCorridor_IdAndMontantMinLessThanEqualAndMontantMaxGreaterThanEqual(
                        corridorId, montant, montant)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Aucune grille tarifaire pour ce montant sur ce corridor."));

        // Frais = fraisFixe + (montant * fraisPourcentage / 100)
        BigDecimal fraisVariables = montant
                .multiply(grille.getFraisPourcentage())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal montantFrais = grille.getFraisFixe().add(fraisVariables);

        FraisResult result = new FraisResult();
        result.setMontantFrais(montantFrais);
        result.setPartAgence(grille.getPartAgence());
        result.setPartCentrale(grille.getPartCentrale());
        result.setMontantRecu(montant.subtract(montantFrais));
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Corridor getCorridorActif(String codeSource, String codeDestination) {
        return corridorRepository
                .findByDeviseSource_CodeAndDeviseDestination_CodeAndActifTrue(codeSource, codeDestination)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Aucun corridor actif entre " + codeSource + " et " + codeDestination));
    }

    // ── ICorridorService ──────────────────────────────────────────────────────

    @Override
    public CorridorResponse creer(CorridorRequest request) {
        Devise source = deviseRepository.findById(request.getDeviseSourceId())
                .orElseThrow(() -> new IllegalArgumentException("Devise source introuvable."));
        Devise destination = deviseRepository.findById(request.getDeviseDestinationId())
                .orElseThrow(() -> new IllegalArgumentException("Devise destination introuvable."));

        if (corridorRepository.existsByDeviseSource_IdAndDeviseDestination_Id(
                source.getId(), destination.getId())) {
            throw new IllegalArgumentException("Ce corridor existe déjà.");
        }

        Corridor corridor = new Corridor();
        corridor.setDeviseSource(source);
        corridor.setDeviseDestination(destination);
        corridor.setActif(true);
        corridor.setDateActivation(LocalDate.now());

        return corridorConverter.toResponse(corridorRepository.save(corridor));
    }

    @Override
    @Transactional(readOnly = true)
    public CorridorResponse getById(Long id) {
        Corridor corridor = corridorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Corridor introuvable avec l'id : " + id));
        return corridorConverter.toResponse(corridor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CorridorResponse> getAll() {
        return corridorConverter.toResponseList(corridorRepository.findAll());
    }

    @Override
    public void activer(Long id) {
        Corridor corridor = corridorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Corridor introuvable avec l'id : " + id));
        corridor.setActif(true);
        corridor.setDateActivation(LocalDate.now());
        corridorRepository.save(corridor);
    }

    @Override
    public void desactiver(Long id) {
        Corridor corridor = corridorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Corridor introuvable avec l'id : " + id));
        corridor.setActif(false);
        corridorRepository.save(corridor);
    }
    @Override
    public void creerGrille(GrilleTarifaireRequest request) {
        Corridor corridor = corridorRepository.findById(request.getCorridorId())
                .orElseThrow(() -> new IllegalArgumentException("Corridor introuvable."));
        GrilleTarifaire grille = new GrilleTarifaire();
        grille.setCorridor(corridor);
        grille.setMontantMin(request.getMontantMin());
        grille.setMontantMax(request.getMontantMax());
        grille.setFraisFixe(request.getFraisFixe());
        grille.setFraisPourcentage(request.getFraisPourcentage());
        grille.setPartAgence(request.getPartAgence());
        grille.setPartCentrale(request.getPartCentrale());
        grilleTarifaireRepository.save(grille);
    }
}