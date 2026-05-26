package com.okanetransfer.repository.caisse;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.okanetransfer.entity.caisse.ClotureCaisse;

public interface ClotureCaisseRepository extends JpaRepository<ClotureCaisse, Long> {
    ClotureCaisse findByAgentEmailAndDate(String email, LocalDate date);

    List<ClotureCaisse> findByEcartSignaleTrue();

    List<ClotureCaisse> findByAgentEmailAndEcartSignaleTrue(String email);

    void deleteById(Long id);

    @Query("""
            SELECT cc FROM ClotureCaisse cc
            WHERE cc.agent.agence.id = :agenceId
            ORDER BY cc.date DESC
            """)
    List<ClotureCaisse> findByAgenceIdOrderByDateDesc(
            @Param("agenceId") Long agenceId);
}
