package com.okanetransfer.repository.agence;

import com.okanetransfer.entity.agence.Agence;
import com.okanetransfer.entity.user.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgenceRepository extends JpaRepository<Agence, Long> {
    Agence findByNom(String nom);
    Agence findByAdresse(String adresse);
    Agence findByResponsableEmail(String email);
    void deleteById(long id);
    List<Agence> findByActiveTrue();
}
