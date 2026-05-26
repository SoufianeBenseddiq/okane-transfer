package com.okanetransfer.repository.transfert;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.okanetransfer.entity.transfert.Transfert;

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
            AND (:debut IS NULL OR t.creeLe >= :debut)
            AND (:fin IS NULL OR t.creeLe <= :fin)
            ORDER BY t.creeLe DESC
            """)
    List<Transfert> findByAgenceEnvoiId(
            @Param("agenceId") Long agenceId,
            @Param("debut") Instant debut,
            @Param("fin") Instant fin);
}
