package com.okanetransfer.repository.devise;

import com.okanetransfer.entity.devise.GrilleTarifaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface GrilleTarifaireRepository extends JpaRepository<GrilleTarifaire, Long> {

    List<GrilleTarifaire> findByCorridor_IdOrderByMontantMinAsc(Long corridorId);

    Optional<GrilleTarifaire> findByCorridor_IdAndMontantMinLessThanEqualAndMontantMaxGreaterThanEqual(
            Long corridorId, BigDecimal montant1, BigDecimal montant2);
}