package com.okanetransfer.service.converter.user;

import com.okanetransfer.entity.user.Utilisateur;
import com.okanetransfer.service.dto.user.request.CreateUserRequest;
import com.okanetransfer.service.dto.user.request.UpdateProfilRequest;
import com.okanetransfer.service.dto.user.response.UserResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UtilisateurConverter {

    /**
     * Populates common Utilisateur fields from a CreateUserRequest.
     * Subclass instantiation (Client/Agent/Manager/Administrateur) stays in the service
     * because role-based branching is business logic, not mapping.
     */
    public void applyCreate(Utilisateur utilisateur, CreateUserRequest request, PasswordEncoder encoder) {
        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setEmail(request.getEmail());
        utilisateur.setTelephone(request.getTelephone());
        utilisateur.setPays(request.getPays());
        utilisateur.setRole(request.getRole());
        utilisateur.setActif(true);
        utilisateur.setMotDePasseHash(encoder.encode(request.getMotDePasse()));
    }

    /**
     * Applies non-null fields only — PATCH semantics.
     */
    public void applyUpdate(Utilisateur utilisateur, UpdateProfilRequest request) {
        if (request.getNom()       != null) utilisateur.setNom(request.getNom());
        if (request.getPrenom()    != null) utilisateur.setPrenom(request.getPrenom());
        if (request.getTelephone() != null) utilisateur.setTelephone(request.getTelephone());
        if (request.getPays()      != null) utilisateur.setPays(request.getPays());
    }

    public UserResponse toResponse(Utilisateur u) {
        if (u == null) return null;

        UserResponse r = new UserResponse();
        r.setId(u.getId());
        r.setNom(u.getNom());
        r.setPrenom(u.getPrenom());
        r.setEmail(u.getEmail());
        r.setTelephone(u.getTelephone());
        r.setPays(u.getPays());
        r.setRole(u.getRole().name());
        r.setActif(u.getActif());
        r.setCreeLe(u.getCreeLe());
        return r;
    }

    public List<UserResponse> toResponseList(List<Utilisateur> utilisateurs) {
        if (utilisateurs == null || utilisateurs.isEmpty()) return List.of();
        return utilisateurs.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
