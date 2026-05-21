package com.okanetransfer.repository.aml;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.okanetransfer.entity.aml.ListeOFAC;

@Repository
public interface ListeOFACRepository extends JpaRepository<ListeOFAC, Long> {
}
