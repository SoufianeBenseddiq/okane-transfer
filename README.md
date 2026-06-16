# OkaneTransfer

> Plateforme de gestion des transferts d'argent national et international — Projet étudiant 2025–2026

---

## Table des matières

1. [Présentation](#présentation)
2. [Fonctionnalités](#fonctionnalités)
3. [Stack technologique](#stack-technologique)
4. [Architecture](#architecture)
5. [Prérequis](#prérequis)
6. [Installation et lancement](#installation-et-lancement)
7. [Variables d'environnement](#variables-denvironnement)
8. [API & Documentation Swagger](#api--documentation-swagger)
9. [Tests](#tests)
10. [Structure du projet](#structure-du-projet)
11. [Sécurité](#sécurité)

---

## Présentation

**OkaneTransfer** est une application web monolithique de gestion de transferts d'argent entre particuliers, inspirée des opérateurs mondiaux tels que Western Union et MoneyGram. Elle permet à une agence de gérer l'intégralité de ses opérations : du front-office (envoi, paiement) jusqu'à la réconciliation comptable et la conformité réglementaire.

Le projet couvre trois espaces distincts :
- **Administration** : gestion des devises, agences, frais, rapports consolidés
- **Agent** : saisie des envois, paiements, gestion de caisse
- **Client** : suivi en ligne des transferts, historique, notifications

---

## Fonctionnalités

### Espace Administration
- CRUD complet sur les devises (USD, EUR, MAD, GBP…)
- Activation/désactivation des corridors de transfert
- Mise à jour manuelle ou automatique des taux de change
- Paramétrage des grilles tarifaires par tranche de montant
- Gestion des agences (création, suspension, plafonds journaliers)
- Journal d'audit complet et rapports consolidés

### Espace Agent (Front Office)
- Enregistrement d'un envoi avec calcul automatique des frais
- Génération d'un code de retrait unique (alphanumérique 8 caractères)
- Paiement d'un transfert par saisie du code de retrait
- Vérification d'identité du bénéficiaire (CIN/passeport)
- Gestion de caisse : solde temps réel, clôture journalière

### Espace Client (Self-Service)
- Inscription et authentification sécurisée (email + mot de passe + 2FA SMS)
- Tableau de bord : transferts récents, statuts en temps réel
- Historique complet avec filtres (date, montant, statut, corridor)
- Notifications push/email à chaque changement de statut

### Fonctionnalités Innovantes
- **Module KYC/AML** : vérification automatisée des pièces d'identité, contrôle OFAC fictif, déclarations de soupçon automatiques
- **Chatbot intelligent** : support FAQ multilingue (FR/EN/AR), intégration GPT/Dialogflow, escalade vers un agent humain
- **Mobile Money** : envoi vers Orange Money, Wave, M-Pesa (simulation), réconciliation automatique

---

## Stack technologique

| Couche | Technologie |
|---|---|
| Backend | Spring MVC 6 + Spring Security 6 (sans Spring Boot) |
| ORM | Spring Data JPA + Hibernate |
| Base de données | PostgreSQL |
| API Docs | SpringDoc OpenAPI 3 (Swagger UI) |
| Frontend | Angular 17+ + Chart.js |
| Conteneurisation | Docker + Docker Compose |
| CI/CD | GitHub Actions |
| Tests | JUnit 5 + Mockito + MockMvc |
| Monitoring | Spring Actuator + Micrometer |

---

## Architecture

L'application suit une **architecture monolithique en couches** stricte :

```
┌─────────────────────────────────────────┐
│         Couche Présentation             │
│   @RestController + DTOs d'entrée/sortie│
├─────────────────────────────────────────┤
│           Couche Service                │
│  Logique métier + @Transactional        │
├─────────────────────────────────────────┤
│         Couche Persistance              │
│  Repositories Spring Data JPA + Entités │
├─────────────────────────────────────────┤
│        Couche Infrastructure            │
│  Security, Swagger, CORS, Exceptions    │
└─────────────────────────────────────────┘
```

> ⚠️ Architecture microservices interdite — application monolithique obligatoire.

---

## Prérequis

Avant de lancer le projet, assurez-vous d'avoir installé :

- [Java 17+](https://adoptium.net/)
- [Maven 3.9+](https://maven.apache.org/)
- [Node.js 20+ & npm](https://nodejs.org/)
- [Docker & Docker Compose](https://www.docker.com/)
- [Git](https://git-scm.com/)

---

## Installation et lancement

### 1. Cloner le dépôt

```bash
git clone https://github.com/<votre-org>/okane-transfer.git
cd okane-transfer
```

### 2. Configurer les variables d'environnement

Copiez le fichier d'exemple et renseignez vos valeurs :

```bash
cp .env.example .env
```

> ⚠️ Ne commitez jamais le fichier `.env` ni `application.properties` contenant des secrets.

### 3. Lancer avec Docker Compose (recommandé)

```bash
docker compose up --build
```

Les services démarrés :
- **Backend** → `http://localhost:8080`
- **Frontend Angular** → `http://localhost:4200`
- **PostgreSQL** → port `5432`
- **Nginx** → `http://localhost:80`

### 4. Lancement manuel (développement)

**Backend :**
```bash
cd backend
mvn clean install
mvn tomcat7:run
```

**Frontend :**
```bash
cd frontend
npm install
ng serve
```

---

## Variables d'environnement

| Variable | Description | Exemple |
|---|---|---|
| `DB_HOST` | Hôte PostgreSQL | `localhost` |
| `DB_PORT` | Port PostgreSQL | `5432` |
| `DB_NAME` | Nom de la base | `okane_db` |
| `DB_USER` | Utilisateur DB | `okane_user` |
| `DB_PASSWORD` | Mot de passe DB | `*****` |
| `JWT_SECRET` | Clé secrète JWT | `change_me_in_prod` |
| `JWT_EXPIRATION` | Durée access token (ms) | `3600000` |
| `JWT_REFRESH_EXPIRATION` | Durée refresh token (ms) | `604800000` |
| `SMS_API_KEY` | Clé API SMS (2FA) | `*****` |
| `AES_SECRET_KEY` | Clé AES-256 (chiffrement CIN) | `*****` |
| `OPENAI_API_KEY` | Clé GPT pour le chatbot | `*****` |

---

## API & Documentation Swagger

Une fois le backend démarré, la documentation interactive est disponible à :

```
http://localhost:8080/swagger-ui/index.html
```

Les principaux groupes d'endpoints :

| Groupe | Préfixe |
|---|---|
| Authentification | `/api/auth/**` |
| Transferts | `/api/transfers/**` |
| Agences | `/api/agencies/**` |
| Devises & Taux | `/api/currencies/**` |
| Utilisateurs | `/api/users/**` |
| Rapports | `/api/reports/**` |
| KYC/AML | `/api/compliance/**` |

---

## Tests

Lancer tous les tests :

```bash
mvn test
```

Générer le rapport de couverture (JaCoCo) :

```bash
mvn verify
# Rapport disponible dans : target/site/jacoco/index.html
```

> Objectif de couverture : **> 70%**

Types de tests inclus :
- **Tests unitaires** : services et utilitaires (JUnit 5 + Mockito)
- **Tests d'intégration** : couche persistance, repositories
- **Tests API** : endpoints REST (MockMvc)

---

## Structure du projet

```
okane-transfer/
├── backend/
│   └── src/
│       ├── main/
│       │   ├── java/com/okane/
│       │   │   ├── controller/       # @RestController (Présentation)
│       │   │   ├── service/          # Logique métier (Service)
│       │   │   ├── repository/       # Spring Data JPA (Persistance)
│       │   │   ├── entity/           # Entités JPA
│       │   │   ├── dto/              # DTOs entrée/sortie
│       │   │   ├── security/         # JWT, Spring Security
│       │   │   ├── config/           # Swagger, CORS, AppConfig
│       │   │   └── exception/        # Gestion globale des erreurs
│       │   └── resources/
│       │       └── application.properties.example
│       └── test/
├── frontend/
│   └── src/
│       ├── app/
│       │   ├── core/                 # Guards, intercepteurs, services auth
│       │   ├── features/             # Modules par fonctionnalité
│       │   └── shared/               # Composants réutilisables
│       └── environments/
├── docker/
│   ├── Dockerfile.backend
│   ├── Dockerfile.frontend
│   └── nginx.conf
├── docker-compose.yml
├── .env.example
├── .gitignore
└── README.md
```

---

## Sécurité

- **JWT** : access token (1h) + refresh token (7 jours), sessions stateless
- **2FA** : OTP par SMS pour les opérations sensibles
- **BCrypt** : hachage des mots de passe (facteur 12)
- **AES-256** : chiffrement des numéros de pièces d'identité en base
- **Rate limiting** : max 5 tentatives d'authentification / 10 minutes
- **Journal d'audit** : toutes les actions sensibles tracées dans `JournalAudit`
- **Validation** : Jakarta Validation + contraintes custom sur toutes les entrées
- **Secrets** : gérés exclusivement via variables d'environnement — jamais dans Git

---

> Projet académique 2025–2026 — Données fictives uniquement — Aucune donnée réelle.
