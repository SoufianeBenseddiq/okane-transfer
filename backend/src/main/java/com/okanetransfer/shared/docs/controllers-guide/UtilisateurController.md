# UtilisateurController — `/api/utilisateurs`

Manages user accounts (ADMIN operations) and the connected client's own profile.  
All endpoints require a valid JWT in the `Authorization` header.

```
Authorization: Bearer <accessToken>
```

---

## ADMIN Endpoints

### 1. `POST /api/utilisateurs`

Creates a new user. The role determines which subclass is instantiated (Client, Agent, Manager, Administrateur).

**Request Body**

| Field | Type | Required | Rules |
|---|---|---|---|
| `nom` | string | yes | 2–50 characters |
| `prenom` | string | yes | 2–50 characters |
| `email` | string | yes | Valid email, unique |
| `motDePasse` | string | yes | Min 8 chars, at least 1 uppercase, 1 digit |
| `telephone` | string | yes | `+?[0-9]{8,15}` (e.g. `+212661234567`) |
| `pays` | string | yes | Non-empty |
| `role` | string | yes | `ROLE_ADMIN`, `ROLE_MANAGER`, `ROLE_AGENT`, `ROLE_CLIENT` |

```json
{
  "nom": "Benseddiq",
  "prenom": "Soufiane",
  "email": "soufiane@okane.ma",
  "motDePasse": "Password1",
  "telephone": "+212661234567",
  "pays": "Maroc",
  "role": "ROLE_CLIENT"
}
```

**Response `201 Created`**

```json
{
  "id": 2,
  "nom": "Benseddiq",
  "prenom": "Soufiane",
  "email": "soufiane@okane.ma",
  "telephone": "+212661234567",
  "pays": "Maroc",
  "role": "ROLE_CLIENT",
  "actif": true,
  "creeLe": "2026-05-22T10:30:00"
}
```

> The password is never included in any response.

**Error Responses**

| Code | Reason |
|---|---|
| `400` | Validation failure (field errors returned as `{ "field": "message" }`) |
| `400` | Email already in use |

---

### 2. `GET /api/utilisateurs`

Returns all users. Optionally filter by role.

**Query Parameters**

| Parameter | Type | Required | Example |
|---|---|---|---|
| `role` | string | no | `ROLE_AGENT`, `ROLE_CLIENT` |

```
GET /api/utilisateurs
GET /api/utilisateurs?role=ROLE_CLIENT
GET /api/utilisateurs?role=ROLE_AGENT
```

**Response `200 OK`**

```json
[
  {
    "id": 1,
    "nom": "Okane",
    "prenom": "Admin",
    "email": "okane.admin@gmail.com",
    "telephone": "+212600000000",
    "pays": "MA",
    "role": "ROLE_ADMIN",
    "actif": true,
    "creeLe": "2026-05-22T08:00:00"
  },
  {
    "id": 2,
    "nom": "Benseddiq",
    "prenom": "Soufiane",
    "email": "soufiane@okane.ma",
    "telephone": "+212661234567",
    "pays": "Maroc",
    "role": "ROLE_CLIENT",
    "actif": true,
    "creeLe": "2026-05-22T10:30:00"
  }
]
```

---

### 3. `GET /api/utilisateurs/{id}`

Returns a single user by ID.

**Path Parameters**

| Parameter | Type | Description |
|---|---|---|
| `id` | Long | ID of the user |

```
GET /api/utilisateurs/2
```

**Response `200 OK`**

```json
{
  "id": 2,
  "nom": "Benseddiq",
  "prenom": "Soufiane",
  "email": "soufiane@okane.ma",
  "telephone": "+212661234567",
  "pays": "Maroc",
  "role": "ROLE_CLIENT",
  "actif": true,
  "creeLe": "2026-05-22T10:30:00"
}
```

**Error Responses**

| Code | Reason |
|---|---|
| `404` | No user found with this ID |

---

### 4. `PUT /api/utilisateurs/{id}/desactiver`

Soft-disables a user account. The account is preserved in the database but the user cannot log in.

```
PUT /api/utilisateurs/2/desactiver
```

No request body.

**Response `204 No Content`**

**Error Responses**

| Code | Reason |
|---|---|
| `404` | No user found with this ID |

---

### 5. `PUT /api/utilisateurs/{id}/reactiver`

Re-enables a previously disabled account.

```
PUT /api/utilisateurs/2/reactiver
```

No request body.

**Response `204 No Content`**

**Error Responses**

| Code | Reason |
|---|---|
| `404` | No user found with this ID |

---

## CLIENT Endpoints (own profile)

These endpoints resolve the connected user from the JWT — no ID in the path.

### 6. `GET /api/utilisateurs/me`

Returns the profile of the currently authenticated user.

```
GET /api/utilisateurs/me
```

No request body.

**Response `200 OK`**

```json
{
  "id": 2,
  "nom": "Benseddiq",
  "prenom": "Soufiane",
  "email": "soufiane@okane.ma",
  "telephone": "+212661234567",
  "pays": "Maroc",
  "role": "ROLE_CLIENT",
  "actif": true,
  "creeLe": "2026-05-22T10:30:00"
}
```

---

### 7. `PUT /api/utilisateurs/me`

Updates the connected user's own profile. All fields are optional — only non-null fields are applied (PATCH semantics over a PUT route).

**Request Body**

| Field | Type | Required | Rules |
|---|---|---|---|
| `nom` | string | no | 2–50 characters |
| `prenom` | string | no | 2–50 characters |
| `telephone` | string | no | `+?[0-9]{8,15}` |
| `pays` | string | no | 2–60 characters |

```json
{
  "telephone": "+212698765432",
  "pays": "France"
}
```

> Send only the fields you want to update. Fields not included in the body are left unchanged.

**Response `200 OK`**

```json
{
  "id": 2,
  "nom": "Benseddiq",
  "prenom": "Soufiane",
  "email": "soufiane@okane.ma",
  "telephone": "+212698765432",
  "pays": "France",
  "role": "ROLE_CLIENT",
  "actif": true,
  "creeLe": "2026-05-22T10:30:00"
}
```

**Error Responses**

| Code | Reason |
|---|---|
| `400` | A provided field fails its validation rule |

---

### 8. `DELETE /api/utilisateurs/me`

RGPD right to erasure. Pseudonymises all personal data and permanently disables the account.

```
DELETE /api/utilisateurs/me
```

No request body.

**Response `204 No Content`**

> **Irreversible.** After this call: name becomes `SUPPRIME`, email becomes `deleted_{id}@supprime.local`, phone becomes `0000000000`, password hash is invalidated, and all identity documents are deleted. The account cannot be re-enabled.
