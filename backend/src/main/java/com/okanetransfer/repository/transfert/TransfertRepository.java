package com.okanetransfer.repository.transfert;

import com.okanetransfer.entity.transfert.Transfert;
import com.okanetransfer.shared.enums.StatutTransfert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransfertRepository
        extends JpaRepository<Transfert, Long> {

    @Query("""
        SELECT t FROM Transfert t
        WHERE function('replace', upper(t.codeRetrait), '-', '') = :codeRetrait
           OR upper(t.codeRetrait) = :codeRetrait
        """)
    Optional<Transfert> findByCodeRetraitNormalise(
            @Param("codeRetrait") String codeRetrait
    );

    Optional<Transfert> findByCodeRetrait(String codeRetrait);

    Optional<Transfert> findByNumeroReference(String numeroReference);

    List<Transfert> findByExpediteurClientId(Long clientId);

    @Query("""
        SELECT t FROM Transfert t
        WHERE t.beneficiaire.telephone = :telephone
        ORDER BY t.creeLe DESC
        """)
    List<Transfert> findByBeneficiairePhone(
            @Param("telephone") String telephone
    );
}
