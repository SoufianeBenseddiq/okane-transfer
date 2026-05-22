# PieceIdentiteController — `/api/utilisateurs/{clientId}/pieces`

Manages identity documents attached to a client account.  
All endpoints require a valid JWT in the `Authorization` header.

```
Authorization: Bearer <accessToken>
```

ID document numbers are **encrypted at rest** (AES-256) — they are stored encrypted in the database and decrypted automatically on read. The API always works with plain-text numbers.

---

## Endpoints

### 1. `GET /api/utilisateurs/{clientId}/pieces`

Returns all identity documents belonging to a client.

**Path Parameters**

| Parameter | Type | Description |
|---|---|---|
| `clientId` | Long | ID of the client |

```
GET /api/utilisateurs/2/pieces
```

**Response `200 OK`**

```json
[
  {
    "id": 1,
    "numero": "AB123456",
    "type": "PASSEPORT",
    "paysEmetteur": "Maroc",
    "dateExpiration": "2030-06-15",
    "principale": true
  },
  {
    "id": 2,
    "numero": "C-987654",
    "type": "CIN",
    "paysEmetteur": "Maroc",
    "dateExpiration": null,
    "principale": false
  }
]
```

| Field | Description |
|---|---|
| `principale` | `true` if this is the default/primary document used for transfers |
| `dateExpiration` | `null` if the document has no expiry date |

---

### 2. `POST /api/utilisateurs/{clientId}/pieces`

Adds a new identity document to a client account.  
If this is the **first document** added, it automatically becomes the principal.

**Path Parameters**

| Parameter | Type | Description |
|---|---|---|
| `clientId` | Long | ID of the client |

**Request Body**

| Field | Type | Required | Rules |
|---|---|---|---|
| `numero` | string | yes | Non-empty (will be encrypted before storage) |
| `type` | string | yes | One of: `CIN`, `PASSEPORT`, `CARTE_SEJOUR`, `PERMIS` |
| `paysEmetteur` | string | yes | Non-empty |
| `dateExpiration` | string (date) | no | Format `YYYY-MM-DD`. Omit if no expiry |

```json
{
  "numero": "AB123456",
  "type": "PASSEPORT",
  "paysEmetteur": "Maroc",
  "dateExpiration": "2030-06-15"
}
```

Example without expiry date:

```json
{
  "numero": "C-987654",
  "type": "CIN",
  "paysEmetteur": "Maroc"
}
```

**Response `201 Created`**

```json
{
  "id": 1,
  "numero": "AB123456",
  "type": "PASSEPORT",
  "paysEmetteur": "Maroc",
  "dateExpiration": "2030-06-15",
  "principale": true
}
```

**Error Responses**

| Code | Reason |
|---|---|
| `400` | Missing required field or invalid `type` value |
| `403` | Client not found |

**Accepted `type` values**

| Value | Document |
|---|---|
| `CIN` | Carte Nationale d'Identité |
| `PASSEPORT` | Passeport |
| `CARTE_SEJOUR` | Carte de séjour |
| `PERMIS` | Permis de conduire |

---

### 3. `PUT /api/utilisateurs/{clientId}/pieces/{pieceId}/principale`

Sets a document as the principal (default) document for the client.  
All other documents of the same client automatically lose the `principale` flag.

**Path Parameters**

| Parameter | Type | Description |
|---|---|---|
| `clientId` | Long | ID of the client |
| `pieceId` | Long | ID of the document to promote |

```
PUT /api/utilisateurs/2/pieces/2/principale
```

No request body.

**Response `200 OK`**

```json
{
  "id": 2,
  "numero": "C-987654",
  "type": "CIN",
  "paysEmetteur": "Maroc",
  "dateExpiration": null,
  "principale": true
}
```

**Error Responses**

| Code | Reason |
|---|---|
| `403` | Document does not belong to this client |

---

### 4. `DELETE /api/utilisateurs/{clientId}/pieces/{pieceId}`

Deletes an identity document.  
**Cannot delete the principal document** — set another document as principal first.

**Path Parameters**

| Parameter | Type | Description |
|---|---|---|
| `clientId` | Long | ID of the client |
| `pieceId` | Long | ID of the document to delete |

```
DELETE /api/utilisateurs/2/pieces/1
```

No request body.

**Response `204 No Content`**

**Error Responses**

| Code | Reason |
|---|---|
| `400` | Attempt to delete the principal document |
| `403` | Document does not belong to this client |

---

## Rules Summary

| Rule | Detail |
|---|---|
| First document added | Automatically becomes `principale` |
| Delete principal | Blocked — promote another document first |
| Encrypted storage | `numero` is AES-256 encrypted in DB, decrypted transparently on read |
| Ownership check | Every operation verifies the document belongs to the given `clientId` |

---

## Typical Usage Flow

```
1. POST  /api/utilisateurs/2/pieces          → add first document (auto-principal)
2. POST  /api/utilisateurs/2/pieces          → add a second document
3. PUT   /api/utilisateurs/2/pieces/2/principale → switch principal to second doc
4. DELETE /api/utilisateurs/2/pieces/1      → now safe to delete the former principal
5. GET   /api/utilisateurs/2/pieces          → verify final state
```
