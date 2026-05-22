package com.okanetransfer.repository.aml;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.okanetransfer.entity.aml.DeclarationSoupcon;

@Repository
public interface DeclarationSoupconRepository extends JpaRepository<DeclarationSoupcon, Long> {
}
