package com.okanetransfer.service.facade.transfert;

import java.time.Instant;
import java.util.List;

import com.okanetransfer.service.dto.transfert.request.CreateTransfertRequest;
import com.okanetransfer.service.dto.transfert.request.PaiementRequest;
import com.okanetransfer.service.dto.transfert.request.UpdateTransfertRequest;
import com.okanetransfer.service.dto.transfert.response.TransfertResponse;

public interface ITransfertService {

    List<TransfertResponse> getAllTransferts();

    TransfertResponse getTransfertById(Long id);

    TransfertResponse creerTransfert(CreateTransfertRequest request);

    TransfertResponse updateTransfert(Long id, UpdateTransfertRequest request);

    TransfertResponse payerTransfert(PaiementRequest request);

    TransfertResponse getByCodeRetrait(String codeRetrait);

    List<TransfertResponse> getMesTransferts(Long clientId);

    TransfertResponse annulerTransfert(Long id);

    TransfertResponse getByTelephoneBeneficiaire(String telephone);

    List<TransfertResponse> getByAgence(Long agenceId, Instant debut, Instant fin);
}
