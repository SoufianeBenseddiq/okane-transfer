package com.okanetransfer.service.facade.transfert;


import com.okanetransfer.service.dto.transfert.request.CreateTransfertRequest;
import com.okanetransfer.service.dto.transfert.request.PaiementRequest;
import com.okanetransfer.service.dto.transfert.request.UpdateTransfertRequest;
import com.okanetransfer.service.dto.transfert.response.TransfertResponse;
import com.okanetransfer.service.dto.transfert.response.TransfertStatsResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ITransfertService {

    List<TransfertResponse> getAllTransferts();

    TransfertStatsResponse getStats();

    TransfertResponse getTransfertById(Long id);

    TransfertResponse creerTransfert(CreateTransfertRequest request);

    TransfertResponse updateTransfert(Long id, UpdateTransfertRequest request);

    TransfertResponse payerTransfert(PaiementRequest request);

    TransfertResponse getByCodeRetrait(String codeRetrait);

    List<TransfertResponse> getMesTransferts(Long clientId);

    TransfertResponse annulerTransfert(Long id);

<<<<<<< HEAD
    TransfertResponse getByTelephoneBeneficiaire(String telephone);
=======
    BigDecimal commissionsAgent(String email, LocalDate debut, LocalDate fin);
>>>>>>> 78c7d10560abb4927f42ed9e093d2c396875add6
}
