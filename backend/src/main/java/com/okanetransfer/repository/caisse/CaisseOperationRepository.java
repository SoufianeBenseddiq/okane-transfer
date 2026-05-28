package com.okanetransfer.repository.caisse;

import com.okanetransfer.entity.caisse.CaisseOperation;
import com.okanetransfer.entity.user.Agent;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface CaisseOperationRepository extends JpaRepository<CaisseOperation, Long> {

    @EntityGraph(attributePaths = {"agent"})
    List<CaisseOperation> findByAgentEmail(String email);

    void deleteById(Long id);

    @Query(value = """
    SELECT COALESCE(SUM(
        CASE
            WHEN type IN ('OUVERTURE', 'ENVOI', 'DEPOT', 'REMBOURSEMENT', 'AJUSTEMENT') THEN montant
            WHEN type IN ('RETRAIT') THEN -montant
            ELSE 0
        END
    ), 0)
    FROM caisse_operations
    WHERE agent_id = :agentId
      AND date_heure BETWEEN :debut AND :fin
    """, nativeQuery = true)
    BigDecimal calculerSoldeTheorique(
            @Param("agentId") Long agentId,
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin
    );

    @EntityGraph(attributePaths = {"agent"})
    List<CaisseOperation> findByAgentEmailAndDateHeureBetweenOrderByDateHeureAsc(
            String agentEmail,
            LocalDateTime debut,
            LocalDateTime fin
    );

    List<CaisseOperation> findByAgentEmailAndDateHeureBetweenOrderByDateHeureDesc(
            String email, LocalDateTime from, LocalDateTime to);
}