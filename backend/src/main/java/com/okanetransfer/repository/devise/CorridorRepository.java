package com.okanetransfer.repository.devise;

import com.okanetransfer.entity.devise.Corridor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CorridorRepository extends JpaRepository<Corridor, Long> {

    boolean existsByPaysSource_IdAndPaysDestination_Id(Long paysSourceId, Long paysDestinationId);

    Optional<Corridor> findByPaysSource_CodeIsoAndPaysDestination_CodeIsoAndActifTrue(
            String isoSource, String isoDestination);
}
