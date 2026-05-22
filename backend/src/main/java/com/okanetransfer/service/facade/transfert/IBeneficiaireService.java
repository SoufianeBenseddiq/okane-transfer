package com.okanetransfer.service.facade.transfert;

import com.okanetransfer.service.dto.transfert.request.CreateBeneficiaireRequest;
import com.okanetransfer.service.dto.transfert.request.UpdateBeneficiaireRequest;
import com.okanetransfer.service.dto.transfert.response.BeneficiaireResponse;

import java.util.List;

public interface IBeneficiaireService {

    List<BeneficiaireResponse> getAllBeneficiaires();

    BeneficiaireResponse getBeneficiaireById(Long id);

    BeneficiaireResponse createBeneficiaire(CreateBeneficiaireRequest request);

    BeneficiaireResponse updateBeneficiaire(Long id, UpdateBeneficiaireRequest request);

    void deleteBeneficiaire(Long id);
}
