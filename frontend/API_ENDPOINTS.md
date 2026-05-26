# Okane Transfer Backend API

Base URL locale:

```text
http://localhost:8080
```

Si le WAR est deploye avec un contexte Tomcat:

```text
http://localhost:8080/okane-transfer-1.0-SNAPSHOT
```

Pour les routes protegees, ajouter:

```http
Authorization: Bearer <accessToken>
Content-Type: application/json
```

## Auth

| Methode | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/login` | Connexion |
| POST | `/api/auth/refresh` | Renouveler le token |
| POST | `/api/auth/logout` | Deconnexion |

Login:

```json
{
  "email": "admin@test.com",
  "motDePasse": "Password123"
}
```

Refresh:

```json
{
  "refreshToken": "REFRESH_TOKEN"
}
```

## Utilisateurs

| Methode | Endpoint | Description |
|---|---|---|
| POST | `/api/utilisateurs` | Creer un utilisateur |
| GET | `/api/utilisateurs` | Lister les utilisateurs |
| GET | `/api/utilisateurs?role=ROLE_CLIENT` | Filtrer par role |
| GET | `/api/utilisateurs/{id}` | Recuperer un utilisateur |
| PUT | `/api/utilisateurs/{id}/desactiver` | Desactiver un utilisateur |
| PUT | `/api/utilisateurs/{id}/reactiver` | Reactiver un utilisateur |
| GET | `/api/utilisateurs/me` | Profil connecte |
| PUT | `/api/utilisateurs/me` | Modifier le profil connecte |
| DELETE | `/api/utilisateurs/me` | Demander l'effacement du profil |

Create user:

```json
{
  "nom": "Alami",
  "prenom": "Youssef",
  "email": "youssef.alami@test.com",
  "motDePasse": "Password123",
  "telephone": "0611111111",
  "pays": "Maroc",
  "role": "ROLE_CLIENT"
}
```

Roles acceptes:

```text
ROLE_ADMIN, ROLE_MANAGER, ROLE_AGENT, ROLE_CLIENT
```

Update profil:

```json
{
  "nom": "Alami",
  "prenom": "Youssef",
  "telephone": "0611111111",
  "pays": "Maroc"
}
```

## Pieces D'identite

| Methode | Endpoint | Description |
|---|---|---|
| GET | `/api/utilisateurs/{clientId}/pieces` | Lister les pieces d'un client |
| POST | `/api/utilisateurs/{clientId}/pieces` | Ajouter une piece |
| PUT | `/api/utilisateurs/{clientId}/pieces/{pieceId}/principale` | Definir la piece principale |
| DELETE | `/api/utilisateurs/{clientId}/pieces/{pieceId}` | Supprimer une piece |

Create piece:

```json
{
  "numero": "P12345678",
  "type": "PASSEPORT",
  "paysEmetteur": "Maroc",
  "dateExpiration": "2030-12-31"
}
```

Types acceptes:

```text
CIN, PASSEPORT, CARTE_SEJOUR, PERMIS
```

## Expediteurs

| Methode | Endpoint | Description |
|---|---|---|
| GET | `/api/expediteurs` | Lister les expediteurs |
| GET | `/api/expediteurs/{id}` | Recuperer un expediteur |
| POST | `/api/expediteurs` | Creer un expediteur |
| PUT | `/api/expediteurs/{id}` | Modifier un expediteur |
| DELETE | `/api/expediteurs/{id}` | Supprimer un expediteur |

Create / update expediteur:

```json
{
  "clientId": 1005,
  "pieceIdentiteId": 2002
}
```

La piece doit appartenir au client.

## Beneficiaires

| Methode | Endpoint | Description |
|---|---|---|
| GET | `/api/beneficiaires` | Lister les beneficiaires |
| GET | `/api/beneficiaires/{id}` | Recuperer un beneficiaire |
| POST | `/api/beneficiaires` | Creer un beneficiaire |
| PUT | `/api/beneficiaires/{id}` | Modifier un beneficiaire |
| DELETE | `/api/beneficiaires/{id}` | Supprimer un beneficiaire |

Create / update beneficiaire:

```json
{
  "nom": "Benali",
  "prenom": "Sara",
  "telephone": "0622222222",
  "pays": "Maroc",
  "surListeSurveillance": false
}
```

## Transferts

| Methode | Endpoint | Description |
|---|---|---|
| GET | `/api/transferts` | Lister les transferts |
| GET | `/api/transferts/{id}` | Recuperer un transfert |
| POST | `/api/transferts` | Creer un transfert |
| PUT | `/api/transferts/{id}` | Modifier un transfert |
| POST | `/api/transferts/paiement` | Payer un transfert |
| PUT | `/api/transferts/{id}/annuler` | Annuler un transfert |
| GET | `/api/transferts/code/{codeRetrait}` | Chercher par code retrait |
| GET | `/api/transferts/mes-transferts?clientId={clientId}` | Historique d'un client |

Create transfert:

```json
{
  "clientId": 1005,
  "pieceIdentiteId": 2002,
  "agentId": 3001,
  "agenceEnvoiId": 4001,
  "corridorId": 5001,
  "grilleTarifaireId": 6001,
  "nomBeneficiaire": "Benali",
  "prenomBeneficiaire": "Sara",
  "telephoneBeneficiaire": "0622222222",
  "paysBeneficiaire": "Maroc",
  "montant": 1000.00
}
```

Update transfert:

```json
{
  "nomBeneficiaire": "Benali",
  "prenomBeneficiaire": "Sara",
  "telephoneBeneficiaire": "0622222222",
  "paysBeneficiaire": "Maroc",
  "montant": 1200.00
}
```

Paiement:

```json
{
  "codeRetrait": "ABC123",
  "agenceRetraitId": 4002
}
```

## Devises

| Methode | Endpoint | Description |
|---|---|---|
| GET | `/api/admin/devises` | Lister les devises |
| GET | `/api/admin/devises/{id}` | Recuperer une devise |
| GET | `/api/admin/devises/code/{code}` | Recuperer par code |
| POST | `/api/admin/devises` | Creer une devise |
| PUT | `/api/admin/devises/{id}` | Modifier une devise |
| PATCH | `/api/admin/devises/{id}/activer` | Activer une devise |
| PATCH | `/api/admin/devises/{id}/desactiver` | Desactiver une devise |

Create / update devise:

```json
{
  "code": "MAD",
  "nom": "Dirham marocain",
  "symbole": "DH",
  "tauxVersEuro": 0.092,
  "sourceTaux": "MANUEL"
}
```

## Corridors Et Frais

| Methode | Endpoint | Description |
|---|---|---|
| GET | `/api/admin/corridors` | Lister les corridors |
| GET | `/api/admin/corridors/{id}` | Recuperer un corridor |
| POST | `/api/admin/corridors` | Creer un corridor |
| PATCH | `/api/admin/corridors/{id}/activer` | Activer un corridor |
| PATCH | `/api/admin/corridors/{id}/desactiver` | Desactiver un corridor |
| GET | `/api/admin/corridors/{id}/frais?montant=1000` | Calculer les frais |
| POST | `/api/admin/corridors/grilles` | Creer une grille tarifaire |

Create corridor:

```json
{
  "deviseSourceId": 1,
  "deviseDestinationId": 2
}
```

Create grille tarifaire:

```json
{
  "corridorId": 1,
  "montantMin": 1.00,
  "montantMax": 5000.00,
  "fraisFixe": 20.00,
  "fraisPourcentage": 2.50,
  "partAgence": 10.00,
  "partCentrale": 10.00
}
```

## Agences

| Methode | Endpoint | Description |
|---|---|---|
| GET | `/api/agences/nom/{nom}` | Chercher par nom |
| GET | `/api/agences/adresse/{adresse}` | Chercher par adresse |
| GET | `/api/agences/responsable/{email}` | Chercher par responsable |
| GET | `/api/agences/actives` | Lister les agences actives |
| GET | `/api/agences/all` | Lister toutes les agences |
| POST | `/api/agences/add-one` | Creer une agence |
| PUT | `/api/agences/id/{id}` | Modifier une agence |
| DELETE | `/api/agences/id/{id}` | Supprimer une agence |

Create / update agence:

```json
{
  "nom": "Agence Centre",
  "adresse": "Avenue Mohammed V, Casablanca",
  "pays": "Maroc",
  "plafondJournalier": 100000.00
}
```

## Caisse Operations

| Methode | Endpoint | Description |
|---|---|---|
| GET | `/api/caisse-operations/agent/{email}` | Operations d'un agent |
| GET | `/api/caisse-operations/agent/{email}/solde` | Solde d'un agent |
| GET | `/api/caisse-operations/{email}/operations` | Historique agent |
| GET | `/api/caisse-operations/{agentEmail}/{debut}/{fin}/solde` | Solde theorique |
| GET | `/api/caisse-operations/all` | Lister toutes les operations |
| POST | `/api/caisse-operations/ouvrir?agentEmail={email}&montantInitial=1000` | Ouvrir caisse |
| DELETE | `/api/caisse-operations/id/{id}` | Supprimer une operation |

Format date-time pour `debut` et `fin`:

```text
2026-05-24T08:00:00
```

## Clotures Caisse

| Methode | Endpoint | Description |
|---|---|---|
| GET | `/api/clotures-caisse/agent/{email}/date/{date}` | Cloture par agent et date |
| GET | `/api/clotures-caisse/ecarts` | Clotures avec ecart |
| GET | `/api/clotures-caisse/agent/{email}/ecarts` | Ecarts d'un agent |
| GET | `/api/clotures-caisse/all` | Lister les clotures |
| GET | `/api/clotures-caisse/{email}/rapport?date=2026-05-24` | Rapport de cloture |
| POST | `/api/clotures-caisse/add-one` | Creer une cloture |
| POST | `/api/clotures-caisse/{email}/cloturer` | Cloturer la caisse |
| PUT | `/api/clotures-caisse/update` | Modifier une cloture |
| DELETE | `/api/clotures-caisse/id/{id}` | Supprimer une cloture |

Create / update cloture:

```json
{
  "agentEmail": "agent@test.com",
  "date": "2026-05-24",
  "soldeSaisi": 5000.00
}
```

## AML

### Declarations De Soupcon

| Methode | Endpoint | Description |
|---|---|---|
| GET | `/api/aml/declarations` | Lister les declarations |
| GET | `/api/aml/declarations/{id}` | Recuperer une declaration |
| POST | `/api/aml/declarations` | Creer une declaration |
| PUT | `/api/aml/declarations/{id}` | Modifier une declaration |
| DELETE | `/api/aml/declarations/{id}` | Supprimer une declaration |

### Journal Audit

| Methode | Endpoint | Description |
|---|---|---|
| GET | `/api/aml/audit` | Lister les audits |
| GET | `/api/aml/audit/{id}` | Recuperer un audit |
| POST | `/api/aml/audit` | Creer un audit |
| PUT | `/api/aml/audit/{id}` | Modifier un audit |
| DELETE | `/api/aml/audit/{id}` | Supprimer un audit |

### Regles AML

| Methode | Endpoint | Description |
|---|---|---|
| GET | `/api/aml/regles` | Lister les regles |
| GET | `/api/aml/regles/{id}` | Recuperer une regle |
| POST | `/api/aml/regles` | Creer une regle |
| PUT | `/api/aml/regles/{id}` | Modifier une regle |
| DELETE | `/api/aml/regles/{id}` | Supprimer une regle |

Les endpoints AML utilisent directement les entites `DeclarationSoupcon`, `JournalAudit` et `RegleAML` comme body JSON.
