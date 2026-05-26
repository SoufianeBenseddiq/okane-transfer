package com.okanetransfer.repository.transfert;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.okanetransfer.entity.transfert.Transfert;
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
            SELECT t FROM Transfert t
            WHERE t.beneficiaire.telephone = :telephone
            ORDER BY t.creeLe DESC
            """)
    List<Transfert> findByBeneficiairePhone(
            @Param("telephone") String telephone);

    @Query("""
            SELECT t FROM Transfert t
            WHERE t.agenceEnvoi.id = :agenceId
            AND CAST(t.creeLe AS LocalDate) BETWEEN :debut AND :fin
            ORDER BY t.creeLe DESC
            """)
    List<Transfert> findByAgenceAndDateRange(
            @Param("agenceId") Long agenceId,
            @Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin);
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
