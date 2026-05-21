package com.okanetransfer.repository.user;

import com.okanetransfer.entity.user.Agent;
import com.okanetransfer.entity.user.Client;
import com.okanetransfer.entity.user.Manager;
import com.okanetransfer.entity.user.Utilisateur;
import com.okanetransfer.shared.enums.RoleUtilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByTelephone(String telephone);


    List<Utilisateur> findByRole(RoleUtilisateur role);

    List<Utilisateur> findByRoleAndActif(RoleUtilisateur role, Boolean actif);

    // Client

    // utilisé par TransfertServiceImpl pour lier le transfert au compte client
    @Query("SELECT c FROM Client c WHERE c.telephone = :telephone AND c.actif = true")
    Optional<Client> findClientByTelephone(@Param("telephone") String telephone);

    // utilisé par TransfertServiceImpl pour l'historique en ligne
    @Query("SELECT c FROM Client c WHERE c.email = :email AND c.actif = true")
    Optional<Client> findClientByEmail(@Param("email") String email);

    @Query("SELECT c FROM Client c WHERE c.id = :id")
    Optional<Client> findClientById(@Param("id") Long id);

    // Agent

    // Used in TransfertServiceImpl to get connected Agent
    @Query("SELECT a FROM Agent a WHERE a.email = :email AND a.actif = true")
    Optional<Agent> findAgentByEmail(@Param("email") String email);

    // Fetch a specific Agency agents
    @Query("SELECT a FROM Agent a WHERE a.agence.id = :agenceId")
    List<Agent> findAgentsByAgenceId(@Param("agenceId") Long agenceId);

    // Fetch a specific Agency actif agents
    @Query("SELECT a FROM Agent a WHERE a.agence.id = :agenceId AND a.actif = true")
    List<Agent> findAgentsActifsByAgenceId(@Param("agenceId") Long agenceId);

    // Manager

    @Query("SELECT m FROM Manager m WHERE m.email = :email AND m.actif = true")
    Optional<Manager> findManagerByEmail(@Param("email") String email);

    // Fetch a specific Agency manager
    @Query("SELECT m FROM Manager m WHERE m.agence.id = :agenceId")
    Optional<Manager> findManagerByAgenceId(@Param("agenceId") Long agenceId);


}