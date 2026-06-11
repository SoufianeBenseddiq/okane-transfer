package com.okanetransfer.service.impl.devise;

import com.okanetransfer.service.converter.devise.CorridorConverter;
import com.okanetransfer.entity.devise.Corridor;
import com.okanetransfer.entity.devise.GrilleTarifaire;
import com.okanetransfer.entity.devise.Pays;
import com.okanetransfer.repository.devise.CorridorRepository;
import com.okanetransfer.repository.devise.GrilleTarifaireRepository;
import com.okanetransfer.repository.devise.PaysRepository;
import com.okanetransfer.service.dto.devise.request.GrilleTarifaireRequest;
import com.okanetransfer.service.facade.devise.ICorridorService;
import com.okanetransfer.service.facade.devise.IFraisService;
import com.okanetransfer.service.dto.devise.request.CorridorRequest;
import com.okanetransfer.service.dto.devise.response.CorridorResponse;
import com.okanetransfer.service.dto.devise.response.FraisResult;
import com.okanetransfer.service.dto.devise.response.GrilleTarifaireResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FraisServiceImpl implements IFraisService, ICorridorService {

    private final CorridorRepository        corridorRepository;
    private final GrilleTarifaireRepository grilleTarifaireRepository;
    private final PaysRepository            paysRepository;
    private final CorridorConverter         corridorConverter;

    public FraisServiceImpl(CorridorRepository corridorRepository,
                            GrilleTarifaireRepository grilleTarifaireRepository,
                            PaysRepository paysRepository,
                            CorridorConverter corridorConverter) {
        this.corridorRepository        = corridorRepository;
        this.grilleTarifaireRepository = grilleTarifaireRepository;
        this.paysRepository            = paysRepository;
        this.corridorConverter         = corridorConverter;
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

        BigDecimal fraisVariables = montant
                .multiply(grille.getFraisPourcentage())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal montantFrais = grille.getFraisFixe().add(fraisVariables);

        FraisResult result = new FraisResult();
        result.setMontantFrais(montantFrais);
        result.setPartAgence(grille.getPartAgence());
        result.setPartCentrale(grille.getPartCentrale());
        BigDecimal commissionAgence = grille.getPartAgence() != null
                ? grille.getPartAgence() : BigDecimal.ZERO;
        result.setMontantRecu(montant.subtract(montantFrais).subtract(commissionAgence));

        // Taux derived from pays.devise
        Corridor corridor = grille.getCorridor();
        BigDecimal tauxSource = tauxVersEuro(corridor.getPaysSource());
        BigDecimal tauxDest   = tauxVersEuro(corridor.getPaysDestination());
        BigDecimal taux = BigDecimal.ZERO;
        if (tauxDest.compareTo(BigDecimal.ZERO) != 0) {
            taux = tauxSource.divide(tauxDest, 4, RoundingMode.HALF_UP);
        }
        result.setTaux(taux);
        result.setDelaiMin(5);
        result.setGrilleTarifaireId(grille.getId());

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Corridor getCorridorActif(String isoSource, String isoDestination) {
        return corridorRepository
                .findByPaysSource_CodeIsoAndPaysDestination_CodeIsoAndActifTrue(isoSource, isoDestination)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Aucun corridor actif entre " + isoSource + " et " + isoDestination));
    }

    // ── ICorridorService ──────────────────────────────────────────────────────

    @Override
    public CorridorResponse creer(CorridorRequest request) {
        Pays paysSource = paysRepository.findById(request.getPaysSourceId())
                .orElseThrow(() -> new IllegalArgumentException("Pays source introuvable."));
        Pays paysDestination = paysRepository.findById(request.getPaysDestinationId())
                .orElseThrow(() -> new IllegalArgumentException("Pays destination introuvable."));

        if (corridorRepository.existsByPaysSource_IdAndPaysDestination_Id(
                paysSource.getId(), paysDestination.getId())) {
            throw new IllegalArgumentException("Un corridor entre ces deux pays existe déjà.");
        }

        Corridor corridor = new Corridor();
        corridor.setPaysSource(paysSource);
        corridor.setPaysDestination(paysDestination);
        corridor.setActif(true);
        corridor.setDateActivation(LocalDate.now());

        return corridorConverter.toResponse(corridorRepository.save(corridor));
    }

    @Override
    @Transactional(readOnly = true)
    public CorridorResponse getById(Long id) {
        Corridor corridor = corridorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Corridor introuvable : " + id));
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
                .orElseThrow(() -> new IllegalArgumentException("Corridor introuvable : " + id));
        corridor.setActif(true);
        corridor.setDateActivation(LocalDate.now());
        corridorRepository.save(corridor);
    }

    @Override
    public void desactiver(Long id) {
        Corridor corridor = corridorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Corridor introuvable : " + id));
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

    @Override
    @Transactional(readOnly = true)
    public List<GrilleTarifaireResponse> getGrillesByCorridor(Long corridorId) {
        corridorRepository.findById(corridorId)
                .orElseThrow(() -> new IllegalArgumentException("Corridor introuvable : " + corridorId));
        return grilleTarifaireRepository.findByCorridor_IdOrderByMontantMinAsc(corridorId)
                .stream().map(this::toGrilleResponse).collect(Collectors.toList());
    }

    @Override
    public GrilleTarifaireResponse updateGrille(Long id, GrilleTarifaireRequest request) {
        GrilleTarifaire grille = grilleTarifaireRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Grille tarifaire introuvable : " + id));
        grille.setMontantMin(request.getMontantMin());
        grille.setMontantMax(request.getMontantMax());
        grille.setFraisFixe(request.getFraisFixe());
        grille.setFraisPourcentage(request.getFraisPourcentage());
        grille.setPartAgence(request.getPartAgence());
        grille.setPartCentrale(request.getPartCentrale());
        return toGrilleResponse(grilleTarifaireRepository.save(grille));
    }

    @Override
    public void deleteGrille(Long id) {
        if (!grilleTarifaireRepository.existsById(id)) {
            throw new IllegalArgumentException("Grille tarifaire introuvable : " + id);
        }
        grilleTarifaireRepository.deleteById(id);
    }

    private BigDecimal tauxVersEuro(Pays pays) {
        if (pays == null || pays.getDevise() == null) return BigDecimal.ONE;
        BigDecimal taux = pays.getDevise().getTauxVersEuro();
        return taux != null ? taux : BigDecimal.ONE;
    }

    private GrilleTarifaireResponse toGrilleResponse(GrilleTarifaire g) {
        GrilleTarifaireResponse r = new GrilleTarifaireResponse();
        r.setId(g.getId());
        r.setCorridorId(g.getCorridor().getId());
        r.setMontantMin(g.getMontantMin());
        r.setMontantMax(g.getMontantMax());
        r.setFraisFixe(g.getFraisFixe());
        r.setFraisPourcentage(g.getFraisPourcentage());
        r.setPartAgence(g.getPartAgence());
        r.setPartCentrale(g.getPartCentrale());
        return r;
    }
}
