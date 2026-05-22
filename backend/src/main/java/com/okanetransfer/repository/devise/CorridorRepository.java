package com.okanetransfer.repository.devise;

import com.okanetransfer.entity.devise.Corridor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CorridorRepository extends JpaRepository<Corridor, Long> {

    Optional<Corridor> findByDeviseSource_CodeAndDeviseDestination_CodeAndActifTrue(
            String codeSource, String codeDestination);

    boolean existsByDeviseSource_IdAndDeviseDestination_Id(
            Long sourceId, Long destinationId);
}