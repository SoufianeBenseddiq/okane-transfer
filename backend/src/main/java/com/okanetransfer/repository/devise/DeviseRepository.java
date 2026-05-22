package com.okanetransfer.repository.devise;

import com.okanetransfer.entity.devise.Devise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviseRepository extends JpaRepository<Devise, Long> {

    Optional<Devise> findByCode(String code);

    boolean existsByCode(String code);
}