package com.okanetransfer.service.facade.devise;

import com.okanetransfer.service.dto.devise.request.CorridorRequest;
import com.okanetransfer.service.dto.devise.response.CorridorResponse;

import java.util.List;

public interface ICorridorService {

    CorridorResponse creer(CorridorRequest request);
    CorridorResponse getById(Long id);
    List<CorridorResponse> getAll();
    void activer(Long id);
    void desactiver(Long id);
}