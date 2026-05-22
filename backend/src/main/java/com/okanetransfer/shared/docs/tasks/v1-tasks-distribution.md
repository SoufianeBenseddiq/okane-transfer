# Okane Transfer — Répartition du travail (6 membres)

> Objectif : zéro conflit Git entre membres. Chaque membre possède ses fichiers de bout en bout.
> Ordre de développement strict : les phases doivent être respectées car chaque phase dépend de la précédente.

---

## Règle d'or

```
Personne ne touche aux fichiers d'un autre membre.
Si tu as besoin d'une fonctionnalité d'un autre module → tu utilises son interface (IXxxService).
Si l'interface n'est pas encore codée → tu crées un mock temporaire dans ton propre dossier.
```

---

## Vue d'ensemble de la répartition

| Membre     | Domaine | Entités concernées |
|------------|---------|-------------------|
| Soufiane   | User + Auth + Security | `Utilisateur`, `Administrateur`, `Agent`, `Manager`, `Client`, `PieceIdentite` |
| Btissam    | Agence + Caisse | `Agence`, `CaisseOperation`, `ClotureCaisse` |
| Siham      | Devise + Corridor + Frais | `Devise`, `Corridor`, `GrilleTarifaire`, `HistoriqueTaux` |
| Ayman      | Transfert | `Transfert`, `Expediteur`, `Beneficiaire` |
| Tabati     | AML + Audit | `DeclarationSoupcon`, `JournalAudit`, `ListeOFAC`, `RegleAML` |
| Abdelghani | Notification + MobileMoney + Rapport | `Notification`, `TransfertMobileMoney` |

---

## Phase 0 — Travail commun (TOUS ensemble — Jour 1)

> Durée estimée : 2-3 heures. Tout le monde présent.
> Ces fichiers sont créés une seule fois, partagés par tous, et ne sont plus modifiés ensuite.

### Fichiers à créer ensemble

```
src/main/java/com/okanetransfer/shared/

├── enums/
│   ├── RoleUtilisateur.java        → ROLE_ADMIN, ROLE_MANAGER, ROLE_AGENT, ROLE_CLIENT
│   ├── StatutTransfert.java        → EN_ATTENTE, PAYE, ANNULE, EXPIRE, BLOQUE
│   ├── TypePiece.java              → CIN, PASSEPORT, CARTE_SEJOUR, PERMIS
│   ├── TypeOperation.java          → ENVOI, RETRAIT, OUVERTURE, AJUSTEMENT, CLOTURE
│   └── TypeNotification.java       → SMS, EMAIL, PUSH

├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── ErrorResponse.java
│   ├── TransfertNotFoundException.java
│   ├── PlafondDepasseException.java
│   ├── OFACViolationException.java
│   ├── CorridorInactifException.java
│   ├── CodeRetraitInvalideException.java
│   └── AccesRefuseException.java

└── util/
    ├── CodeGenerator.java
    ├── CryptoUtil.java
    ├── CryptoConverter.java
    └── DateUtil.java
```

### Signatures des interfaces à définir ensemble

Avant de se séparer, définir les signatures complètes de :

```java
IUserService          IAuthService
IAgenceService        ICaisseService
IDeviseService        IFraisService
ITransfertService
IAmlService           IAuditService
INotificationService  IRapportService    IMobileMoneyService
```

> Une fois les signatures validées par tous → chacun part travailler sur son module.

---

## Phase 1 — Fondations (Membres 1, 2, 3 en parallèle)

> Prérequis : Phase 0 terminée.
> Membres 4, 5, 6 attendent la fin de cette phase avant de commencer.

---

### Membre 1 — User + Auth + Security

**Pourquoi en premier :** toutes les autres entités référencent `Utilisateur`, `Agent`, `Client`, `Manager`.

#### Fichiers à créer (dans l'ordre)

```
entity/user/
├── Utilisateur.java          ← classe mère abstraite @Inheritance(JOINED)
├── Administrateur.java
├── Manager.java
├── Agent.java
├── Client.java
└── PieceIdentite.java        ← @ManyToOne vers Client

repository/user/
└── UtilisateurRepository.java

service/user/
├── IAuthService.java         ← login(), verifyOtp(), refreshToken(), logout()
├── IUserService.java         ← creer(), desactiver(), getById(), demanderEffacement()
└── impl/
    ├── AuthServiceImpl.java
    └── UserServiceImpl.java

dto/user/
├── request/
│   ├── LoginRequest.java
│   ├── OtpRequest.java
│   ├── RefreshTokenRequest.java
│   ├── CreateUserRequest.java
│   └── PieceIdentiteRequest.java
└── response/
    ├── TokenResponse.java
    ├── UserResponse.java
    └── PieceIdentiteResponse.java

converter/
└── UserConverter.java

controller/
├── AuthController.java       ← /api/auth/**  (public)
└── UserController.java       ← /api/users/** (auth requis)

shared/config/
├── AppConfig.java
├── WebConfig.java
├── SecurityConfig.java
├── JpaConfig.java
└── SwaggerConfig.java

shared/security/
├── JwtTokenProvider.java
├── JwtAuthFilter.java
├── OtpService.java
└── UserDetailsServiceImpl.java
```

#### Contrat fourni aux autres membres

```java
// ce que les autres peuvent appeler
IUserService.getAgentById(Long id) → Agent
IUserService.getClientByTelephone(String tel) → Optional<Client>
IUserService.getClientById(Long id) → Client
IAuthService.getCurrentUser(Authentication auth) → Utilisateur
```

---

### Membre 2 — Agence + Caisse

**Pourquoi en premier :** `Transfert` (Membre 4) a besoin d'`Agence`. `CaisseOperation` dépend d'`Agent` (Membre 1).

> Attendre que Membre 1 ait commité `Agent.java` avant de coder `CaisseOperation`.

#### Fichiers à créer (dans l'ordre)

```
entity/agence/
└── Agence.java               ← @OneToOne Manager, @OneToMany Agent

entity/caisse/
├── CaisseOperation.java      ← @ManyToOne Agent
└── ClotureCaisse.java        ← @ManyToOne Agent

repository/agence/
└── AgenceRepository.java

repository/caisse/
├── CaisseOperationRepository.java
└── ClotureCaisseRepository.java

service/agence/
├── IAgenceService.java       ← verifierPlafond(), incrementerMontant(), suspendre()
└── impl/
    └── AgenceServiceImpl.java

service/caisse/
├── ICaisseService.java       ← getSolde(), enregistrerMouvement(), cloturerCaisse()
└── impl/
    └── CaisseServiceImpl.java

dto/agence/
├── request/
│   └── AgenceRequest.java
└── response/
    ├── AgenceResponse.java
    └── AgencePerformanceResponse.java

dto/caisse/
├── request/
│   └── ClotureRequest.java
└── response/
    ├── CaisseOperationResponse.java
    └── ClotureCaisseResponse.java

converter/
├── AgenceConverter.java
└── CaisseConverter.java

controller/
├── AgenceController.java     ← /api/agences/**
└── CaisseController.java     ← /api/caisse/**
```

#### Contrat fourni aux autres membres

```java
IAgenceService.verifierPlafond(Long agenceId, BigDecimal montant)
IAgenceService.incrementerMontantTraite(Long agenceId, BigDecimal montant)
IAgenceService.getById(Long agenceId) → Agence
ICaisseService.enregistrerMouvement(Long agentId, TypeOperation, BigDecimal)
```

---

### Membre 3 — Devise + Corridor + Frais

**Pourquoi en premier :** `Transfert` (Membre 4) dépend de `Corridor` et `GrilleTarifaire`.

#### Fichiers à créer (dans l'ordre)

```
entity/devise/
├── Devise.java
├── Corridor.java             ← @ManyToOne Devise x2
├── GrilleTarifaire.java      ← @ManyToOne Corridor
└── HistoriqueTaux.java       ← @ManyToOne Devise

repository/devise/
├── DeviseRepository.java
├── CorridorRepository.java
├── GrilleTarifaireRepository.java
└── HistoriqueTauxRepository.java

service/devise/
├── IDeviseService.java       ← CRUD devises, updateTaux(), activerCorridor()
├── IFraisService.java        ← calculerFrais(montant, corridorId) → FraisResult
└── impl/
    ├── DeviseServiceImpl.java
    └── FraisServiceImpl.java

dto/devise/
├── request/
│   ├── DeviseRequest.java
│   └── CorridorRequest.java
└── response/
    ├── DeviseResponse.java
    ├── CorridorResponse.java
    └── FraisResult.java      ← montantFrais, partAgence, partCentrale, montantRecu

converter/
├── DeviseConverter.java
└── CorridorConverter.java

controller/
├── DeviseController.java     ← /api/admin/devises/**
└── CorridorController.java   ← /api/admin/corridors/**
```

#### Contrat fourni aux autres membres

```java
IDeviseService.getTauxConversion(String src, String dest) → BigDecimal
IFraisService.calculerFrais(BigDecimal montant, Long corridorId) → FraisResult
IDeviseService.getCorridorActif(String src, String dest) → Corridor
```

---

## Phase 2 — Cœur métier (Membre 4 + Membre 5 en parallèle)

> Prérequis : Phase 1 entièrement terminée et commitée.

---

### Membre 4 — Transfert

**Dépend de :** `Agent` (M1), `Client` (M1), `PieceIdentite` (M1), `Agence` (M2), `Corridor` (M3), `GrilleTarifaire` (M3).

#### Fichiers à créer (dans l'ordre)

```
entity/transfert/
├── Expediteur.java           ← @ManyToOne Client, @ManyToOne PieceIdentite
├── Beneficiaire.java
└── Transfert.java            ← @ManyToOne Expediteur, Client(nullable),
                                 Beneficiaire, Agent, Agence x2,
                                 Corridor, GrilleTarifaire

repository/transfert/
├── TransfertRepository.java
├── ExpediteurRepository.java
└── BeneficiaireRepository.java

service/transfert/
├── ITransfertService.java
└── impl/
    └── TransfertServiceImpl.java
        ← injecte : IAgenceService, IFraisService,
                    IAmlService (mock si M5 pas fini),
                    INotificationService (mock si M6 pas fini)

dto/transfert/
├── request/
│   ├── TransfertRequest.java
│   └── PaiementRequest.java
└── response/
    └── TransfertResponse.java

converter/
└── TransfertConverter.java

controller/
└── TransfertController.java  ← /api/transferts/**
```

#### Mock temporaire si Membre 5 pas encore prêt

```java
// à créer dans ton propre dossier — PAS dans le dossier de M5
// src/main/java/com/okanetransfer/service/aml/impl/AmlServiceMock.java
@Service @Profile("dev")
public class AmlServiceMock implements IAmlService {
    public void verifierOFAC(String nom, String prenom) { }
    public void evaluerTransfert(Transfert t) { }
}
```

---

### Membre 5 — AML + Audit

**Dépend de :** `Transfert` (M4), `Client` (M1), `Utilisateur` (M1).

> Commencer par `ListeOFAC` et `RegleAML` qui n'ont pas de dépendances.
> Coder `IAmlService` et `IAuditService` en premier pour débloquer Membre 4.

#### Fichiers à créer (dans l'ordre)

```
entity/aml/
├── ListeOFAC.java            ← pas de dépendances externes
├── RegleAML.java             ← pas de dépendances externes
├── DeclarationSoupcon.java   ← @ManyToOne Transfert, Client, RegleAML
└── JournalAudit.java         ← @ManyToOne Utilisateur

repository/aml/
├── ListeOFACRepository.java
├── RegleAMLRepository.java
├── DeclarationSoupconRepository.java
└── JournalAuditRepository.java

service/aml/
├── IAmlService.java          ← verifierOFAC(), evaluerTransfert(), getDeclarations()
├── IAuditService.java        ← log(acteur, action, entite, id, avant, apres)
└── impl/
    ├── AmlServiceImpl.java
    └── AuditServiceImpl.java

dto/aml/
├── request/
│   └── OFACRequest.java
└── response/
    ├── DeclarationResponse.java
    ├── AuditResponse.java
    └── AmlDashboardResponse.java

converter/
└── AmlConverter.java

controller/
└── AmlController.java        ← /api/aml/** (ADMIN only)
```

---

## Phase 3 — Fonctionnalités avancées (Membre 6)

> Prérequis : Phase 2 entièrement terminée.

---

### Membre 6 — Notification + MobileMoney + Rapport

**Dépend de :** `Transfert` (M4), `Utilisateur` (M1), `Agence` (M2).

#### Fichiers à créer (dans l'ordre)

```
entity/notification/
└── Notification.java         ← @ManyToOne Utilisateur, Transfert

entity/mobilemoney/
└── TransfertMobileMoney.java ← @OneToOne Transfert

repository/notification/
└── NotificationRepository.java

repository/mobilemoney/
└── MobileMoneyRepository.java

service/notification/
├── INotificationService.java ← notifierCreation(), notifierChangementStatut(), envoyerSMS()
└── impl/
    └── NotificationServiceImpl.java

service/rapport/
├── IRapportService.java      ← getRapportJournalier(), getRapportMensuel(), getCommissions()
└── impl/
    └── RapportServiceImpl.java

service/mobilemoney/
├── IMobileMoneyService.java  ← envoyer(), reconcilier(), getStatut()
└── impl/
    └── MobileMoneyServiceImpl.java

dto/notification/
└── response/
    └── NotificationResponse.java

dto/rapport/
└── response/
    ├── RapportJournalierResponse.java
    ├── RapportMensuelResponse.java
    └── CommissionResponse.java

dto/mobilemoney/
├── request/
│   └── MobileMoneyRequest.java
└── response/
    └── MobileMoneyResponse.java

converter/
├── NotificationConverter.java
└── MobileMoneyConverter.java

controller/
├── NotificationController.java   ← /api/notifications/**
├── RapportController.java        ← /api/admin/rapports/**
└── MobileMoneyController.java    ← /api/mobile-money/**
```

---

## Récapitulatif des phases

```
PHASE 0 — Tous ensemble
  └── shared/ (enums, exceptions, utils) + signatures des interfaces
       ↓
PHASE 1 — Membres 1, 2, 3 en parallèle
  ├── M1 : entity/user + security + config
  ├── M2 : entity/agence + entity/caisse
  └── M3 : entity/devise + corridor + frais
       ↓
PHASE 2 — Membres 4, 5 en parallèle
  ├── M4 : entity/transfert (dépend de M1, M2, M3)
  └── M5 : entity/aml (dépend de M1, M4)
       ↓
PHASE 3 — Membre 6
  └── notification + mobilemoney + rapport (dépend de tout)
```

---

## Branches Git recommandées

```bash
main
├── develop                          ← branche d'intégration
│   ├── feature/phase0-shared        ← tous ensemble
│   ├── feature/m1-user-auth         ← Membre 1
│   ├── feature/m2-agence-caisse     ← Membre 2
│   ├── feature/m3-devise-frais      ← Membre 3
│   ├── feature/m4-transfert         ← Membre 4
│   ├── feature/m5-aml-audit         ← Membre 5
│   └── feature/m6-notif-rapport     ← Membre 6
```

### Règles Git

```
✅ Chaque membre merge develop → sa branche au début de chaque session
✅ Pull Request obligatoire pour merger dans develop
✅ Au moins 1 autre membre doit review la PR
✅ Minimum 3 commits par semaine par membre
❌ Jamais commiter directement sur develop ou main
❌ Jamais modifier les fichiers d'un autre membre sans accord
```

---

## Dépendances entre membres (résumé)

```
M1 (User)
  ↑ utilisé par M2 (Agent → Agence)
  ↑ utilisé par M4 (Client, Agent → Transfert, Expediteur)
  ↑ utilisé par M5 (Utilisateur → JournalAudit)
  ↑ utilisé par M6 (Utilisateur → Notification)

M2 (Agence)
  ↑ utilisé par M4 (Agence → Transfert)
  ↑ utilisé par M6 (Agence → Rapport)

M3 (Devise)
  ↑ utilisé par M4 (Corridor, GrilleTarifaire → Transfert)

M4 (Transfert)
  ↑ utilisé par M5 (Transfert → DeclarationSoupcon)
  ↑ utilisé par M6 (Transfert → Notification, TransfertMobileMoney)

M5 (AML)
  ↑ utilisé par M4 (IAmlService.verifierOFAC dans TransfertServiceImpl)

M6 (Notification)
  ↑ utilisé par M4 (INotificationService dans TransfertServiceImpl)
```

---

## Checklist avant de merger dans develop

Chaque membre vérifie avant sa PR :

```
□ Toutes les entités ont leurs getters/setters
□ Toutes les entités ont @Entity, @Table, @Id
□ Tous les services ont une interface + une implémentation
□ Tous les controllers ont @PreAuthorize sur chaque endpoint
□ Tous les DTOs request ont les annotations @Valid (@NotBlank, @NotNull...)
□ Aucun secret dans le code (mot de passe, clé API)
□ Aucune entité JPA retournée directement dans un controller
□ Au moins un test unitaire par service
```