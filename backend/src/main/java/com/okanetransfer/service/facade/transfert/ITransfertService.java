package com.okanetransfer.service.facade.transfert;


import com.okanetransfer.service.dto.transfert.request.CreateTransfertRequest;
import com.okanetransfer.service.dto.transfert.request.CreateTransfertAvecNouveauClientRequest;
import com.okanetransfer.service.dto.transfert.request.PaiementRequest;
import com.okanetransfer.service.dto.transfert.request.SendTransfertReceiptEmailRequest;
import com.okanetransfer.service.dto.transfert.request.UpdateTransfertRequest;
import com.okanetransfer.service.dto.transfert.response.TransfertResponse;

import java.util.List;

public interface ITransfertService {

    List<TransfertResponse> getAllTransferts();

    TransfertResponse getTransfertById(Long id);

    TransfertResponse creerTransfert(CreateTransfertRequest request);

    TransfertResponse creerTransfertAvecNouveauClient(CreateTransfertAvecNouveauClientRequest request);

    TransfertResponse updateTransfert(Long id, UpdateTransfertRequest request);

    TransfertResponse payerTransfert(PaiementRequest request);

    TransfertResponse getByCodeRetrait(String codeRetrait);

    void envoyerRecuParEmail(SendTransfertReceiptEmailRequest request);

    List<TransfertResponse> getMesTransferts(Long clientId);

    TransfertResponse annulerTransfert(Long id);

    TransfertResponse getByTelephoneBeneficiaire(String telephone);
}
