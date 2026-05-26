package com.okanetransfer.repository.transfert;

import com.okanetransfer.entity.transfert.Transfert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransfertRepository
        extends JpaRepository<Transfert, Long> {

    Optional<Transfert> findByCodeRetrait(String codeRetrait);

    Optional<Transfert> findByNumeroReference(String numeroReference);

    List<Transfert> findByExpediteurClientId(Long clientId);

    @Query("""
    SELECT COALESCE(SUM(t.grilleTarifaire.partAgence), 0)
    FROM Transfert t
    WHERE t.agentSaisie.email = :email
    AND t.creeLe BETWEEN :from AND :to
    AND t.statut <> 'ANNULE'
""")
    BigDecimal sumCommissionsAgent(
            @Param("email") String email,
            @Param("from") LocalDateTime from,
            @Param("to")    LocalDateTime to
    );
}
