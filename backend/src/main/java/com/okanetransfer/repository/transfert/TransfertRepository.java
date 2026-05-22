package com.okanetransfer.repository.transfert;

import com.okanetransfer.entity.transfert.Transfert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransfertRepository
        extends JpaRepository<Transfert, Long> {

    Optional<Transfert> findByCodeRetrait(String codeRetrait);

    Optional<Transfert> findByNumeroReference(String numeroReference);

    List<Transfert> findByExpediteurClientId(Long clientId);
}
