package com.okanetransfer.repository.caisse;

import com.okanetransfer.entity.caisse.ClotureCaisse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ClotureCaisseRepository extends JpaRepository<ClotureCaisse, Long> {
    ClotureCaisse findByAgentEmailAndDate(String email, LocalDate date);
    List<ClotureCaisse> findByEcartSignaleTrue();
    List<ClotureCaisse> findByAgentEmailAndEcartSignaleTrue(String email);
    void deleteById(Long id);
}
