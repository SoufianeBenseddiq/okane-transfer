# Postman Guide — Notifications CRUD

## Prerequisites

- Backend running on `http://localhost:8080`
- MySQL running with at least one existing user
- Postman installed

---

## Environment Setup

Create an environment named **Okane Dev** with the following variables:

| Variable          | Value                   | Description                          |
| ----------------- | ----------------------- | ------------------------------------ |
| `base_url`        | `http://localhost:8080` | Backend base URL                     |
| `admin_token`     | *(from login response)* | JWT token obtained from login        |
| `notification_id` | `1`                     | Default notification ID for tests    |
| `destinataire_id` | `1`                     | Recipient ID — must exist in the DB  |

Select **Okane Dev** from the environment dropdown before running any request.

---

## Authentication

### `POST /okane_transfer_war/api/auth/login`

Obtain a JWT token with `ROLE_ADMIN`.

**Headers**

| Key            | Value              |
| -------------- | ------------------ |
| `Content-Type` | `application/json` |

**Body**

```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Response `200 OK`**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "admin",
  "roles": ["ROLE_ADMIN"],
  "expiresIn": 3600
}
```

Copy the `token` value and paste it into the `admin_token` environment variable.

---

## Notification Endpoints

All requests require the following header:

| Key             | Value                    |
| --------------- | ------------------------ |
| `Authorization` | `Bearer {{admin_token}}` |

---

### 1. `POST /okane_transfer_war/api/notifications`

Creates a new notification.

**Additional Headers**

| Key            | Value              |
| -------------- | ------------------ |
| `Content-Type` | `application/json` |

**Body**

```json
{
  "destinataireId": {{destinataire_id}},
  "message": "Votre transfert de 5000 MAD a été confirmé",
  "type": "SMS"
}
```

**Response `201 Created`**

```json
{
  "id": 1,
  "destinataireId": 1,
  "destinataireEmail": "user@example.com",
  "message": "Votre transfert de 5000 MAD a été confirmé",
  "type": "SMS",
  "lue": false,
  "envoyeLe": "2026-05-24T10:30:45"
}
```

Note the returned `id` and update `notification_id` in the environment.

---

### 2. `GET /okane_transfer_war/api/notifications`

Returns all notifications.

**Response `200 OK`** — array of notification objects.

---

### 3. `GET /okane_transfer_war/api/notifications/{{notification_id}}`

Returns a single notification by ID.

**Response `200 OK`** — notification object.

**Error Responses**

| Code  | Reason                            |
| ----- | --------------------------------- |
| `404` | No notification found with this ID |

---

### 4. `GET /okane_transfer_war/api/notifications/unread`

Returns all unread notifications for the authenticated user.

**Response `200 OK`** — array of notification objects.

---

### 5. `GET /okane_transfer_war/api/notifications/unread/count`

Returns the count of unread notifications.

**Response `200 OK`**

```
3
```

Plain integer, no JSON wrapper.

---

### 6. `GET /okane_transfer_war/api/notifications/type/SMS`

Returns notifications filtered by type. Replace `SMS` with `EMAIL` or `PUSH` as needed.

**Response `200 OK`** — array of notification objects.

---

### 7. `PUT /okane_transfer_war/api/notifications/{{notification_id}}/read`

Marks a specific notification as read. No request body.

**Response `204 No Content`**

---

### 8. `PUT /okane_transfer_war/api/notifications/destinataire/{{destinataire_id}}/read-all`

Marks all notifications as read for the given recipient. No request body.

**Response `204 No Content`**

---

### 9. `DELETE /okane_transfer_war/api/notifications/{{notification_id}}`

Deletes a notification.

**Response `200 OK`**

**Error Responses**

| Code  | Reason                            |
| ----- | --------------------------------- |
| `404` | No notification found with this ID |

---

## Recommended Test Order

| Step | Request              | Expected Response       |
| ---- | -------------------- | ----------------------- |
| 1    | Login                | `200` + token           |
| 2    | Create               | `201` + notification ID |
| 3    | Get all              | `200` + list            |
| 4    | Get by ID            | `200` + object          |
| 5    | Get unread           | `200` + list            |
| 6    | Count unread         | `200` + integer         |
| 7    | Get by type          | `200` + list            |
| 8    | Mark one as read     | `204`                   |
| 9    | Get unread (verify)  | `200` + reduced list    |
| 10   | Mark all as read     | `204`                   |
| 11   | Delete               | `200`                   |
| 12   | Get by ID (verify)   | `404`                   |

---

## Troubleshooting

### `401 Unauthorized`

- The token is missing, malformed, or expired.
- Re-run the login request and update `admin_token`.
- Confirm the header value is `Bearer {{admin_token}}` (with the `Bearer` prefix).

### `403 Forbidden`

- The authenticated user does not have `ROLE_ADMIN`.
- Use an account with the required role.

### `404 Not Found`

- The `notification_id` does not exist.
- Run the Create request first and update the variable with the returned ID.

### `500 Internal Server Error`

- `destinataireId` refers to a user that does not exist. Verify with:
  ```sql
  SELECT id, email, prenom, nom FROM utilisateurs;
  ```
  Update `destinataire_id` to a valid ID.
- `{{base_url}}` must be `http://localhost:8080` — not `http://localhost:8080/okane_transfer_war`.
- MySQL is not running or the connection in `application.properties` is misconfigured.

### Environment variables not resolving

- Confirm **Okane Dev** is selected in the environment dropdown (top-right in Postman).
- Variable names are case-sensitive: `{{base_url}}`, `{{admin_token}}`.