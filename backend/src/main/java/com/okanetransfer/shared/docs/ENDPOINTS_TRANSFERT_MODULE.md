# APIs du module Transfert

Ce fichier explique comment travailler avec les APIs REST de:

- Beneficiaire
- Expediteur
- Transfert

Les URLs sont donnees sans contexte serveur. Ajoute seulement ton prefixe avant `/api`.

Exemple:

```text
/api/beneficiaires
/api/expediteurs
/api/transferts
```

## Ordre conseille pour tester

1. Creer ou inserer les clients et pieces d'identite.
2. Tester les APIs `beneficiaires`.
3. Tester les APIs `expediteurs`.
4. Tester les APIs `transferts`.

Un transfert utilise un beneficiaire cree automatiquement depuis le body de creation.

## Beneficiaire APIs

### 1. Creer un beneficiaire

```http
POST /api/beneficiaires
```

Body:

```json
{
  "nom": "El Amrani",
  "prenom": "Nadia",
  "telephone": "0610101010",
  "pays": "Maroc",
  "surListeSurveillance": false
}
```

Utilisation:

- cree un nouveau beneficiaire
- retourne le beneficiaire cree avec son `id`

### 2. Lister tous les beneficiaires

```http
GET /api/beneficiaires
```

Utilisation:

- retourne tous les beneficiaires existants

### 3. Chercher un beneficiaire par id

```http
GET /api/beneficiaires/{id}
```

Exemple:

```http
GET /api/beneficiaires/4001
```

Utilisation:

- retourne un seul beneficiaire
- si l'id n'existe pas, retourne une erreur

### 4. Modifier un beneficiaire

```http
PUT /api/beneficiaires/{id}
```

Body:

```json
{
  "nom": "El Amrani",
  "prenom": "Nadia",
  "telephone": "0699999999",
  "pays": "Maroc",
  "surListeSurveillance": false
}
```

Utilisation:

- modifie toutes les informations du beneficiaire

### 5. Supprimer un beneficiaire

```http
DELETE /api/beneficiaires/{id}
```

Exemple:

```http
DELETE /api/beneficiaires/4001
```

Utilisation:

- supprime le beneficiaire

## Expediteur APIs

Un expediteur est lie a:

- un client existant: `clientId`
- une piece d'identite existante: `pieceIdentiteId`

La piece d'identite doit appartenir au meme client.

### 1. Creer un expediteur

```http
POST /api/expediteurs
```

Body:

```json
{
  "clientId": 1001,
  "pieceIdentiteId": 2001
}
```

Utilisation:

- cree un expediteur a partir d'un client et d'une piece d'identite

### 2. Lister tous les expediteurs

```http
GET /api/expediteurs
```

Utilisation:

- retourne tous les expediteurs

### 3. Chercher un expediteur par id

```http
GET /api/expediteurs/{id}
```

Exemple:

```http
GET /api/expediteurs/3001
```

Utilisation:

- retourne un expediteur avec les informations de son client et sa piece

### 4. Modifier un expediteur

```http
PUT /api/expediteurs/{id}
```

Body:

```json
{
  "clientId": 1002,
  "pieceIdentiteId": 2002
}
```

Utilisation:

- change le client ou la piece d'identite de l'expediteur

### 5. Supprimer un expediteur

```http
DELETE /api/expediteurs/{id}
```

Exemple:

```http
DELETE /api/expediteurs/3001
```

Utilisation:

- supprime l'expediteur

## Transfert APIs

### 1. Creer un transfert

```http
POST /api/transferts
```

Body:

```json
{
  "clientId": 1001,
  "pieceIdentiteId": 2001,
  "agentId": null,
  "agenceEnvoiId": null,
  "corridorId": null,
  "grilleTarifaireId": null,
  "nomBeneficiaire": "Diallo",
  "prenomBeneficiaire": "Mamadou",
  "telephoneBeneficiaire": "221771112233",
  "paysBeneficiaire": "Senegal",
  "montant": 1000
}
```

Utilisation:

- cree un beneficiaire
- cree un transfert
- genere automatiquement `codeRetrait`
- genere automatiquement `numeroReference`
- calcule automatiquement les frais
- calcule automatiquement le montant recu

Regle actuelle:

- frais = 5% du montant envoye
- montant recu = montant envoye - frais

### 2. Lister tous les transferts

```http
GET /api/transferts
```

Utilisation:

- retourne tous les transferts

### 3. Chercher un transfert par id

```http
GET /api/transferts/{id}
```

Exemple:

```http
GET /api/transferts/1
```

Utilisation:

- retourne un transfert par son id

### 4. Modifier un transfert

```http
PUT /api/transferts/{id}
```

Body:

```json
{
  "nomBeneficiaire": "Traore",
  "prenomBeneficiaire": "Aminata",
  "telephoneBeneficiaire": "2250701020304",
  "paysBeneficiaire": "Cote Ivoire",
  "montant": 1500
}
```

Utilisation:

- modifie les informations du beneficiaire lie au transfert
- modifie le montant
- recalcule les frais
- recalcule le montant recu

Restrictions:

- impossible de modifier un transfert deja paye
- impossible de modifier un transfert annule

### 5. Payer un transfert

```http
POST /api/transferts/paiement
```

Body:

```json
{
  "codeRetrait": "CODE_ICI"
}
```

Utilisation:

- cherche le transfert par `codeRetrait`
- change son statut vers `PAYE`
- remplit la date de paiement

Le `codeRetrait` est donne dans la reponse de creation du transfert.

### 6. Annuler un transfert

```http
PUT /api/transferts/{id}/annuler
```

Exemple:

```http
PUT /api/transferts/1/annuler
```

Utilisation:

- change le statut du transfert vers `ANNULE`

### 7. Chercher par code de retrait

```http
GET /api/transferts/code/{codeRetrait}
```

Exemple:

```http
GET /api/transferts/code/ABCD1234
```

Utilisation:

- cherche un transfert avec son code de retrait

### 8. Chercher les transferts d'un client

```http
GET /api/transferts/mes-transferts?clientId={clientId}
```

Exemple:

```http
GET /api/transferts/mes-transferts?clientId=1001
```

Utilisation:

- retourne les transferts envoyes par un client

## Donnees SQL pour tester

Les numeros de piece dans `pieces_identite.numero` sont deja cryptes.

```sql
USE okane_transfer;

INSERT INTO utilisateurs
(id, dtype, nom, prenom, email, motDePasseHash, telephone, pays, role, actif, creeLe)
VALUES
(1001, 'CLIENT', 'Alami', 'Youssef', 'youssef.alami@test.com', 'hash-test-1', '0611111111', 'Maroc', 'ROLE_CLIENT', 1, NOW()),
(1002, 'CLIENT', 'Benali', 'Sara', 'sara.benali@test.com', 'hash-test-2', '0622222222', 'Maroc', 'ROLE_CLIENT', 1, NOW()),
(1003, 'CLIENT', 'El Fassi', 'Omar', 'omar.elfassi@test.com', 'hash-test-3', '0633333333', 'France', 'ROLE_CLIENT', 1, NOW());

INSERT INTO clients
(id, twoFactorActive)
VALUES
(1001, 0),
(1002, 1),
(1003, 0);

INSERT INTO pieces_identite
(id, numero, type, paysEmetteur, dateExpiration, principale, client_id)
VALUES
(2001, 'FZBoZ7S1vP4LFQTjoxXxiQ==', 'CIN', 'Maroc', '2030-12-31', 1, 1001),
(2002, '1TQmJhKMQu9SfaQbX5bxhg==', 'PASSEPORT', 'Maroc', '2031-12-31', 1, 1002),
(2003, 'Mh4uhqhxUMYu2fzWBlFDvA==', 'CARTE_SEJOUR', 'France', '2029-06-30', 1, 1003);

INSERT INTO beneficiaires
(id, nom, prenom, telephone, pays, surListeSurveillance)
VALUES
(4001, 'El Amrani', 'Nadia', '0610101010', 'Maroc', 0),
(4002, 'Diallo', 'Mamadou', '221771112233', 'Senegal', 0),
(4003, 'Traore', 'Aminata', '2250701020304', 'Cote Ivoire', 0);

INSERT INTO expediteurs
(id, client_id, piece_identite_id)
VALUES
(3001, 1001, 2001),
(3002, 1002, 2002),
(3003, 1003, 2003);
```

## Tests importants

### Test valide expediteur

```json
{
  "clientId": 1001,
  "pieceIdentiteId": 2001
}
```

### Test invalide expediteur

```json
{
  "clientId": 1001,
  "pieceIdentiteId": 2002
}
```

Cette requete est invalide parce que la piece `2002` appartient au client `1002`, pas au client `1001`.

### Test transfert

1. Appeler `POST /api/transferts`.
2. Copier le `codeRetrait` dans la reponse.
3. Appeler `GET /api/transferts/code/{codeRetrait}`.
4. Appeler `POST /api/transferts/paiement` avec le meme `codeRetrait`.
