package com.okanetransfer.service.facade.devise;

import com.okanetransfer.service.dto.devise.request.PaysRequest;
import com.okanetransfer.service.dto.devise.response.PaysResponse;

import java.util.List;

public interface IPaysService {
    List<PaysResponse> getAll();
    PaysResponse getById(Long id);
    PaysResponse creer(PaysRequest request);
    PaysResponse modifier(Long id, PaysRequest request);
}
