package com.okanetransfer.service.converter.mobilemoney;

import com.okanetransfer.entity.mobilemoney.TransfertMobileMoney;
import com.okanetransfer.service.dto.mobilemoney.request.CreateTransfertMobileMoneyRequest;
import com.okanetransfer.service.dto.mobilemoney.response.TransfertMobileMoneyResponse;

public class TransfertMobileMoneyConverter {

    public static TransfertMobileMoney toEntity(CreateTransfertMobileMoneyRequest request) {
        TransfertMobileMoney transfertMobileMoney = new TransfertMobileMoney();
        transfertMobileMoney.setOperateur(request.getOperateur());
        transfertMobileMoney.setNumeroCible(request.getNumeroCible());
        transfertMobileMoney.setStatutMobile("EN_ATTENTE");
        return transfertMobileMoney;
    }

    public static TransfertMobileMoneyResponse toResponse(TransfertMobileMoney transfertMobileMoney) {
        return new TransfertMobileMoneyResponse(
                transfertMobileMoney.getId(),
                transfertMobileMoney.getTransfert().getId(),
                transfertMobileMoney.getOperateur(),
                transfertMobileMoney.getNumeroCible(),
                transfertMobileMoney.getStatutMobile(),
                transfertMobileMoney.getReferenceOperateur(),
                transfertMobileMoney.getEnvoyeLe()
        );
    }
}
