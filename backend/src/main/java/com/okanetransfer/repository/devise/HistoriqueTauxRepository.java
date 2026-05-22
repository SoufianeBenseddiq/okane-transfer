package com.okanetransfer.repository.devise;

import com.okanetransfer.entity.devise.HistoriqueTaux;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoriqueTauxRepository extends JpaRepository<HistoriqueTaux, Long> {

    List<HistoriqueTaux> findByDevise_IdOrderByDateDesc(Long deviseId);
}