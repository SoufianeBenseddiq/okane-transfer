package com.okanetransfer.shared.config;

import com.okanetransfer.entity.agence.Agence;
import com.okanetransfer.entity.aml.JournalAudit;
import com.okanetransfer.entity.aml.RegleAML;
import com.okanetransfer.entity.devise.Corridor;
import com.okanetransfer.entity.devise.Devise;
import com.okanetransfer.entity.devise.GrilleTarifaire;
import com.okanetransfer.entity.user.Administrateur;
import com.okanetransfer.entity.user.Agent;
import com.okanetransfer.entity.user.Manager;
import com.okanetransfer.entity.user.Utilisateur;
import com.okanetransfer.repository.agence.AgenceRepository;
import com.okanetransfer.repository.aml.JournalAuditRepository;
import com.okanetransfer.repository.aml.RegleAMLRepository;
import com.okanetransfer.repository.devise.CorridorRepository;
import com.okanetransfer.repository.devise.DeviseRepository;
import com.okanetransfer.repository.devise.GrilleTarifaireRepository;
import com.okanetransfer.repository.user.UtilisateurRepository;
import com.okanetransfer.shared.enums.RoleUtilisateur;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class DataInitializer {

    private final UtilisateurRepository     utilisateurRepository;
    private final PasswordEncoder           passwordEncoder;
    private final DeviseRepository          deviseRepository;
    private final CorridorRepository        corridorRepository;
    private final GrilleTarifaireRepository grilleTarifaireRepository;
    private final AgenceRepository          agenceRepository;
    private final RegleAMLRepository        regleAMLRepository;
    private final JournalAuditRepository    journalAuditRepository;

    public DataInitializer(UtilisateurRepository utilisateurRepository,
                           PasswordEncoder passwordEncoder,
                           DeviseRepository deviseRepository,
                           CorridorRepository corridorRepository,
                           GrilleTarifaireRepository grilleTarifaireRepository,
                           AgenceRepository agenceRepository,
                           RegleAMLRepository regleAMLRepository,
                           JournalAuditRepository journalAuditRepository) {
        this.utilisateurRepository     = utilisateurRepository;
        this.passwordEncoder           = passwordEncoder;
        this.deviseRepository          = deviseRepository;
        this.corridorRepository        = corridorRepository;
        this.grilleTarifaireRepository = grilleTarifaireRepository;
        this.agenceRepository          = agenceRepository;
        this.regleAMLRepository        = regleAMLRepository;
        this.journalAuditRepository    = journalAuditRepository;
    }

    @EventListener(ContextRefreshedEvent.class)
    @Transactional
    public void seed() {
        initAdmin();
        Map<String, Devise> devises = initDevises();
        initCorridorsAndGrilles(devises);
        initAgencesAndUsers();
        initReglesAML();
        initJournalAudit();
    }

    // ── Admin ─────────────────────────────────────────────────────────────────

    private void initAdmin() {
        String email = "okane.admin@gmail.com";
        if (utilisateurRepository.existsByEmail(email)) return;

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

    // ── Devises ───────────────────────────────────────────────────────────────

    private Map<String, Devise> initDevises() {
        Map<String, Devise> result = new HashMap<>();

        Object[][] data = {
            { "MAD", "Dirham Marocain",   "د.م.", "0.093000" },
            { "EUR", "Euro",               "€",    "1.000000" },
            { "USD", "Dollar Américain",   "$",    "0.921000" },
            { "GBP", "Livre Sterling",     "£",    "1.171000" },
            { "XOF", "Franc CFA Ouest",    "FCFA", "0.001524" },
            { "XAF", "Franc CFA Centre",   "FCFA", "0.001524" },
            { "DZD", "Dinar Algérien",     "دج",   "0.006900" },
            { "TND", "Dinar Tunisien",     "د.ت",  "0.297000" },
            { "EGP", "Livre Égyptienne",   "£E",   "0.019000" },
            { "CAD", "Dollar Canadien",    "CA$",  "0.677000" },
            { "SAR", "Riyal Saoudien",     "﷼",    "0.245000" },
            { "AED", "Dirham Émirien",     "د.إ",  "0.251000" },
        };

        for (Object[] row : data) {
            String code = (String) row[0];
            Devise d = deviseRepository.findByCode(code).orElseGet(() -> {
                Devise nd = new Devise();
                nd.setCode(code);
                nd.setNom((String) row[1]);
                nd.setSymbole((String) row[2]);
                nd.setTauxVersEuro(new BigDecimal((String) row[3]));
                nd.setSourceTaux("MANUEL");
                nd.setActive(true);
                nd.setDerniereMaj(LocalDateTime.now());
                return deviseRepository.save(nd);
            });
            result.put(code, d);
        }
        return result;
    }

    // ── Corridors & Grilles ───────────────────────────────────────────────────

    private void initCorridorsAndGrilles(Map<String, Devise> d) {

        Corridor madEur = createCorridorIfNew(d.get("MAD"), d.get("EUR"));
        if (madEur != null) {
            saveGrilles(madEur, new Object[][] {
                { "0.01",    "500.00",  "20.00",  "8.00",  "12.00" },
                { "500.01",  "2000.00", "35.00",  "14.00", "21.00" },
                { "2000.01", "5000.00", "60.00",  "24.00", "36.00" },
                { "5000.01", "9999.99", "90.00",  "36.00", "54.00" },
            });
        }

        Corridor madXof = createCorridorIfNew(d.get("MAD"), d.get("XOF"));
        if (madXof != null) {
            saveGrilles(madXof, new Object[][] {
                { "0.01",    "500.00",  "25.00",  "10.00", "15.00" },
                { "500.01",  "2000.00", "40.00",  "16.00", "24.00" },
                { "2000.01", "5000.00", "70.00",  "28.00", "42.00" },
                { "5000.01", "9999.99", "100.00", "40.00", "60.00" },
            });
        }

        createCorridorIfNew(d.get("MAD"), d.get("USD"));
        createCorridorIfNew(d.get("MAD"), d.get("XAF"));
        createCorridorIfNew(d.get("MAD"), d.get("DZD"));
        createCorridorIfNew(d.get("MAD"), d.get("TND"));
        createCorridorIfNew(d.get("MAD"), d.get("EGP"));
        createCorridorIfNew(d.get("MAD"), d.get("CAD"));
        createCorridorIfNew(d.get("MAD"), d.get("GBP"));
        createCorridorIfNew(d.get("MAD"), d.get("SAR"));
        createCorridorIfNew(d.get("MAD"), d.get("AED"));
        createCorridorIfNew(d.get("EUR"), d.get("MAD"));
        createCorridorIfNew(d.get("EUR"), d.get("XOF"));
        createCorridorIfNew(d.get("EUR"), d.get("USD"));
    }

    private Corridor createCorridorIfNew(Devise src, Devise dst) {
        if (corridorRepository.existsByDeviseSource_IdAndDeviseDestination_Id(src.getId(), dst.getId())) {
            return null;
        }
        Corridor c = new Corridor();
        c.setDeviseSource(src);
        c.setDeviseDestination(dst);
        c.setActif(true);
        c.setDateActivation(LocalDate.now());
        return corridorRepository.save(c);
    }

    private void saveGrilles(Corridor corridor, Object[][] rows) {
        for (Object[] row : rows) {
            GrilleTarifaire g = new GrilleTarifaire();
            g.setCorridor(corridor);
            g.setMontantMin(new BigDecimal((String) row[0]));
            g.setMontantMax(new BigDecimal((String) row[1]));
            g.setFraisFixe(new BigDecimal((String) row[2]));
            g.setPartAgence(new BigDecimal((String) row[3]));
            g.setPartCentrale(new BigDecimal((String) row[4]));
            grilleTarifaireRepository.save(g);
        }
    }

    // ── Agences, Managers & Agents ────────────────────────────────────────────

    private void initAgencesAndUsers() {

        Agence marrakech = agenceRepository.findByNom("Okane Marrakech");
        if (marrakech == null) {
            marrakech = new Agence();
            marrakech.setNom("Okane Marrakech");
            marrakech.setAdresse("Avenue Mohammed VI, Marrakech");
            marrakech.setPays("Maroc");
            marrakech.setPlafondJournalier(new BigDecimal("200000.00"));
            marrakech.setActive(true);
            marrakech = agenceRepository.save(marrakech);
        }

        Agence casablanca = agenceRepository.findByNom("Okane Casablanca");
        if (casablanca == null) {
            casablanca = new Agence();
            casablanca.setNom("Okane Casablanca");
            casablanca.setAdresse("Avenue Fal Ould Oumeir, Rabat");
            casablanca.setPays("Maroc");
            casablanca.setPlafondJournalier(new BigDecimal("150000.00"));
            casablanca.setActive(true);
            casablanca = agenceRepository.save(casablanca);
        }

        saveManager("ayman.bouhmouch@okanetransfer.ma",     "Bouhmouch", "Ayman",      "+212661000001", marrakech);
        saveManager("siham.elasli@okanetransfer.ma",        "El Asli",   "Siham",      "+212661000002", casablanca);

        saveAgent("zakaria.tabati@okanetransfer.ma",        "Tabati",    "Zakaria",    "+212662000001", marrakech);
        saveAgent("soufiane.benseddiq@okanetransfer.ma",    "Benseddiq", "Soufiane",   "+212662000002", marrakech);
        saveAgent("yousfi.btissam@okanetransfer.ma",        "Yousfi",    "Btissam",    "+212662000003", casablanca);
        saveAgent("abdelghani.bensalih@okanetransfer.ma",   "Bensalih",  "Abdelghani", "+212662000004", casablanca);
    }

    private void saveManager(String email, String nom, String prenom, String tel, Agence agence) {
        if (utilisateurRepository.existsByEmail(email)) return;
        Manager m = new Manager();
        m.setNom(nom); m.setPrenom(prenom); m.setEmail(email);
        m.setMotDePasseHash(passwordEncoder.encode("Okane123"));
        m.setTelephone(tel); m.setPays("MA");
        m.setRole(RoleUtilisateur.ROLE_MANAGER);
        m.setActif(true); m.setAgence(agence);
        utilisateurRepository.save(m);
    }

    private void saveAgent(String email, String nom, String prenom, String tel, Agence agence) {
        if (utilisateurRepository.existsByEmail(email)) return;
        Agent a = new Agent();
        a.setNom(nom); a.setPrenom(prenom); a.setEmail(email);
        a.setMotDePasseHash(passwordEncoder.encode("Okane123"));
        a.setTelephone(tel); a.setPays("MA");
        a.setRole(RoleUtilisateur.ROLE_AGENT);
        a.setActif(true); a.setAgence(agence);
        utilisateurRepository.save(a);
    }

    // ── Règles AML ────────────────────────────────────────────────────────────

    private void initReglesAML() {
        if (regleAMLRepository.count() > 0) return;

        Object[][] data = {
            { "DETECTION_MONTANT_ELEVE",
              "Transfert unique dépassant le seuil réglementaire de 10 000 MAD",
              "10000.00", null, null, true },
            { "DETECTION_FRACTIONNEMENT",
              "Plusieurs petits transferts consécutifs visant à contourner le seuil",
              "3000.00",  3,    180,  true },
            { "SURVEILLANCE_FREQUENCE",
              "Fréquence anormalement élevée de transactions en peu de temps",
              null,       5,    60,   true },
            { "PAYS_A_RISQUE",
              "Transfert vers un pays classé à haut risque de blanchiment",
              "5000.00",  null, null, true },
            { "COMPTE_DORMANT_ACTIF",
              "Activité soudaine sur un compte inactif depuis plus de 6 mois",
              "2000.00",  2,    1440, false },
        };

        for (Object[] row : data) {
            RegleAML r = new RegleAML();
            r.setNom((String) row[0]);
            r.setDescription((String) row[1]);
            r.setSeuilMontant(row[2] != null ? new BigDecimal((String) row[2]) : null);
            r.setSeuilNbTransactions((Integer) row[3]);
            r.setFenetreTempsMinutes((Integer) row[4]);
            r.setActive((Boolean) row[5]);
            regleAMLRepository.save(r);
        }
    }

    // ── Journal d'audit ───────────────────────────────────────────────────────

    private void initJournalAudit() {
        if (journalAuditRepository.count() > 0) return;

        Utilisateur admin = utilisateurRepository.findByEmail("okane.admin@gmail.com").orElse(null);
        if (admin == null) return;

        Object[][] rows = {
            { "CREATE_UTILISATEUR",      "Utilisateur",      2L,
              null,
              "{\"nom\":\"Tabati\",\"prenom\":\"Zakaria\",\"role\":\"ROLE_AGENT\",\"actif\":true}",
              "192.168.1.10" },
            { "UPDATE_DEVISE",           "Devise",           1L,
              "{\"code\":\"MAD\",\"tauxVersEuro\":0.090000}",
              "{\"code\":\"MAD\",\"tauxVersEuro\":0.093000}",
              "192.168.1.10" },
            { "CREATE_AGENCE",           "Agence",           1L,
              null,
              "{\"nom\":\"Okane Marrakech\",\"pays\":\"Maroc\",\"active\":true}",
              "10.0.0.5" },
            { "DELETE_CORRIDOR",         "Corridor",         99L,
              "{\"source\":\"USD\",\"destination\":\"XOF\",\"actif\":true}",
              null,
              "10.0.0.5" },
            { "UPDATE_REGLE_AML",        "RegleAML",         1L,
              "{\"nom\":\"DETECTION_MONTANT_ELEVE\",\"seuilMontant\":8000}",
              "{\"nom\":\"DETECTION_MONTANT_ELEVE\",\"seuilMontant\":10000}",
              "192.168.1.22" },
            { "CREATE_GRILLE_TARIFAIRE", "GrilleTarifaire",  3L,
              null,
              "{\"montantMin\":2000.01,\"montantMax\":5000.00,\"fraisFixe\":60.00}",
              "192.168.1.10" },
            { "DESACTIVER_UTILISATEUR",  "Utilisateur",      5L,
              "{\"email\":\"yousfi.btissam@okanetransfer.ma\",\"actif\":true}",
              "{\"email\":\"yousfi.btissam@okanetransfer.ma\",\"actif\":false}",
              "10.0.0.5" },
        };

        for (Object[] row : rows) {
            JournalAudit e = new JournalAudit();
            e.setActeur(admin);
            e.setAction((String) row[0]);
            e.setEntiteCible((String) row[1]);
            e.setIdCible((Long) row[2]);
            e.setDetailAvant((String) row[3]);
            e.setDetailApres((String) row[4]);
            e.setIpAdresse((String) row[5]);
            journalAuditRepository.save(e);
        }
    }
}
