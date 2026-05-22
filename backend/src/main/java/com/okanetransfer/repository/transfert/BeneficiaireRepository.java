package com.okanetransfer.repository.transfert;

import com.okanetransfer.entity.transfert.Beneficiaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BeneficiaireRepository
        extends JpaRepository<Beneficiaire, Long> {
}