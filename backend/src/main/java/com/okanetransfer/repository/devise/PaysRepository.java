package com.okanetransfer.repository.devise;

import com.okanetransfer.entity.devise.Pays;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaysRepository extends JpaRepository<Pays, Long> {
    boolean existsByNom(String nom);
    Optional<Pays> findByNom(String nom);
    Optional<Pays> findByCodeIso(String codeIso);
}
