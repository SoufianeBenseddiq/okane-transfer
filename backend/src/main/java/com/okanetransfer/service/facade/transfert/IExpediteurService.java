package com.okanetransfer.service.facade.transfert;

import com.okanetransfer.service.dto.transfert.request.CreateExpediteurRequest;
import com.okanetransfer.service.dto.transfert.request.UpdateExpediteurRequest;
import com.okanetransfer.service.dto.transfert.response.ExpediteurResponse;

import java.util.List;

public interface IExpediteurService {

    List<ExpediteurResponse> getAllExpediteurs();

    ExpediteurResponse getExpediteurById(Long id);

    ExpediteurResponse createExpediteur(CreateExpediteurRequest request);

    ExpediteurResponse updateExpediteur(Long id, UpdateExpediteurRequest request);

    void deleteExpediteur(Long id);
}
