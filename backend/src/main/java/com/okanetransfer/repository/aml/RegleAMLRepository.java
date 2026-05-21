package com.okanetransfer.repository.aml;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.okanetransfer.entity.aml.RegleAML;

@Repository
public interface RegleAMLRepository extends JpaRepository<RegleAML, Long> {
}
