# AuthController — `/api/auth`

Handles authentication: login, token refresh, and logout.  
No JWT required on these endpoints (they are public).

---

## Endpoints

### 1. `POST /api/auth/login`

Authenticates a user with email and password.  
Returns a JWT access token + refresh token on success.

**Request Body**

| Field | Type | Required | Rules |
|---|---|---|---|
| `email` | string | yes | Valid email format |
| `motDePasse` | string | yes | Non-empty |

```json
{
  "email": "okane.admin@gmail.com",
  "motDePasse": "Okane123"
}
```

**Response `200 OK`**

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "requiresOtp": null
}
```

| Field | Description |
|---|---|
| `accessToken` | Short-lived JWT (1 hour). Send in `Authorization: Bearer <token>` header |
| `refreshToken` | Long-lived token (7 days). Use to get a new access token |
| `expiresIn` | Seconds until the access token expires |

**Error Responses**

| Code | Reason |
|---|---|
| `400` | Missing or invalid fields |
| `403` | Email not found, wrong password, or account disabled |

---

### 2. `POST /api/auth/refresh`

Generates a new access token using a valid refresh token.  
Use this when the access token has expired (HTTP 403 on secured endpoints).

**Request Body**

| Field | Type | Required |
|---|---|---|
| `refreshToken` | string | yes |

```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Response `200 OK`**

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "requiresOtp": null
}
```

> The refresh token itself is returned unchanged. Only the access token is renewed.

**Error Responses**

| Code | Reason |
|---|---|
| `400` | Missing `refreshToken` field |
| `403` | Refresh token invalid, expired, or blacklisted |

---

### 3. `POST /api/auth/logout`

Invalidates the current access token server-side (adds it to the blacklist).

**Headers**

```
Authorization: Bearer <accessToken>
```

No request body needed.

**Response `204 No Content`**

Empty body. The token is now blacklisted and will be rejected on all further requests.

> If no `Authorization` header is provided, the endpoint still returns `204` — it is a no-op in that case.

---

## Authentication Flow

```
1.  POST /api/auth/login          → receive accessToken + refreshToken
2.  Use accessToken in headers    → Authorization: Bearer <accessToken>
3.  On 403 (token expired):
      POST /api/auth/refresh      → receive new accessToken
4.  On logout:
      POST /api/auth/logout       → token invalidated
```
