package com.okanetransfer.service.impl.transfert;

import com.okanetransfer.entity.transfert.Beneficiaire;
import com.okanetransfer.repository.transfert.BeneficiaireRepository;
import com.okanetransfer.service.converter.BeneficiaireConverter;
import com.okanetransfer.service.dto.transfert.request.CreateBeneficiaireRequest;
import com.okanetransfer.service.dto.transfert.request.UpdateBeneficiaireRequest;
import com.okanetransfer.service.dto.transfert.response.BeneficiaireResponse;
import com.okanetransfer.service.facade.transfert.IBeneficiaireService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BeneficiaireServiceImpl implements IBeneficiaireService {

    private final BeneficiaireRepository beneficiaireRepository;

    public BeneficiaireServiceImpl(BeneficiaireRepository beneficiaireRepository) {
        this.beneficiaireRepository = beneficiaireRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BeneficiaireResponse> getAllBeneficiaires() {
        return beneficiaireRepository.findAll()
                .stream()
                .map(BeneficiaireConverter::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BeneficiaireResponse getBeneficiaireById(Long id) {
        Beneficiaire beneficiaire = findBeneficiaire(id);
        return BeneficiaireConverter.toResponse(beneficiaire);
    }

    @Override
    public BeneficiaireResponse createBeneficiaire(CreateBeneficiaireRequest request) {
        Beneficiaire beneficiaire = new Beneficiaire();
        fillBeneficiaire(beneficiaire, request.getNom(), request.getPrenom(),
                request.getTelephone(), request.getPays(), request.getSurListeSurveillance());

        beneficiaire = beneficiaireRepository.save(beneficiaire);

        return BeneficiaireConverter.toResponse(beneficiaire);
    }

    @Override
    public BeneficiaireResponse updateBeneficiaire(Long id, UpdateBeneficiaireRequest request) {
        Beneficiaire beneficiaire = findBeneficiaire(id);
        fillBeneficiaire(beneficiaire, request.getNom(), request.getPrenom(),
                request.getTelephone(), request.getPays(), request.getSurListeSurveillance());

        beneficiaire = beneficiaireRepository.save(beneficiaire);

        return BeneficiaireConverter.toResponse(beneficiaire);
    }

    @Override
    public void deleteBeneficiaire(Long id) {
        Beneficiaire beneficiaire = findBeneficiaire(id);
        beneficiaireRepository.delete(beneficiaire);
    }

    private Beneficiaire findBeneficiaire(Long id) {
        return beneficiaireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Beneficiaire introuvable"));
    }

    private void fillBeneficiaire(
            Beneficiaire beneficiaire,
            String nom,
            String prenom,
            String telephone,
            String pays,
            Boolean surListeSurveillance) {

        beneficiaire.setNom(nom);
        beneficiaire.setPrenom(prenom);
        beneficiaire.setTelephone(telephone);
        beneficiaire.setPays(pays);
        beneficiaire.setSurListeSurveillance(Boolean.TRUE.equals(surListeSurveillance));
    }
}
