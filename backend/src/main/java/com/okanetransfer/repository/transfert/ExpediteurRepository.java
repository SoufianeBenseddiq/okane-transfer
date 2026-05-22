package com.okanetransfer.repository.transfert;

import com.okanetransfer.entity.transfert.Expediteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpediteurRepository
        extends JpaRepository<Expediteur, Long> {
}