# Okane Transfer — Structure Backend Complète

> Application Spring MVC 6 **sans Spring Boot** — Architecture en couches (approche B : par couche, modules à l'intérieur)

---

## Table des matières

1. [Vue d'ensemble](#1-vue-densemble)
2. [Arborescence complète](#2-arborescence-complète)
3. [Explication de chaque couche](#3-explication-de-chaque-couche)
4. [Explication de chaque module métier](#4-explication-de-chaque-module-métier)
5. [Explication de shared/](#5-explication-de-shared)
6. [Règles de nommage](#6-règles-de-nommage)
7. [Répartition des équipes](#7-répartition-des-équipes)
8. [Dépendances pom.xml](#8-dépendances-pomxml)

---

## 1. Vue d'ensemble

```
Approche B : par couche globale, modules à l'intérieur de chaque couche

entity/
  transfert/   devise/   agence/   user/   caisse/   aml/   ...
repository/
  transfert/   devise/   agence/   ...
service/
  transfert/   devise/   agence/   ...
dto/
  transfert/   devise/   agence/   ...
converter/
  (un fichier par module, pas de sous-dossiers)
controller/
  (un fichier par module, pas de sous-dossiers)
shared/
  config/   security/   enum/   exception/   util/
```

**Flux de données strict (sens unique) :**
```
HTTP Request
    ↓
Controller          reçoit DTO Request, retourne DTO Response
    ↓
Service (impl)      logique métier, @Transactional
    ↓
Repository          accès base de données
    ↓
Entity              objet JPA mappé en table SQL
```

---

## 2. Arborescence complète

```
okane-transfer/
├── pom.xml
├── Dockerfile
├── docker-compose.yml
├── .gitignore
├── README.md
│
├── .github/
│   └── workflows/
│       ├── ci.yml                              ← build + tests à chaque push
│       └── cd.yml                              ← déploiement sur merge main
│
└── src/
    │
    ├── test/
    │   └── java/ma/okane/
    │       ├── transfert/
    │       │   ├── TransfertServiceTest.java
    │       │   ├── TransfertControllerTest.java
    │       │   └── TransfertRepositoryTest.java
    │       ├── devise/
    │       │   ├── DeviseServiceTest.java
    │       │   └── FraisServiceTest.java
    │       ├── agence/
    │       │   ├── AgenceServiceTest.java
    │       │   └── AgenceControllerTest.java
    │       ├── user/
    │       │   ├── AuthServiceTest.java
    │       │   └── UserServiceTest.java
    │       ├── caisse/
    │       │   ├── CaisseServiceTest.java
    │       │   └── CaisseControllerTest.java
    │       ├── aml/
    │       │   ├── AmlServiceTest.java
    │       │   └── OFACServiceTest.java
    │       └── shared/
    │           ├── JwtTokenProviderTest.java
    │           └── CodeGeneratorTest.java
    │
    └── main/
        │
        ├── resources/
        │   ├── application.properties          ← références aux variables d'environnement
        │   ├── application-dev.properties      ← config développement local
        │   ├── application-prod.properties     ← config production
        │   └── db/
        │       └── migration/                  ← scripts Flyway versionnés
        │           ├── V1__create_users.sql
        │           ├── V2__create_agences.sql
        │           ├── V3__create_transferts.sql
        │           ├── V4__create_devises.sql
        │           ├── V5__create_caisse.sql
        │           ├── V6__create_aml.sql
        │           ├── V7__create_notifications.sql
        │           ├── V8__create_mobile_money.sql
        │           ├── V9__insert_devises_initiales.sql
        │           ├── V10__insert_corridors_initiaux.sql
        │           └── V11__insert_regles_aml.sql
        │
        └── java/ma/okane/
            │
            ├── entity/                         ← COUCHE 1 : entités JPA (@Entity)
            │   │
            │   ├── transfert/
            │   │   ├── Transfert.java
            │   │   ├── Expediteur.java
            │   │   └── Beneficiaire.java
            │   │
            │   ├── devise/
            │   │   ├── Devise.java
            │   │   ├── Corridor.java
            │   │   ├── GrilleTarifaire.java
            │   │   └── HistoriqueTaux.java
            │   │
            │   ├── agence/
            │   │   └── Agence.java
            │   │
            │   ├── user/
            │   │   ├── Utilisateur.java        ← classe mère @Inheritance(JOINED)
            │   │   ├── Administrateur.java
            │   │   ├── Manager.java
            │   │   ├── Agent.java
            │   │   └── Client.java
            │   │
            │   ├── caisse/
            │   │   ├── CaisseOperation.java
            │   │   └── ClotureCaisse.java
            │   │
            │   ├── aml/
            │   │   ├── ListeOFAC.java
            │   │   ├── RegleAML.java
            │   │   ├── DeclarationSoupcon.java
            │   │   └── JournalAudit.java
            │   │
            │   ├── notification/
            │   │   └── Notification.java
            │   │
            │   └── mobilemoney/
            │       └── TransfertMobileMoney.java
            │
            ├── repository/                     ← COUCHE 2 : accès BDD (JpaRepository)
            │   │
            │   ├── transfert/
            │   │   ├── TransfertRepository.java
            │   │   ├── ExpediteurRepository.java
            │   │   └── BeneficiaireRepository.java
            │   │
            │   ├── devise/
            │   │   ├── DeviseRepository.java
            │   │   ├── CorridorRepository.java
            │   │   ├── GrilleTarifaireRepository.java
            │   │   └── HistoriqueTauxRepository.java
            │   │
            │   ├── agence/
            │   │   └── AgenceRepository.java
            │   │
            │   ├── user/
            │   │   └── UtilisateurRepository.java
            │   │
            │   ├── caisse/
            │   │   ├── CaisseOperationRepository.java
            │   │   └── ClotureCaisseRepository.java
            │   │
            │   ├── aml/
            │   │   ├── ListeOFACRepository.java
            │   │   ├── RegleAMLRepository.java
            │   │   ├── DeclarationSoupconRepository.java
            │   │   └── JournalAuditRepository.java
            │   │
            │   ├── notification/
            │   │   └── NotificationRepository.java
            │   │
            │   └── mobilemoney/
            │       └── MobileMoneyRepository.java
            │
            ├── service/                        ← COUCHE 3 : logique métier
            │   │
            │   ├── transfert/
            │   │   ├── ITransfertService.java  ← interface (contrat public)
            │   │   └── impl/
            │   │       └── TransfertServiceImpl.java
            │   │
            │   ├── devise/
            │   │   ├── IDeviseService.java
            │   │   ├── IFraisService.java
            │   │   └── impl/
            │   │       ├── DeviseServiceImpl.java
            │   │       └── FraisServiceImpl.java
            │   │
            │   ├── agence/
            │   │   ├── IAgenceService.java
            │   │   └── impl/
            │   │       └── AgenceServiceImpl.java
            │   │
            │   ├── user/
            │   │   ├── IAuthService.java
            │   │   ├── IUserService.java
            │   │   └── impl/
            │   │       ├── AuthServiceImpl.java
            │   │       └── UserServiceImpl.java
            │   │
            │   ├── caisse/
            │   │   ├── ICaisseService.java
            │   │   └── impl/
            │   │       └── CaisseServiceImpl.java
            │   │
            │   ├── aml/
            │   │   ├── IAmlService.java
            │   │   ├── IAuditService.java
            │   │   └── impl/
            │   │       ├── AmlServiceImpl.java
            │   │       └── AuditServiceImpl.java
            │   │
            │   ├── notification/
            │   │   ├── INotificationService.java
            │   │   └── impl/
            │   │       └── NotificationServiceImpl.java
            │   │
            │   ├── rapport/
            │   │   ├── IRapportService.java
            │   │   └── impl/
            │   │       └── RapportServiceImpl.java
            │   │
            │   └── mobilemoney/
            │       ├── IMobileMoneyService.java
            │       └── impl/
            │           └── MobileMoneyServiceImpl.java
            │
            ├── dto/                            ← COUCHE 4 : objets de transfert de données
            │   │
            │   ├── transfert/
            │   │   ├── request/
            │   │   │   ├── TransfertRequest.java
            │   │   │   └── PaiementRequest.java
            │   │   └── response/
            │   │       └── TransfertResponse.java
            │   │
            │   ├── devise/
            │   │   ├── request/
            │   │   │   ├── DeviseRequest.java
            │   │   │   └── CorridorRequest.java
            │   │   └── response/
            │   │       ├── DeviseResponse.java
            │   │       ├── CorridorResponse.java
            │   │       └── FraisResult.java
            │   │
            │   ├── agence/
            │   │   ├── request/
            │   │   │   └── AgenceRequest.java
            │   │   └── response/
            │   │       ├── AgenceResponse.java
            │   │       └── AgencePerformanceResponse.java
            │   │
            │   ├── user/
            │   │   ├── request/
            │   │   │   ├── LoginRequest.java
            │   │   │   ├── OtpRequest.java
            │   │   │   ├── RefreshTokenRequest.java
            │   │   │   └── CreateUserRequest.java
            │   │   └── response/
            │   │       ├── TokenResponse.java
            │   │       └── UserResponse.java
            │   │
            │   ├── caisse/
            │   │   ├── request/
            │   │   │   └── ClotureRequest.java
            │   │   └── response/
            │   │       ├── CaisseOperationResponse.java
            │   │       └── ClotureCaisseResponse.java
            │   │
            │   ├── aml/
            │   │   ├── request/
            │   │   │   └── OFACRequest.java
            │   │   └── response/
            │   │       ├── DeclarationResponse.java
            │   │       ├── AuditResponse.java
            │   │       └── AmlDashboardResponse.java
            │   │
            │   ├── notification/
            │   │   └── response/
            │   │       └── NotificationResponse.java
            │   │
            │   ├── rapport/
            │   │   └── response/
            │   │       ├── RapportJournalierResponse.java
            │   │       ├── RapportMensuelResponse.java
            │   │       └── CommissionResponse.java
            │   │
            │   └── mobilemoney/
            │       ├── request/
            │       │   └── MobileMoneyRequest.java
            │       └── response/
            │           └── MobileMoneyResponse.java
            │
            ├── converter/                      ← COUCHE 5 : mapping entité ↔ DTO
            │   ├── TransfertConverter.java
            │   ├── DeviseConverter.java
            │   ├── CorridorConverter.java
            │   ├── AgenceConverter.java
            │   ├── UserConverter.java
            │   ├── CaisseConverter.java
            │   ├── AmlConverter.java
            │   ├── NotificationConverter.java
            │   └── MobileMoneyConverter.java
            │
            ├── controller/                     ← COUCHE 6 : endpoints REST
            │   ├── AuthController.java
            │   ├── TransfertController.java
            │   ├── DeviseController.java
            │   ├── CorridorController.java
            │   ├── AgenceController.java
            │   ├── UserController.java
            │   ├── CaisseController.java
            │   ├── AmlController.java
            │   ├── NotificationController.java
            │   ├── RapportController.java
            │   └── MobileMoneyController.java
            │
            └── shared/                         ← code transversal (tous modules)
                ├── config/
                │   ├── AppConfig.java
                │   ├── WebConfig.java
                │   ├── SecurityConfig.java
                │   ├── JpaConfig.java
                │   └── SwaggerConfig.java
                ├── security/
                │   ├── JwtTokenProvider.java
                │   ├── JwtAuthFilter.java
                │   ├── OtpService.java
                │   └── UserDetailsServiceImpl.java
                ├── enum/
                │   ├── StatutTransfertEnum.java
                │   ├── RoleEnum.java
                │   ├── TypeOperationEnum.java
                │   ├── TypeNotificationEnum.java
                │   └── TypePieceEnum.java
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

---

## 3. Explication de chaque couche

### `entity/`

Contient les classes Java annotées `@Entity`. Chaque classe correspond exactement à une table en base de données. Hibernate se charge du mapping objet-relationnel automatiquement.

**Règles :**
- Uniquement des champs, des relations JPA et des annotations de mapping
- Aucune logique métier dans les entités
- Les champs sensibles (numéro de pièce d'identité) sont chiffrés via `@Convert(converter = CryptoConverter.class)` avant stockage

**Exemple :**
```java
@Entity
@Table(name = "transferts")
public class Transfert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String codeRetrait;           // ex: "K7X2-M9QA"

    @Column(unique = true, nullable = false)
    private String numeroReference;       // ex: "TRF-20260517-00892"

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montantEnvoye;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montantRecu;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal frais;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutTransfertEnum statut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expediteur_id")
    private Expediteur expediteur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beneficiaire_id")
    private Beneficiaire beneficiaire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private Agent agentSaisie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agence_envoi_id")
    private Agence agenceEnvoi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agence_retrait_id")
    private Agence agenceRetrait;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "corridor_id")
    private Corridor corridor;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creeLe;

    private LocalDateTime payeLe;

    @PrePersist
    public void prePersist() {
        this.creeLe = LocalDateTime.now();
    }
}
```

---

### `repository/`

Contient les interfaces qui étendent `JpaRepository<Entite, Long>`. Spring Data JPA génère automatiquement toutes les requêtes CRUD de base. On y ajoute uniquement les requêtes spécifiques au métier.

**Règles :**
- Uniquement des interfaces, jamais de classes concrètes
- Les requêtes custom s'écrivent en JPQL avec `@Query` ou par convention de nommage Spring Data
- Un seul repository par entité

**Exemple :**
```java
public interface TransfertRepository extends JpaRepository<Transfert, Long> {

    // recherche par code de retrait (paiement au guichet)
    Optional<Transfert> findByCodeRetrait(String codeRetrait);

    // recherche par référence (suivi client)
    Optional<Transfert> findByNumeroReference(String reference);

    // historique d'un client avec pagination
    @Query("SELECT t FROM Transfert t WHERE t.expediteur.client = :client ORDER BY t.creeLe DESC")
    Page<Transfert> findByClient(@Param("client") Client client, Pageable pageable);

    // volume d'une agence sur une période (rapport)
    @Query("SELECT COALESCE(SUM(t.montantEnvoye), 0) FROM Transfert t " +
           "WHERE t.agenceEnvoi = :agence AND t.creeLe BETWEEN :debut AND :fin")
    BigDecimal sumMontantByAgenceAndPeriode(
        @Param("agence") Agence agence,
        @Param("debut") LocalDateTime debut,
        @Param("fin") LocalDateTime fin
    );

    // détection AML : compter transactions d'un expéditeur dans une fenêtre de temps
    @Query("SELECT COUNT(t) FROM Transfert t " +
           "WHERE t.expediteur.telephone = :telephone " +
           "AND t.creeLe >= :depuis")
    long countByExpediteurTelephoneAndCreeLeSince(
        @Param("telephone") String telephone,
        @Param("depuis") LocalDateTime depuis
    );

    // transferts par statut pour une agence (tableau de bord manager)
    List<Transfert> findByAgenceEnvoiAndStatut(Agence agence, StatutTransfertEnum statut);
}
```

---

### `service/`

Couche métier. Chaque module a une interface publique (`IXxxService`) et une implémentation concrète dans `impl/`.

**Règle fondamentale :** les autres couches et modules injectent **uniquement l'interface**, jamais l'implémentation. Cela permet à chaque équipe de travailler en parallèle.

**Interface (contrat public) :**
```java
public interface ITransfertService {

    TransfertResponse creerTransfert(TransfertRequest request, String agentEmail);

    TransfertResponse payerTransfert(PaiementRequest request, String agentEmail);

    TransfertResponse getByReference(String reference);

    TransfertResponse getByCodeRetrait(String code);

    Page<TransfertResponse> getHistoriqueClient(String clientEmail, Pageable pageable);

    Page<TransfertResponse> getByAgence(Long agenceId, Pageable pageable);

    void annuler(Long id, String motif, String acteurEmail);
}
```

**Implémentation :**
```java
@Service
@Transactional
public class TransfertServiceImpl implements ITransfertService {

    private final TransfertRepository transfertRepository;
    private final IAgenceService agenceService;         // interface, pas l'impl
    private final IFraisService fraisService;           // interface, pas l'impl
    private final IAmlService amlService;               // interface, pas l'impl
    private final INotificationService notifService;    // interface, pas l'impl
    private final TransfertConverter converter;
    private final CodeGenerator codeGenerator;

    // injection par constructeur (meilleure pratique)
    public TransfertServiceImpl(TransfertRepository transfertRepository,
                                 IAgenceService agenceService, ...) {
        this.transfertRepository = transfertRepository;
        this.agenceService = agenceService;
        ...
    }

    @Override
    public TransfertResponse creerTransfert(TransfertRequest req, String agentEmail) {
        // 1. vérifier que le corridor est actif
        Corridor corridor = corridorRepository.findByDevises(
            req.getDeviseSource(), req.getDeviseDestination()
        ).orElseThrow(() -> new CorridorInactifException(...));

        // 2. calculer les frais
        FraisResult frais = fraisService.calculerFrais(req.getMontant(), corridor.getId());

        // 3. vérifier la liste OFAC
        amlService.verifierOFAC(req.getNomBeneficiaire(), req.getPrenomBeneficiaire());

        // 4. vérifier le plafond de l'agence
        agenceService.verifierPlafond(agent.getAgence().getId(), req.getMontant());

        // 5. construire et sauvegarder le transfert
        Transfert transfert = new Transfert();
        transfert.setCodeRetrait(codeGenerator.generateCodeRetrait());
        transfert.setNumeroReference(codeGenerator.generateReference());
        transfert.setMontantEnvoye(req.getMontant());
        transfert.setFrais(frais.getMontantTotal());
        transfert.setStatut(StatutTransfertEnum.EN_ATTENTE);
        ...
        transfert = transfertRepository.save(transfert);

        // 6. incrémenter le montant traité par l'agence
        agenceService.incrementerMontantTraite(agent.getAgence().getId(), req.getMontant());

        // 7. notifier l'expéditeur
        notifService.notifierCreation(transfert);

        // 8. évaluation AML (asynchrone, ne bloque pas la réponse)
        amlService.evaluerTransfert(transfert);

        return converter.toResponse(transfert);
    }
}
```

---

### `dto/`

Les DTOs (Data Transfer Objects) sont les objets échangés entre l'API et les clients HTTP. Ils évitent d'exposer les entités JPA directement.

**`request/`** — objet reçu dans le body d'une requête HTTP. Validé avec Jakarta Validation.

```java
public class TransfertRequest {

    @NotBlank(message = "Le nom de l'expéditeur est obligatoire")
    private String nomExpediteur;

    @NotBlank(message = "Le prénom de l'expéditeur est obligatoire")
    private String prenomExpediteur;

    @NotBlank(message = "Le numéro de pièce est obligatoire")
    private String numeroPieceExpediteur;

    @NotNull(message = "Le type de pièce est obligatoire")
    private TypePieceEnum typePiece;

    @NotBlank @Pattern(regexp = "^\\+?[0-9]{8,15}$")
    private String telephoneExpediteur;

    @NotBlank
    private String nomBeneficiaire;

    @NotBlank
    private String prenomBeneficiaire;

    @NotBlank @Pattern(regexp = "^\\+?[0-9]{8,15}$")
    private String telephoneBeneficiaire;

    @NotBlank
    private String paysDestination;

    @NotNull @Positive(message = "Le montant doit être positif")
    private BigDecimal montant;

    @NotBlank
    private String deviseSource;        // ex: "MAD"

    @NotBlank
    private String deviseDestination;   // ex: "XOF"
}
```

**`response/`** — objet retourné par l'API. Ne contient que les champs à exposer.

```java
public class TransfertResponse {
    private Long id;
    private String codeRetrait;
    private String numeroReference;
    private String nomExpediteur;
    private String nomBeneficiaire;
    private String paysDestination;
    private BigDecimal montantEnvoye;
    private BigDecimal montantRecu;
    private BigDecimal frais;
    private String deviseSource;
    private String deviseDestination;
    private String statut;
    private LocalDateTime creeLe;
    private LocalDateTime payeLe;
    private String nomAgenceEnvoi;
}
```

---

### `converter/`

Chaque converter est un `@Component` Spring qui se charge de la transformation entre entités JPA et DTOs. Centralisé ici pour garder les services et contrôleurs lisibles.

**Règles :**
- Un converter par module métier
- Le converter ne contient aucune logique métier
- Le converter peut appeler d'autres converters si nécessaire (ex: `TransfertConverter` appelle `AgenceConverter`)

**Exemple :**
```java
@Component
public class TransfertConverter {

    public TransfertResponse toResponse(Transfert t) {
        if (t == null) return null;
        TransfertResponse r = new TransfertResponse();
        r.setId(t.getId());
        r.setCodeRetrait(t.getCodeRetrait());
        r.setNumeroReference(t.getNumeroReference());
        r.setNomExpediteur(t.getExpediteur().getNom()
            + " " + t.getExpediteur().getPrenom());
        r.setNomBeneficiaire(t.getBeneficiaire().getNom()
            + " " + t.getBeneficiaire().getPrenom());
        r.setMontantEnvoye(t.getMontantEnvoye());
        r.setMontantRecu(t.getMontantRecu());
        r.setFrais(t.getFrais());
        r.setStatut(t.getStatut().name());
        r.setCreeLe(t.getCreeLe());
        r.setPayeLe(t.getPayeLe());
        if (t.getAgenceEnvoi() != null) {
            r.setNomAgenceEnvoi(t.getAgenceEnvoi().getNom());
        }
        return r;
    }

    public List<TransfertResponse> toResponseList(List<Transfert> list) {
        return list.stream()
                   .map(this::toResponse)
                   .collect(Collectors.toList());
    }
}
```

---

### `controller/`

Couche présentation. Chaque contrôleur expose les endpoints REST d'un domaine fonctionnel. Son rôle est strictement limité à : recevoir la requête, appeler le service, retourner la réponse avec le bon code HTTP.

**Règles :**
- Aucune logique métier dans les contrôleurs
- Aucun accès direct aux repositories
- Les permissions sont définies avec `@PreAuthorize` sur chaque méthode
- `@Valid` est obligatoire sur chaque `@RequestBody`

**Exemple :**
```java
@RestController
@RequestMapping("/api/transferts")
public class TransfertController {

    private final ITransfertService transfertService;

    public TransfertController(ITransfertService transfertService) {
        this.transfertService = transfertService;
    }

    // créer un envoi — agent uniquement
    @PostMapping
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<TransfertResponse> creer(
            @Valid @RequestBody TransfertRequest request,
            Authentication auth) {
        TransfertResponse response = transfertService.creerTransfert(request, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // payer un transfert (retrait) — agent uniquement
    @PutMapping("/{code}/payer")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<TransfertResponse> payer(
            @PathVariable String code,
            @Valid @RequestBody PaiementRequest request,
            Authentication auth) {
        TransfertResponse response = transfertService.payerTransfert(request, auth.getName());
        return ResponseEntity.ok(response);
    }

    // consulter par référence — agent, manager, admin
    @GetMapping("/{reference}")
    @PreAuthorize("hasAnyRole('AGENT', 'MANAGER', 'ADMIN')")
    public ResponseEntity<TransfertResponse> getByRef(@PathVariable String reference) {
        return ResponseEntity.ok(transfertService.getByReference(reference));
    }

    // historique du client connecté — client uniquement
    @GetMapping("/mes-transferts")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Page<TransfertResponse>> getMesTransferts(
            Pageable pageable, Authentication auth) {
        return ResponseEntity.ok(
            transfertService.getHistoriqueClient(auth.getName(), pageable)
        );
    }

    // annuler un transfert — admin uniquement
    @DeleteMapping("/{id}/annuler")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> annuler(
            @PathVariable Long id,
            @RequestParam String motif,
            Authentication auth) {
        transfertService.annuler(id, motif, auth.getName());
        return ResponseEntity.noContent().build();
    }
}
```

---

## 4. Explication de chaque module métier

### Module `transfert/`

**Rôle :** gestion complète du cycle de vie d'un transfert d'argent.

| Fichier | Rôle |
|---------|------|
| `Transfert.java` | Entité centrale. Contient toutes les relations vers Expediteur, Beneficiaire, Agent, Agences, Corridor |
| `Expediteur.java` | Personne qui envoie les fonds. Numéro de pièce chiffré AES-256 |
| `Beneficiaire.java` | Personne qui reçoit les fonds. Champ `surListeSurveillance` mis à jour par AmlService |
| `TransfertRepository.java` | Requêtes BDD : findByCodeRetrait, findByClient, sum par agence, count pour AML |
| `ITransfertService.java` | Contrat public : creer, payer, getByReference, getHistorique, annuler |
| `TransfertServiceImpl.java` | Orchestrateur principal : vérifie corridor → calcule frais → vérifie OFAC → vérifie plafond → sauvegarde → notifie |
| `TransfertRequest.java` | DTO entrant avec toutes les infos expéditeur, bénéficiaire, montant, devises |
| `PaiementRequest.java` | DTO pour le retrait : codeRetrait + numeroPieceBeneficiaire |
| `TransfertResponse.java` | DTO sortant : infos publiques du transfert sans données sensibles |
| `TransfertConverter.java` | Transforme Transfert ↔ TransfertResponse |
| `TransfertController.java` | Endpoints : POST /creer, PUT /{code}/payer, GET /{ref}, GET /mes-transferts, DELETE /{id}/annuler |

---

### Module `devise/`

**Rôle :** gestion des devises, corridors de transfert, taux de change et calcul des frais.

| Fichier | Rôle |
|---------|------|
| `Devise.java` | Code ISO (MAD, EUR, XOF), taux vers Euro, source du taux (manuel ou API) |
| `Corridor.java` | Route de transfert entre deux devises. Peut être activé/désactivé |
| `GrilleTarifaire.java` | Tranche de montant → frais applicables + répartition agence/centrale |
| `HistoriqueTaux.java` | Trace chaque modification de taux avec l'ancien et le nouveau taux |
| `IDeviseService.java` | CRUD devises, activation corridors, mise à jour taux |
| `IFraisService.java` | calculerFrais(montant, corridorId) → FraisResult |
| `DeviseServiceImpl.java` | Sauvegarde un HistoriqueTaux avant chaque mise à jour de taux |
| `FraisServiceImpl.java` | Cherche la GrilleTarifaire dont montantMin ≤ montant ≤ montantMax pour le corridor donné |
| `DeviseController.java` | CRUD devises — ADMIN uniquement |
| `CorridorController.java` | Activer/désactiver corridors — ADMIN uniquement |

---

### Module `agence/`

**Rôle :** gestion du réseau d'agences, plafonds journaliers, performances.

| Fichier | Rôle |
|---------|------|
| `Agence.java` | Nom, adresse, pays, plafondJournalier, montantTraiteAujourdhui (remis à 0 chaque minuit) |
| `AgenceRepository.java` | findByPaysAndActive, sumMontantTraite, findTopByCommissions |
| `IAgenceService.java` | verifierPlafond, incrementerMontantTraite, suspendre, affecterAgent |
| `AgenceServiceImpl.java` | verifierPlafond() lève PlafondDepasseException si montantTraite + nouveau > plafond |
| `AgenceController.java` | GET /, POST /, PUT /{id}/suspendre (ADMIN) — GET /{id}/rapport (MANAGER) |

---

### Module `user/`

**Rôle :** gestion de tous les utilisateurs, authentification JWT avec 2FA.

| Fichier | Rôle |
|---------|------|
| `Utilisateur.java` | Classe mère @Inheritance(JOINED). Champs communs : email, motDePasseHash, telephone, role, actif |
| `Administrateur.java` | Sous-classe sans champs supplémentaires. @DiscriminatorValue("ADMIN") |
| `Manager.java` | Référence vers son agence. @DiscriminatorValue("MANAGER") |
| `Agent.java` | Référence vers son agence + soldeCaisse. @DiscriminatorValue("AGENT") |
| `Client.java` | twoFactorActive. @DiscriminatorValue("CLIENT") |
| `UtilisateurRepository.java` | findByEmail (pour Spring Security), findByAgenceAndRole |
| `IAuthService.java` | login, verifyOtp, refreshToken, logout |
| `AuthServiceImpl.java` | login() : vérifie mdp → si 2FA actif envoie OTP sinon génère tokens |
| `IUserService.java` | creerAgent, creerClient, desactiver, demanderEffacement (RGPD) |
| `AuthController.java` | POST /api/auth/login, /verify-otp, /refresh, /logout |
| `UserController.java` | GET+PUT /api/users/profil (CLIENT), POST+DELETE /api/users (ADMIN) |

---

### Module `caisse/`

**Rôle :** suivi de la caisse de chaque agent en temps réel, clôture journalière.

| Fichier | Rôle |
|---------|------|
| `CaisseOperation.java` | Un enregistrement par mouvement de caisse : type (ENVOI/RETRAIT), montant, horodatage |
| `ClotureCaisse.java` | Bilan journalier : solde théorique calculé vs solde saisi par l'agent, écart |
| `ICaisseService.java` | getSoldeActuel, enregistrerMouvement, cloturerCaisse, signalerEcart |
| `CaisseServiceImpl.java` | cloturerCaisse() : calcule le théorique, compare au saisi, lève alerte si écart |
| `CaisseController.java` | GET /solde, GET /operations, POST /cloturer, POST /ecart — AGENT uniquement |

---

### Module `aml/`

**Rôle :** conformité KYC/AML — vérification OFAC, détection de fraude, journal d'audit.

| Fichier | Rôle |
|---------|------|
| `ListeOFAC.java` | Noms blacklistés. ajoutManuel=true si ajouté par admin, false si détection automatique |
| `RegleAML.java` | Règle de détection : seuil montant, nb transactions, fenêtre de temps |
| `DeclarationSoupcon.java` | Générée automatiquement quand une règle est déclenchée. traitee=true après examen admin |
| `JournalAudit.java` | Trace toutes les actions sensibles : acteur, action, entité, état avant/après, IP |
| `IAmlService.java` | verifierOFAC (lève exception si match), evaluerTransfert, getDeclarations, traiterDeclaration |
| `AmlServiceImpl.java` | evaluerTransfert() itère sur toutes les RegleAML actives et génère des déclarations si déclenchées |
| `IAuditService.java` | log(acteur, action, entite, id, avant, apres) |
| `AuditServiceImpl.java` | Appelé manuellement dans les services pour les actions critiques ou via @Aspect AOP |
| `AmlController.java` | GET+PUT /api/aml/declarations, GET+POST+DELETE /api/aml/ofac, GET /api/aml/audit — ADMIN uniquement |

---

### Module `notification/`

**Rôle :** envoi de SMS, emails et notifications push. Découplé via interface.

| Fichier | Rôle |
|---------|------|
| `Notification.java` | Entité stockée en BDD : destinataire, message, type (SMS/EMAIL/PUSH), lue, envoyeLe |
| `INotificationService.java` | notifierCreation, notifierChangementStatut, envoyerSMS, envoyerEmail, envoyerPush |
| `NotificationServiceImpl.java` | Construit le message selon le type d'événement et délègue à la gateway SMS/SMTP simulée |
| `NotificationController.java` | GET /api/notifications (CLIENT), PUT /{id}/lire, PUT /lire-toutes |

---

### Module `rapport/`

**Rôle :** génération des rapports consolidés pour l'admin et les managers.

| Fichier | Rôle |
|---------|------|
| `IRapportService.java` | getRapportJournalier, getRapportMensuel, getPerformancesAgences, getCommissions |
| `RapportServiceImpl.java` | Agrège les données via les repositories de transfert et de caisse |
| `RapportController.java` | GET /api/admin/rapports/journalier, /mensuel, /agences, /commissions — ADMIN/MANAGER |

---

### Module `mobilemoney/`

**Rôle :** simulation d'envoi vers Orange Money, Wave, M-Pesa avec réconciliation.

| Fichier | Rôle |
|---------|------|
| `TransfertMobileMoney.java` | Extension d'un transfert classique : operateur, numeroCible, statutMobile, referenceOperateur |
| `IMobileMoneyService.java` | envoyer(transfertId, operateur, numero), reconcilier(operateur, date), getStatut(ref) |
| `MobileMoneyServiceImpl.java` | Simule l'appel API de l'opérateur, stocke la référence retournée, notifie par SMS |
| `MobileMoneyController.java` | POST /api/mobile-money/envoyer (AGENT), POST /reconcilier (ADMIN), GET /statut/{ref} |

---

## 5. Explication de `shared/`

Le dossier `shared/` contient tout le code utilisé par plusieurs modules. Si un fichier n'est utilisé que par un seul module, il va dans ce module.

### `shared/config/`

| Fichier | Rôle |
|---------|------|
| `AppConfig.java` | `@Configuration` + `@ComponentScan("ma.okane")`. Point d'entrée Spring. Déclare le `PasswordEncoder` (BCrypt force 12) |
| `WebConfig.java` | `@EnableWebMvc`. Configure `DispatcherServlet`, CORS (autorise `localhost:4200`), Jackson (dates ISO 8601), intercepteurs HTTP |
| `SecurityConfig.java` | Chaîne de filtres Spring Security 6 : CSRF désactivé, sessions STATELESS, règles par URL, installation de `JwtAuthFilter` |
| `JpaConfig.java` | `DataSource` HikariCP + `EntityManagerFactory` Hibernate + `PlatformTransactionManager`. Tout en Java sans Spring Boot |
| `SwaggerConfig.java` | SpringDoc OpenAPI 3. Génère Swagger UI sur `/swagger-ui/index.html` avec schéma d'auth JWT Bearer |

**Exemple SecurityConfig :**
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity   // active @PreAuthorize sur les méthodes
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtAuthFilter jwtFilter) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/agent/**").hasRole("AGENT")
                .requestMatchers("/api/client/**").hasRole("CLIENT")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
```

---

### `shared/security/`

| Fichier | Rôle |
|---------|------|
| `JwtTokenProvider.java` | Génère access token (1h) et refresh token (7j) signés HS256. Valide et décode les tokens |
| `JwtAuthFilter.java` | `OncePerRequestFilter`. Lit `Authorization: Bearer <token>`, valide, charge l'utilisateur dans `SecurityContext` |
| `OtpService.java` | Génère codes OTP 6 chiffres. Stocke avec TTL 5 minutes dans `ConcurrentHashMap`. Vérifie à la connexion 2FA |
| `UserDetailsServiceImpl.java` | `loadUserByUsername(email)` → charge l'utilisateur depuis la BDD pour Spring Security |

---

### `shared/enum/`

| Fichier | Valeurs |
|---------|---------|
| `StatutTransfertEnum.java` | `EN_ATTENTE`, `PAYE`, `ANNULE`, `EXPIRE`, `BLOQUE` |
| `RoleEnum.java` | `ROLE_ADMIN`, `ROLE_MANAGER`, `ROLE_AGENT`, `ROLE_CLIENT` |
| `TypeOperationEnum.java` | `ENVOI`, `RETRAIT`, `OUVERTURE`, `AJUSTEMENT`, `CLOTURE` |
| `TypeNotificationEnum.java` | `SMS`, `EMAIL`, `PUSH` |
| `TypePieceEnum.java` | `CIN`, `PASSEPORT`, `CARTE_SEJOUR`, `PERMIS` |

---

### `shared/exception/`

| Fichier | Code HTTP | Déclenchement |
|---------|-----------|---------------|
| `GlobalExceptionHandler.java` | — | `@RestControllerAdvice` : intercepte toutes les exceptions non catchées |
| `ErrorResponse.java` | — | DTO retourné en cas d'erreur : `{ "code": 404, "message": "...", "timestamp": "..." }` |
| `TransfertNotFoundException.java` | 404 | Code ou référence de transfert introuvable |
| `PlafondDepasseException.java` | 400 | Montant dépasse le plafond journalier de l'agence |
| `OFACViolationException.java` | 403 | Bénéficiaire ou expéditeur sur liste de surveillance |
| `CorridorInactifException.java` | 400 | Corridor désactivé ou inexistant pour cette paire de devises |
| `CodeRetraitInvalideException.java` | 400 | Code de retrait inexistant, expiré ou déjà utilisé |
| `AccesRefuseException.java` | 403 | Tentative d'accès hors périmètre (ex : manager d'une autre agence) |

---

### `shared/util/`

| Fichier | Rôle |
|---------|------|
| `CodeGenerator.java` | `generateCodeRetrait()` → `"K7X2-M9QA"` (8 chars alphanumériques uniques). `generateReference()` → `"TRF-20260517-00892"` |
| `CryptoUtil.java` | `encrypt(String)` / `decrypt(String)` en AES-256. Clé lue depuis `System.getenv("AES_SECRET_KEY")` |
| `CryptoConverter.java` | `@Converter` JPA qui applique `CryptoUtil` automatiquement sur les champs `@Convert(converter=CryptoConverter.class)` |
| `DateUtil.java` | `isExpire(creeLe, joursMax)`, `debutJournee(date)`, `finJournee(date)` pour les requêtes de rapports |

---

## 6. Règles de nommage

| Type | Convention | Exemple |
|------|-----------|---------|
| Entité JPA | `NomMetier.java` | `Transfert.java` |
| Repository | `NomMetierRepository.java` | `TransfertRepository.java` |
| Interface service | `INomMetierService.java` | `ITransfertService.java` |
| Implémentation service | `NomMetierServiceImpl.java` | `TransfertServiceImpl.java` |
| DTO entrant | `NomMetierRequest.java` | `TransfertRequest.java` |
| DTO sortant | `NomMetierResponse.java` | `TransfertResponse.java` |
| Converter | `NomMetierConverter.java` | `TransfertConverter.java` |
| Contrôleur | `NomMetierController.java` | `TransfertController.java` |
| Enum | `NomDescriptifEnum.java` | `StatutTransfertEnum.java` |
| Exception | `DescriptionException.java` | `PlafondDepasseException.java` |
| Test service | `NomMetierServiceTest.java` | `TransfertServiceTest.java` |
| Test contrôleur | `NomMetierControllerTest.java` | `TransfertControllerTest.java` |

---

## 7. Répartition des équipes

### Équipe 1 — Core transfert (modules `transfert/` + `devise/`)

**Ordre de développement recommandé :**
1. `Devise.java` + `DeviseRepository` + `DeviseServiceImpl` (pas de dépendances)
2. `Corridor.java` + `GrilleTarifaire.java` + `FraisServiceImpl`
3. `Expediteur.java` + `Beneficiaire.java`
4. `Transfert.java` + `TransfertServiceImpl` (dépend des interfaces des équipes 2 et 3)
5. DTOs, converters, contrôleurs

**Contrat vers les autres équipes :**
- `ITransfertService` → utilisé par équipe 3 (AML, notification)
- `IDeviseService` → utilisé par toutes les équipes pour la conversion
- `TransfertResponse` → format stable dès le départ

---

### Équipe 2 — Infrastructure (modules `user/` + `agence/` + `caisse/`)

**Ordre de développement recommandé :**
1. `Utilisateur.java` + sous-classes + `UtilisateurRepository` + `UserDetailsServiceImpl`
2. `shared/security/` : `JwtTokenProvider`, `JwtAuthFilter`, `OtpService`
3. `shared/config/SecurityConfig.java`
4. `Agence.java` + `AgenceServiceImpl` (dépend de Utilisateur)
5. `CaisseOperation.java` + `CaisseServiceImpl` (dépend de Agent)
6. `AuthServiceImpl` + contrôleurs

**Contrat vers les autres équipes :**
- `IAgenceService` → utilisé par équipe 1 (verifierPlafond)
- `IUserService` → utilisé par équipe 1 (récupérer l'agent courant)
- `UserDetails` → utilisé par shared/security

---

### Équipe 3 — Fonctionnalités avancées (modules `aml/` + `notification/` + `rapport/` + `mobilemoney/`)

**Ordre de développement recommandé :**
1. Définir les interfaces `IAmlService` et `INotificationService` en premier (équipes 1 et 2 en dépendent)
2. Créer des implémentations mock temporaires pour débloquer les équipes 1 et 2
3. `ListeOFAC.java` + `RegleAML.java` + `AmlServiceImpl`
4. `Notification.java` + `NotificationServiceImpl`
5. `JournalAudit.java` + `AuditServiceImpl`
6. `RapportServiceImpl` (dépend des repositories des équipes 1 et 2)
7. `TransfertMobileMoney.java` + `MobileMoneyServiceImpl`

**Contrat vers les autres équipes :**
- `IAmlService` → utilisé par équipe 1 (verifierOFAC, evaluerTransfert)
- `INotificationService` → utilisé par équipe 1 (notifier à chaque changement statut)

---

### Semaine 1 — travail commun obligatoire

Avant de se séparer, les 3 équipes définissent ensemble dans `shared/` :
- Tous les `enum/` (StatutTransfertEnum, RoleEnum, etc.)
- Toutes les `exception/` métier
- Les signatures de toutes les interfaces de service
- La structure de tous les DTOs response partagés

---

## 8. Dépendances pom.xml

```xml
<dependencies>

    <!-- Spring MVC — sans Spring Boot -->
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-webmvc</artifactId>
        <version>6.1.10</version>
    </dependency>

    <!-- Spring Security -->
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-web</artifactId>
        <version>6.2.5</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-config</artifactId>
        <version>6.2.5</version>
    </dependency>

    <!-- Spring Data JPA -->
    <dependency>
        <groupId>org.springframework.data</groupId>
        <artifactId>spring-data-jpa</artifactId>
        <version>3.2.7</version>
    </dependency>

    <!-- Hibernate ORM -->
    <dependency>
        <groupId>org.hibernate.orm</groupId>
        <artifactId>hibernate-core</artifactId>
        <version>6.4.9.Final</version>
    </dependency>

    <!-- PostgreSQL Driver -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <version>42.7.3</version>
    </dependency>

    <!-- HikariCP (connection pool) -->
    <dependency>
        <groupId>com.zaxxer</groupId>
        <artifactId>HikariCP</artifactId>
        <version>5.1.0</version>
    </dependency>

    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.12.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.12.5</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.12.5</version>
        <scope>runtime</scope>
    </dependency>

    <!-- Jackson (JSON) -->
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.17.1</version>
    </dependency>
    <dependency>
        <groupId>com.fasterxml.jackson.datatype</groupId>
        <artifactId>jackson-datatype-jsr310</artifactId>
        <version>2.17.1</version>
    </dependency>

    <!-- Jakarta Validation -->
    <dependency>
        <groupId>jakarta.validation</groupId>
        <artifactId>jakarta.validation-api</artifactId>
        <version>3.0.2</version>
    </dependency>
    <dependency>
        <groupId>org.hibernate.validator</groupId>
        <artifactId>hibernate-validator</artifactId>
        <version>8.0.1.Final</version>
    </dependency>

    <!-- Swagger / OpenAPI 3 -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.5.0</version>
    </dependency>

    <!-- Flyway (migrations BDD) -->
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-core</artifactId>
        <version>10.15.0</version>
    </dependency>

    <!-- Servlet API -->
    <dependency>
        <groupId>jakarta.servlet</groupId>
        <artifactId>jakarta.servlet-api</artifactId>
        <version>6.0.0</version>
        <scope>provided</scope>
    </dependency>

    <!-- Tests -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.3</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <version>5.11.0</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-test</artifactId>
        <version>6.1.10</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <version>2.2.224</version>
        <scope>test</scope>
    </dependency>

</dependencies>
```
