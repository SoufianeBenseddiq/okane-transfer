package com.okanetransfer.repository.mobilemoney;

import com.okanetransfer.entity.mobilemoney.TransfertMobileMoney;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransfertMobileMoneyRepository extends JpaRepository<TransfertMobileMoney, Long> {

    Optional<TransfertMobileMoney> findByTransfertId(Long transfertId);

    List<TransfertMobileMoney> findByOperateur(String operateur);

    List<TransfertMobileMoney> findByStatutMobile(String statutMobile);

    Optional<TransfertMobileMoney> findByReferenceOperateur(String referenceOperateur);
}
