package com.okanetransfer.repository.agence;

import com.okanetransfer.entity.agence.Agence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AgenceRepository extends JpaRepository<Agence, Long> {
    Agence findByNom(String nom);
    Agence findByAdresse(String adresse);
    Agence findByResponsableEmail(String email);
    void deleteById(long id);
    List<Agence> findByActiveTrue();
    List<Agence> findByEstCentraleTrue();
    List<Agence> findByAgenceCentraleId(Long agenceCentraleId);

    @Query("SELECT a FROM Agence a JOIN a.agents ag WHERE ag.email = :email")
    Optional<Agence> findByAgentEmail(@Param("email") String email);
}
