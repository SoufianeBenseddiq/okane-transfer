package com.okanetransfer.shared.config;

import com.okanetransfer.entity.user.Administrateur;
import com.okanetransfer.repository.user.UtilisateurRepository;
import com.okanetransfer.shared.enums.RoleUtilisateur;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UtilisateurRepository utilisateurRepository,
                           PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder       = passwordEncoder;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void createDefaultAdmin() {
        String email = "okane.admin@gmail.com";

        if (utilisateurRepository.existsByEmail(email)) {
            return;
        }

        Administrateur admin = new Administrateur();
        admin.setNom("Okane");
        admin.setPrenom("Admin");
        admin.setEmail(email);
        admin.setMotDePasseHash(passwordEncoder.encode("Okane123"));
        admin.setTelephone("+212600000000");
        admin.setPays("MA");
        admin.setRole(RoleUtilisateur.ROLE_ADMIN);
        admin.setActif(true);

        utilisateurRepository.save(admin);
    }
}
