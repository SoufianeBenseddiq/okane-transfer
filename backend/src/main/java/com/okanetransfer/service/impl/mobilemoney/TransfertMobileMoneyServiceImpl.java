package com.okanetransfer.service.impl.mobilemoney;

import com.okanetransfer.entity.mobilemoney.TransfertMobileMoney;
import com.okanetransfer.entity.transfert.Transfert;
import com.okanetransfer.repository.mobilemoney.TransfertMobileMoneyRepository;
import com.okanetransfer.repository.transfert.TransfertRepository;
import com.okanetransfer.service.converter.mobilemoney.TransfertMobileMoneyConverter;
import com.okanetransfer.service.dto.mobilemoney.request.CreateTransfertMobileMoneyRequest;
import com.okanetransfer.service.dto.mobilemoney.request.UpdateTransfertMobileMoneyRequest;
import com.okanetransfer.service.dto.mobilemoney.response.TransfertMobileMoneyResponse;
import com.okanetransfer.service.facade.mobilemoney.ITransfertMobileMoneyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TransfertMobileMoneyServiceImpl implements ITransfertMobileMoneyService {

    private final TransfertMobileMoneyRepository transfertMobileMoneyRepository;
    private final TransfertRepository transfertRepository;

    public TransfertMobileMoneyServiceImpl(
            TransfertMobileMoneyRepository transfertMobileMoneyRepository,
            TransfertRepository transfertRepository) {
        this.transfertMobileMoneyRepository = transfertMobileMoneyRepository;
        this.transfertRepository = transfertRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransfertMobileMoneyResponse> getAllTransfertsMobileMoney() {
        return transfertMobileMoneyRepository.findAll()
                .stream()
                .map(TransfertMobileMoneyConverter::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TransfertMobileMoneyResponse getTransfertMobileMoneyById(Long id) {
        TransfertMobileMoney transfertMobileMoney = transfertMobileMoneyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transfert Mobile Money introuvable"));
        return TransfertMobileMoneyConverter.toResponse(transfertMobileMoney);
    }

    @Override
    public TransfertMobileMoneyResponse createTransfertMobileMoney(CreateTransfertMobileMoneyRequest request) {
        if (request.getTransfertId() == null) {
            throw new RuntimeException("Transfert ID requis");
        }

        if (request.getOperateur() == null || request.getOperateur().trim().isEmpty()) {
            throw new RuntimeException("Opérateur requis");
        }

        if (request.getNumeroCible() == null || request.getNumeroCible().trim().isEmpty()) {
            throw new RuntimeException("Numéro cible requis");
        }

        Transfert transfert = transfertRepository.findById(request.getTransfertId())
                .orElseThrow(() -> new RuntimeException("Transfert introuvable"));

        // Vérifier si un mobile money existe déjà pour ce transfert
        if (transfertMobileMoneyRepository.findByTransfertId(request.getTransfertId()).isPresent()) {
            throw new RuntimeException("Un transfert mobile money existe déjà pour ce transfert");
        }

        TransfertMobileMoney transfertMobileMoney = TransfertMobileMoneyConverter.toEntity(request);
        transfertMobileMoney.setTransfert(transfert);
        transfertMobileMoney = transfertMobileMoneyRepository.save(transfertMobileMoney);

        return TransfertMobileMoneyConverter.toResponse(transfertMobileMoney);
    }

    @Override
    public TransfertMobileMoneyResponse updateTransfertMobileMoney(Long id, UpdateTransfertMobileMoneyRequest request) {
        TransfertMobileMoney transfertMobileMoney = transfertMobileMoneyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transfert Mobile Money introuvable"));

        if (request.getNumeroCible() != null && !request.getNumeroCible().trim().isEmpty()) {
            transfertMobileMoney.setNumeroCible(request.getNumeroCible());
        }

        if (request.getStatutMobile() != null && !request.getStatutMobile().trim().isEmpty()) {
            transfertMobileMoney.setStatutMobile(request.getStatutMobile());
        }

        if (request.getReferenceOperateur() != null && !request.getReferenceOperateur().trim().isEmpty()) {
            transfertMobileMoney.setReferenceOperateur(request.getReferenceOperateur());
        }

        transfertMobileMoney = transfertMobileMoneyRepository.save(transfertMobileMoney);
        return TransfertMobileMoneyConverter.toResponse(transfertMobileMoney);
    }

    @Override
    public void deleteTransfertMobileMoney(Long id) {
        TransfertMobileMoney transfertMobileMoney = transfertMobileMoneyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transfert Mobile Money introuvable"));
        transfertMobileMoneyRepository.delete(transfertMobileMoney);
    }

    @Override
    @Transactional(readOnly = true)
    public TransfertMobileMoneyResponse getByTransfertId(Long transfertId) {
        TransfertMobileMoney transfertMobileMoney = transfertMobileMoneyRepository.findByTransfertId(transfertId)
                .orElseThrow(() -> new RuntimeException("Transfert Mobile Money introuvable pour ce transfert"));
        return TransfertMobileMoneyConverter.toResponse(transfertMobileMoney);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransfertMobileMoneyResponse> getByOperateur(String operateur) {
        return transfertMobileMoneyRepository.findByOperateur(operateur)
                .stream()
                .map(TransfertMobileMoneyConverter::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransfertMobileMoneyResponse> getByStatutMobile(String statutMobile) {
        return transfertMobileMoneyRepository.findByStatutMobile(statutMobile)
                .stream()
                .map(TransfertMobileMoneyConverter::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TransfertMobileMoneyResponse getByReferenceOperateur(String referenceOperateur) {
        TransfertMobileMoney transfertMobileMoney = transfertMobileMoneyRepository.findByReferenceOperateur(referenceOperateur)
                .orElseThrow(() -> new RuntimeException("Transfert Mobile Money introuvable avec cette référence opérateur"));
        return TransfertMobileMoneyConverter.toResponse(transfertMobileMoney);
    }
}
