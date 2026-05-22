package com.okanetransfer.service.facade.devise;

import com.okanetransfer.service.dto.devise.request.DeviseRequest;
import com.okanetransfer.service.dto.devise.response.DeviseResponse;

import java.math.BigDecimal;
import java.util.List;

public interface IDeviseService {

    // CRUD devises
    DeviseResponse creer(DeviseRequest request);
    DeviseResponse getById(Long id);
    DeviseResponse getByCode(String code);
    List<DeviseResponse> getAll();
    DeviseResponse modifier(Long id, DeviseRequest request);
    void activer(Long id);
    void desactiver(Long id);

    // Conversion (utilisée par les autres modules)
    BigDecimal getTauxConversion(String codeSource, String codeDestination);
}