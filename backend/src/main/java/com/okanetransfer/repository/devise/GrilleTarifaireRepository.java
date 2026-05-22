package com.okanetransfer.repository.devise;

import com.okanetransfer.entity.devise.GrilleTarifaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface GrilleTarifaireRepository extends JpaRepository<GrilleTarifaire, Long> {

    Optional<GrilleTarifaire> findByCorridor_IdAndMontantMinLessThanEqualAndMontantMaxGreaterThanEqual(
            Long corridorId, BigDecimal montant1, BigDecimal montant2);
}