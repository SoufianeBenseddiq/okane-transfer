package com.okanetransfer.service.facade.mobilemoney;

import com.okanetransfer.service.dto.mobilemoney.request.CreateTransfertMobileMoneyRequest;
import com.okanetransfer.service.dto.mobilemoney.request.UpdateTransfertMobileMoneyRequest;
import com.okanetransfer.service.dto.mobilemoney.response.TransfertMobileMoneyResponse;

import java.util.List;

public interface ITransfertMobileMoneyService {

    List<TransfertMobileMoneyResponse> getAllTransfertsMobileMoney();

    TransfertMobileMoneyResponse getTransfertMobileMoneyById(Long id);

    TransfertMobileMoneyResponse createTransfertMobileMoney(CreateTransfertMobileMoneyRequest request);

    TransfertMobileMoneyResponse updateTransfertMobileMoney(Long id, UpdateTransfertMobileMoneyRequest request);

    void deleteTransfertMobileMoney(Long id);

    TransfertMobileMoneyResponse getByTransfertId(Long transfertId);

    List<TransfertMobileMoneyResponse> getByOperateur(String operateur);

    List<TransfertMobileMoneyResponse> getByStatutMobile(String statutMobile);

    TransfertMobileMoneyResponse getByReferenceOperateur(String referenceOperateur);
}
