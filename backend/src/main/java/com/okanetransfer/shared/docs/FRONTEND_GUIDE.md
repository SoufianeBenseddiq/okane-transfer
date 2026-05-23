# Okane Transfer — Frontend Development Guide

> Angular 17 · Standalone Components · Tailwind CSS · ngx-translate

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Setup & Run](#2-setup--run)
3. [Folder Structure](#3-folder-structure)
4. [Models — One Interface Per File](#4-models--one-interface-per-file)
5. [Services — How to Call the Backend](#5-services--how-to-call-the-backend)
6. [Authentication Flow](#6-authentication-flow)
7. [Guards — Protecting Routes](#7-guards--protecting-routes)
8. [Interceptors — Automatic JWT & Error Handling](#8-interceptors--automatic-jwt--error-handling)
9. [Adding a New Feature (Step-by-Step)](#9-adding-a-new-feature-step-by-step)
10. [Complete API Reference](#10-complete-api-reference)
11. [Enums Reference](#11-enums-reference)
12. [Error Handling](#12-error-handling)
13. [Rules & Best Practices](#13-rules--best-practices)

---

## 1. Project Overview

**Okane Transfer** is a money transfer platform (similar to Western Union). The frontend is an Angular 17 SPA that consumes a Spring MVC 6 REST API.

| Layer | Technology |
|---|---|
| Frontend Framework | Angular 17 (standalone components) |
| Styling | Tailwind CSS 3.4 |
| HTTP | Angular `HttpClient` with functional interceptors |
| i18n | ngx-translate (fr / en / ar) |
| Backend Base URL | `http://localhost:8080/okane_transfer_war` |

**Four roles, four independent UIs:**

| Role | Path prefix | Can do |
|---|---|---|
| `ROLE_ADMIN` | `/admin` | Manage everything: users, agencies, currencies, corridors, AML |
| `ROLE_MANAGER` | `/manager` | Manage own agency, agents, see reports |
| `ROLE_AGENT` | `/agent` | Process sends/withdrawals, manage own caisse |
| `ROLE_CLIENT` | `/client` | Track own transfers, manage profile |

---

## 2. Setup & Run

```bash
# from the project root
cd frontend
npm install
npm start          # serves on http://localhost:4200
npm run build      # production build
```

Make sure the backend Tomcat is running on port 8080 before starting the frontend.

---

## 3. Folder Structure

```
frontend/src/app/
│
├── core/                        ← shared infrastructure (never feature logic here)
│   ├── models/                  ← TypeScript interfaces mirroring backend DTOs
│   │   ├── enums/               ← one file per enum
│   │   ├── auth/                ← LoginRequest, TokenResponse, RefreshTokenRequest
│   │   ├── user/                ← UserResponse, CreateUserRequest, UpdateProfilRequest
│   │   ├── piece-identite/      ← PieceIdentiteResponse, PieceIdentiteRequest
│   │   ├── transfert/           ← TransfertResponse, CreateTransfertRequest, PaiementRequest
│   │   ├── beneficiaire/        ← BeneficiaireResponse, Create/Update requests
│   │   ├── expediteur/          ← ExpediteurResponse, Create/Update requests
│   │   ├── agence/              ← AgenceResponse, AgenceRequest
│   │   ├── devise/              ← DeviseResponse, CorridorResponse, FraisResult, ...
│   │   ├── caisse/              ← CaisseOperationResponse, ClotureCaisseResponse, ...
│   │   ├── aml/                 ← DeclarationResponse, AuditResponse, RegleAML
│   │   └── error/               ← ErrorResponse
│   │
│   ├── services/                ← one service per backend domain
│   │   ├── auth.service.ts
│   │   ├── user.service.ts
│   │   ├── transfert.service.ts
│   │   ├── agence.service.ts
│   │   ├── devise.service.ts
│   │   ├── caisse.service.ts
│   │   └── aml.service.ts
│   │
│   ├── interceptors/
│   │   ├── auth.interceptor.ts  ← adds "Authorization: Bearer <token>" to all requests
│   │   └── error.interceptor.ts ← auto-refresh on 401, redirect to login on 403
│   │
│   └── guards/
│       ├── auth.guard.ts        ← blocks unauthenticated users
│       └── role.guard.ts        ← blocks wrong roles
│
├── features/                    ← one folder per role
│   ├── auth/                    ← login page, unauthorized page
│   ├── admin/                   ← admin pages
│   ├── manager/                 ← manager pages
│   ├── agent/                   ← agent pages
│   └── client/                  ← client pages
│
├── layouts/                     ← shell components (navbar, sidebar) per role
│   ├── admin-layout/
│   ├── agent-layout/
│   ├── auth-layout/
│   └── client-layout/
│
└── shared/                      ← reusable UI components, pipes, directives
    ├── components/
    ├── pipes/
    └── directives/
```

---

## 4. Models — One Interface Per File

Every TypeScript interface mirrors exactly one Java DTO from the backend.

### How to import

Each module folder has an `index.ts` barrel — use the short form:

```typescript
// ✅ correct — import from the barrel
import { TransfertResponse } from '../core/models/transfert';
import { AgenceRequest } from '../core/models/agence';
import { RoleUtilisateur } from '../core/models/enums';

// ✅ also correct — import the specific file directly
import { TransfertResponse } from '../core/models/transfert/transfert-response.model';
```

### Models map

| Folder | Files inside |
|---|---|
| `models/enums/` | `statut-transfert.enum.ts`, `role-utilisateur.enum.ts`, `type-operation.enum.ts`, `type-notification.enum.ts`, `type-piece.enum.ts` |
| `models/auth/` | `login-request.model.ts`, `refresh-token-request.model.ts`, `token-response.model.ts` |
| `models/user/` | `user-response.model.ts`, `create-user-request.model.ts`, `update-profil-request.model.ts` |
| `models/piece-identite/` | `piece-identite-response.model.ts`, `piece-identite-request.model.ts` |
| `models/transfert/` | `transfert-response.model.ts`, `create-transfert-request.model.ts`, `update-transfert-request.model.ts`, `paiement-request.model.ts` |
| `models/beneficiaire/` | `beneficiaire-response.model.ts`, `create-beneficiaire-request.model.ts`, `update-beneficiaire-request.model.ts` |
| `models/expediteur/` | `expediteur-response.model.ts`, `create-expediteur-request.model.ts`, `update-expediteur-request.model.ts` |
| `models/agence/` | `agence-response.model.ts`, `agence-request.model.ts` |
| `models/devise/` | `devise-response.model.ts`, `devise-request.model.ts`, `corridor-response.model.ts`, `corridor-request.model.ts`, `grille-tarifaire-request.model.ts`, `frais-result.model.ts` |
| `models/caisse/` | `caisse-operation-response.model.ts`, `cloture-caisse-response.model.ts`, `cloture-caisse-request.model.ts` |
| `models/aml/` | `declaration-response.model.ts`, `audit-response.model.ts`, `regle-aml.model.ts` |
| `models/error/` | `error-response.model.ts` |

### Important field types

| Java backend type | TypeScript type | Notes |
|---|---|---|
| `Long`, `Integer` | `number` | |
| `BigDecimal` | `number` | Backend sends as JSON number |
| `LocalDateTime` | `string` | ISO string e.g. `"2026-05-23T18:47:38"` |
| `LocalDate` | `string` | ISO date string e.g. `"2026-05-23"` |
| `Enum` | TypeScript `enum` value | Use the matching enum from `models/enums/` |
| `null` | `null` | Some fields are nullable — interfaces use `x | null` |

---

## 5. Services — How to Call the Backend

All services are `providedIn: 'root'` — just inject them in any component constructor. **Do not add them to `providers` arrays.**

### Basic pattern

```typescript
import { Component, OnInit } from '@angular/core';
import { TransfertService } from '../../core/services/transfert.service';
import { TransfertResponse } from '../../core/models/transfert';

@Component({ ... })
export class MesTransfertsComponent implements OnInit {

  transferts: TransfertResponse[] = [];

  constructor(private transfertService: TransfertService) {}

  ngOnInit(): void {
    this.transfertService.getMesTransferts(1001).subscribe({
      next: data => this.transferts = data,
      error: err => console.error(err)
    });
  }
}
```

### Services and their methods

#### `AuthService`

```typescript
login(request: LoginRequest): Observable<TokenResponse>
refresh(request: RefreshTokenRequest): Observable<TokenResponse>
logout(): Observable<void>

// State accessors (no HTTP call)
get token(): string | null
get refreshToken(): string | null
get isLoggedIn(): boolean
get currentUser(): UserResponse | null
setCurrentUser(user: UserResponse): void
clearSession(): void
```

#### `UserService`

```typescript
// ADMIN
create(request: CreateUserRequest): Observable<UserResponse>
findAll(role?: RoleUtilisateur): Observable<UserResponse[]>
findById(id: number): Observable<UserResponse>
desactiver(id: number): Observable<void>
reactiver(id: number): Observable<void>

// CLIENT — own profile
getMe(): Observable<UserResponse>
updateMe(request: UpdateProfilRequest): Observable<UserResponse>
deleteMe(): Observable<void>

// Identity documents
getPieces(clientId: number): Observable<PieceIdentiteResponse[]>
addPiece(clientId: number, request: PieceIdentiteRequest): Observable<PieceIdentiteResponse>
setPrincipale(clientId: number, pieceId: number): Observable<PieceIdentiteResponse>
deletePiece(clientId: number, pieceId: number): Observable<void>
```

#### `TransfertService`

```typescript
// Transfers
getAll(): Observable<TransfertResponse[]>
getById(id: number): Observable<TransfertResponse>
create(request: CreateTransfertRequest): Observable<TransfertResponse>
update(id: number, request: UpdateTransfertRequest): Observable<TransfertResponse>
payer(request: PaiementRequest): Observable<TransfertResponse>
annuler(id: number): Observable<TransfertResponse>
getByCodeRetrait(code: string): Observable<TransfertResponse>
getMesTransferts(clientId: number): Observable<TransfertResponse[]>

// Beneficiaries
getAllBeneficiaires(): Observable<BeneficiaireResponse[]>
getBeneficiaireById(id: number): Observable<BeneficiaireResponse>
createBeneficiaire(request): Observable<BeneficiaireResponse>
updateBeneficiaire(id, request): Observable<BeneficiaireResponse>
deleteBeneficiaire(id: number): Observable<void>

// Expeditors
getAllExpediteurs(): Observable<ExpediteurResponse[]>
getExpediteurById(id: number): Observable<ExpediteurResponse>
createExpediteur(request): Observable<ExpediteurResponse>
updateExpediteur(id, request): Observable<ExpediteurResponse>
deleteExpediteur(id: number): Observable<void>
```

#### `AgenceService`

```typescript
findAll(): Observable<AgenceResponse[]>
findActives(): Observable<AgenceResponse[]>
findByNom(nom: string): Observable<AgenceResponse>
findByAdresse(adresse: string): Observable<AgenceResponse>
findByResponsable(email: string): Observable<AgenceResponse>
create(request: AgenceRequest): Observable<AgenceResponse>
update(id: number, request: AgenceRequest): Observable<AgenceResponse>
delete(id: number): Observable<void>
```

#### `DeviseService`

```typescript
// Currencies
getAllDevises(): Observable<DeviseResponse[]>
getDeviseById(id: number): Observable<DeviseResponse>
getDeviseByCode(code: string): Observable<DeviseResponse>
createDevise(request: DeviseRequest): Observable<DeviseResponse>
updateDevise(id, request): Observable<DeviseResponse>
activerDevise(id: number): Observable<void>
desactiverDevise(id: number): Observable<void>

// Corridors
getAllCorridors(): Observable<CorridorResponse[]>
getCorridorById(id: number): Observable<CorridorResponse>
createCorridor(request: CorridorRequest): Observable<CorridorResponse>
activerCorridor(id: number): Observable<void>
desactiverCorridor(id: number): Observable<void>
calculerFrais(corridorId: number, montant: number): Observable<FraisResult>
createGrille(request: GrilleTarifaireRequest): Observable<void>
```

#### `CaisseService`

```typescript
// Operations
findAllOperations(): Observable<CaisseOperationResponse[]>
findByAgent(email: string): Observable<CaisseOperationResponse[]>
consulterSolde(email: string): Observable<number>
historiqueOperations(email: string): Observable<CaisseOperationResponse[]>
ouvrirCaisse(agentEmail: string, montantInitial: number): Observable<CaisseOperationResponse>
deleteOperation(id: number): Observable<void>

// Clotures
findAllClotures(): Observable<ClotureCaisseResponse[]>
findCloture(email: string, date: string): Observable<ClotureCaisseResponse>
findEcarts(): Observable<ClotureCaisseResponse[]>
findEcartsAgent(email: string): Observable<ClotureCaisseResponse[]>
saveCloture(request: ClotureCaisseRequest): Observable<ClotureCaisseResponse>
updateCloture(request: ClotureCaisseRequest): Observable<ClotureCaisseResponse>
cloturerCaisse(email: string, request: ClotureCaisseRequest): Observable<ClotureCaisseResponse>
rapportCloture(email: string, date: string): Observable<ClotureCaisseResponse>
deleteCloture(id: number): Observable<void>
```

#### `AmlService`

```typescript
// Declarations
getAllDeclarations(): Observable<DeclarationResponse[]>
getDeclarationById(id: number): Observable<DeclarationResponse>
updateDeclaration(id, partial): Observable<DeclarationResponse>
deleteDeclaration(id: number): Observable<void>

// Audit
getAllAuditLogs(): Observable<AuditResponse[]>
getAuditById(id: number): Observable<AuditResponse>

// Rules
getAllRegles(): Observable<RegleAML[]>
getRegleById(id: number): Observable<RegleAML>
createRegle(regle: RegleAML): Observable<RegleAML>
updateRegle(id, regle): Observable<RegleAML>
deleteRegle(id: number): Observable<void>
```

---

## 6. Authentication Flow

```
1. User submits login form
      → POST /api/auth/login  { email, motDePasse }

2. Backend returns TokenResponse
      { accessToken, refreshToken, tokenType: "Bearer", expiresIn: 3600, requiresOtp: false }

3. AuthService stores tokens in localStorage and updates token$ BehaviorSubject

4. authInterceptor automatically adds:
      Authorization: Bearer <accessToken>
   to every subsequent HTTP request

5. When access token expires (401 response):
      errorInterceptor automatically calls POST /api/auth/refresh
      Gets a new accessToken and retries the original request

6. On logout:
      POST /api/auth/logout  (blacklists the token server-side)
      AuthService.clearSession() wipes localStorage
```

### Login component example

```typescript
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { UserService } from '../../core/services/user.service';
import { LoginRequest } from '../../core/models/auth';
import { RoleUtilisateur } from '../../core/models/enums';

@Component({ ... })
export class LoginComponent {

  form: LoginRequest = { email: '', motDePasse: '' };
  error = '';

  constructor(
    private auth: AuthService,
    private userService: UserService,
    private router: Router
  ) {}

  submit(): void {
    this.auth.login(this.form).subscribe({
      next: tokenRes => {
        if (tokenRes.requiresOtp) {
          this.router.navigate(['/auth/otp']);
          return;
        }
        // Load user profile and redirect by role
        this.userService.getMe().subscribe(user => {
          this.auth.setCurrentUser(user);
          this.redirectByRole(user.role);
        });
      },
      error: () => this.error = 'Email ou mot de passe incorrect'
    });
  }

  private redirectByRole(role: RoleUtilisateur): void {
    const routes: Record<RoleUtilisateur, string> = {
      [RoleUtilisateur.ROLE_ADMIN]:   '/admin/dashboard',
      [RoleUtilisateur.ROLE_MANAGER]: '/manager/dashboard',
      [RoleUtilisateur.ROLE_AGENT]:   '/agent/caisse',
      [RoleUtilisateur.ROLE_CLIENT]:  '/client/dashboard'
    };
    this.router.navigate([routes[role] ?? '/auth/login']);
  }
}
```

---

## 7. Guards — Protecting Routes

Two guards are ready. Use them in your feature route files.

### `authGuard` — user must be logged in

```typescript
import { authGuard } from '../../core/guards/auth.guard';

export const clientRoutes: Routes = [
  {
    path: 'dashboard',
    canActivate: [authGuard],
    component: ClientDashboardComponent
  }
];
```

### `roleGuard` — user must have a specific role

Pass the allowed roles in `data.roles`. The guard reads the current user's role from `AuthService`.

```typescript
import { roleGuard } from '../../core/guards/role.guard';
import { RoleUtilisateur } from '../../core/models/enums';

export const adminRoutes: Routes = [
  {
    path: 'agences',
    canActivate: [roleGuard],
    data: { roles: [RoleUtilisateur.ROLE_ADMIN] },
    component: AgencesComponent
  },
  {
    path: 'rapports',
    canActivate: [roleGuard],
    data: { roles: [RoleUtilisateur.ROLE_ADMIN, RoleUtilisateur.ROLE_MANAGER] },
    component: RapportsComponent
  }
];
```

> Both guards are already applied at the top-level route level in `app.routes.ts`. Inside a feature's child routes you can use them too for finer control.

---

## 8. Interceptors — Automatic JWT & Error Handling

The interceptors are wired in `app.config.ts` and run on **every** HTTP request. You do not need to set the `Authorization` header manually — ever.

### `authInterceptor`

Reads the access token from `AuthService` and adds it automatically:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### `errorInterceptor`

| Status | Behavior |
|---|---|
| `401` | Tries to refresh the token once, retries the original request. If refresh also fails → clears session → redirects to `/auth/login` |
| `403` | Redirects to `/auth/login` |
| Any other | Propagates the error so your component can handle it |

---

## 9. Adding a New Feature (Step-by-Step)

Follow this checklist every time you build a new page.

### Step 1 — Create the component

Use standalone components (Angular 17 default):

```typescript
// features/agent/envoi/envoi.component.ts
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { TransfertService } from '../../../core/services/transfert.service';
import { CreateTransfertRequest } from '../../../core/models/transfert';

@Component({
  selector: 'app-envoi',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './envoi.component.html'
})
export class EnvoiComponent {

  form = this.fb.group({
    clientId:              [null, Validators.required],
    pieceIdentiteId:       [null, Validators.required],
    nomBeneficiaire:       ['',  Validators.required],
    prenomBeneficiaire:    ['',  Validators.required],
    telephoneBeneficiaire: ['',  Validators.required],
    paysBeneficiaire:      ['',  Validators.required],
    montant:               [0,   [Validators.required, Validators.min(1)]],
    agentId:               [null],
    agenceEnvoiId:         [null],
    corridorId:            [null],
    grilleTarifaireId:     [null]
  });

  constructor(private transfertService: TransfertService, private fb: FormBuilder) {}

  submit(): void {
    if (this.form.invalid) return;

    const request = this.form.value as CreateTransfertRequest;

    this.transfertService.create(request).subscribe({
      next: res => {
        console.log('Code retrait:', res.codeRetrait);
        console.log('Référence:', res.numeroReference);
      },
      error: err => console.error(err)
    });
  }
}
```

### Step 2 — Register in the feature routes file

```typescript
// features/agent/agent.routes.ts
import { Routes } from '@angular/router';
import { authGuard } from '../../core/guards/auth.guard';

export const agentRoutes: Routes = [
  {
    path: 'envoi',
    canActivate: [authGuard],
    loadComponent: () => import('./envoi/envoi.component').then(m => m.EnvoiComponent)
  },
  {
    path: 'caisse',
    canActivate: [authGuard],
    loadComponent: () => import('./caisse/caisse.component').then(m => m.CaisseComponent)
  }
];
```

### Step 3 — Handle loading and error states

```typescript
export class MyComponent {
  data: SomeResponse[] = [];
  loading = false;
  error = '';

  constructor(private myService: MyService) {}

  loadData(): void {
    this.loading = true;
    this.error = '';

    this.myService.getAll().subscribe({
      next: res => {
        this.data = res;
        this.loading = false;
      },
      error: err => {
        this.error = err.error?.message ?? 'Une erreur est survenue';
        this.loading = false;
      }
    });
  }
}
```

### Step 4 — Read the current user from AuthService

```typescript
import { AuthService } from '../../../core/services/auth.service';

constructor(private auth: AuthService) {}

get currentUser() {
  return this.auth.currentUser;
}
```

---

## 10. Complete API Reference

> Base URL: `http://localhost:8080/okane_transfer_war`
> All secured endpoints require: `Authorization: Bearer <accessToken>` (added automatically by the interceptor)

### Auth — `/api/auth` (no token needed)

| Method | Path | Request body | Response |
|---|---|---|---|
| `POST` | `/api/auth/login` | `{ email, motDePasse }` | `TokenResponse` |
| `POST` | `/api/auth/refresh` | `{ refreshToken }` | `TokenResponse` |
| `POST` | `/api/auth/logout` | — | `204` |

### Users — `/api/utilisateurs`

| Method | Path | Body | Response | Role |
|---|---|---|---|---|
| `POST` | `/api/utilisateurs` | `CreateUserRequest` | `UserResponse` (201) | ADMIN |
| `GET` | `/api/utilisateurs?role=` | — | `UserResponse[]` | ADMIN |
| `GET` | `/api/utilisateurs/{id}` | — | `UserResponse` | ADMIN |
| `PUT` | `/api/utilisateurs/{id}/desactiver` | — | `204` | ADMIN |
| `PUT` | `/api/utilisateurs/{id}/reactiver` | — | `204` | ADMIN |
| `GET` | `/api/utilisateurs/me` | — | `UserResponse` | CLIENT |
| `PUT` | `/api/utilisateurs/me` | `UpdateProfilRequest` | `UserResponse` | CLIENT |
| `DELETE` | `/api/utilisateurs/me` | — | `204` | CLIENT |

### Identity Documents — `/api/utilisateurs/{clientId}/pieces`

| Method | Path | Body | Response | Role |
|---|---|---|---|---|
| `GET` | `/{clientId}/pieces` | — | `PieceIdentiteResponse[]` | CLIENT, ADMIN |
| `POST` | `/{clientId}/pieces` | `PieceIdentiteRequest` | `PieceIdentiteResponse` (201) | CLIENT, ADMIN |
| `PUT` | `/{clientId}/pieces/{pieceId}/principale` | — | `PieceIdentiteResponse` | CLIENT |
| `DELETE` | `/{clientId}/pieces/{pieceId}` | — | `204` | CLIENT |

### Transfers — `/api/transferts`

| Method | Path | Body | Response | Role |
|---|---|---|---|---|
| `GET` | `/api/transferts` | — | `TransfertResponse[]` | All |
| `GET` | `/api/transferts/{id}` | — | `TransfertResponse` | All |
| `POST` | `/api/transferts` | `CreateTransfertRequest` | `TransfertResponse` | AGENT |
| `PUT` | `/api/transferts/{id}` | `UpdateTransfertRequest` | `TransfertResponse` | AGENT |
| `POST` | `/api/transferts/paiement` | `{ codeRetrait }` | `TransfertResponse` | AGENT |
| `PUT` | `/api/transferts/{id}/annuler` | — | `TransfertResponse` | ADMIN |
| `GET` | `/api/transferts/code/{code}` | — | `TransfertResponse` | AGENT |
| `GET` | `/api/transferts/mes-transferts?clientId=` | — | `TransfertResponse[]` | CLIENT |

### Beneficiaries — `/api/beneficiaires`

| Method | Path | Body | Response |
|---|---|---|---|
| `GET` | `/api/beneficiaires` | — | `BeneficiaireResponse[]` |
| `GET` | `/api/beneficiaires/{id}` | — | `BeneficiaireResponse` |
| `POST` | `/api/beneficiaires` | `CreateBeneficiaireRequest` | `BeneficiaireResponse` |
| `PUT` | `/api/beneficiaires/{id}` | `UpdateBeneficiaireRequest` | `BeneficiaireResponse` |
| `DELETE` | `/api/beneficiaires/{id}` | — | `204` |

### Expeditors — `/api/expediteurs`

| Method | Path | Body | Response |
|---|---|---|---|
| `GET` | `/api/expediteurs` | — | `ExpediteurResponse[]` |
| `GET` | `/api/expediteurs/{id}` | — | `ExpediteurResponse` |
| `POST` | `/api/expediteurs` | `{ clientId, pieceIdentiteId }` | `ExpediteurResponse` |
| `PUT` | `/api/expediteurs/{id}` | `{ clientId, pieceIdentiteId }` | `ExpediteurResponse` |
| `DELETE` | `/api/expediteurs/{id}` | — | `204` |

### Agencies — `/api/agences`

| Method | Path | Body | Response | Role |
|---|---|---|---|---|
| `GET` | `/api/agences/all` | — | `AgenceResponse[]` | ADMIN, MANAGER |
| `GET` | `/api/agences/actives` | — | `AgenceResponse[]` | ADMIN, MANAGER, AGENT |
| `GET` | `/api/agences/nom/{nom}` | — | `AgenceResponse` | ADMIN, MANAGER |
| `GET` | `/api/agences/adresse/{adresse}` | — | `AgenceResponse` | ADMIN, MANAGER |
| `GET` | `/api/agences/responsable/{email}` | — | `AgenceResponse` | ADMIN, MANAGER |
| `POST` | `/api/agences/add-one` | `AgenceRequest` | `AgenceResponse` (201) | ADMIN |
| `PUT` | `/api/agences/id/{id}` | `AgenceRequest` | `AgenceResponse` | ADMIN |
| `DELETE` | `/api/agences/id/{id}` | — | `204` | ADMIN |

### Currencies — `/api/admin/devises` (ADMIN only)

| Method | Path | Body | Response |
|---|---|---|---|
| `GET` | `/api/admin/devises` | — | `DeviseResponse[]` |
| `GET` | `/api/admin/devises/{id}` | — | `DeviseResponse` |
| `GET` | `/api/admin/devises/code/{code}` | — | `DeviseResponse` |
| `POST` | `/api/admin/devises` | `DeviseRequest` | `DeviseResponse` (201) |
| `PUT` | `/api/admin/devises/{id}` | `DeviseRequest` | `DeviseResponse` |
| `PATCH` | `/api/admin/devises/{id}/activer` | — | `204` |
| `PATCH` | `/api/admin/devises/{id}/desactiver` | — | `204` |

### Corridors — `/api/admin/corridors` (ADMIN only)

| Method | Path | Body | Response |
|---|---|---|---|
| `GET` | `/api/admin/corridors` | — | `CorridorResponse[]` |
| `GET` | `/api/admin/corridors/{id}` | — | `CorridorResponse` |
| `POST` | `/api/admin/corridors` | `CorridorRequest` | `CorridorResponse` (201) |
| `PATCH` | `/api/admin/corridors/{id}/activer` | — | `204` |
| `PATCH` | `/api/admin/corridors/{id}/desactiver` | — | `204` |
| `GET` | `/api/admin/corridors/{id}/frais?montant=` | — | `FraisResult` |
| `POST` | `/api/admin/corridors/grilles` | `GrilleTarifaireRequest` | `201` |

### Caisse Operations — `/api/caisse-operations`

| Method | Path | Body | Response | Role |
|---|---|---|---|---|
| `GET` | `/api/caisse-operations/all` | — | `CaisseOperationResponse[]` | ADMIN, MANAGER |
| `GET` | `/api/caisse-operations/agent/{email}` | — | `CaisseOperationResponse[]` | ADMIN, MANAGER, AGENT |
| `GET` | `/api/caisse-operations/agent/{email}/solde` | — | `number` | ADMIN, MANAGER, AGENT |
| `GET` | `/api/caisse-operations/{email}/operations` | — | `CaisseOperationResponse[]` | AGENT |
| `POST` | `/api/caisse-operations/ouvrir?agentEmail=&montantInitial=` | — | `CaisseOperationResponse` | AGENT |
| `DELETE` | `/api/caisse-operations/id/{id}` | — | `204` | ADMIN |

### Caisse Clotures — `/api/clotures-caisse`

| Method | Path | Body | Response | Role |
|---|---|---|---|---|
| `GET` | `/api/clotures-caisse/all` | — | `ClotureCaisseResponse[]` | ADMIN, MANAGER |
| `GET` | `/api/clotures-caisse/agent/{email}/date/{date}` | — | `ClotureCaisseResponse` | All |
| `GET` | `/api/clotures-caisse/ecarts` | — | `ClotureCaisseResponse[]` | ADMIN, MANAGER |
| `GET` | `/api/clotures-caisse/agent/{email}/ecarts` | — | `ClotureCaisseResponse[]` | ADMIN, MANAGER |
| `POST` | `/api/clotures-caisse/add-one` | `ClotureCaisseRequest` | `ClotureCaisseResponse` (201) | AGENT |
| `PUT` | `/api/clotures-caisse/update` | `ClotureCaisseRequest` | `ClotureCaisseResponse` | ADMIN, MANAGER |
| `POST` | `/api/clotures-caisse/{email}/cloturer` | `ClotureCaisseRequest` | `ClotureCaisseResponse` | AGENT |
| `GET` | `/api/clotures-caisse/{email}/rapport?date=` | — | `ClotureCaisseResponse` | AGENT |
| `DELETE` | `/api/clotures-caisse/id/{id}` | — | `204` | ADMIN |

### AML — `/api/aml`

| Method | Path | Body | Response | Role |
|---|---|---|---|---|
| `GET` | `/api/aml/declarations` | — | `DeclarationResponse[]` | ADMIN |
| `GET` | `/api/aml/declarations/{id}` | — | `DeclarationResponse` | ADMIN |
| `PUT` | `/api/aml/declarations/{id}` | partial body | `DeclarationResponse` | ADMIN |
| `DELETE` | `/api/aml/declarations/{id}` | — | `204` | ADMIN |
| `GET` | `/api/aml/audit` | — | `AuditResponse[]` | ADMIN |
| `GET` | `/api/aml/audit/{id}` | — | `AuditResponse` | ADMIN |
| `GET` | `/api/aml/regles` | — | `RegleAML[]` | ADMIN |
| `POST` | `/api/aml/regles` | `RegleAML` | `RegleAML` | ADMIN |
| `PUT` | `/api/aml/regles/{id}` | `RegleAML` | `RegleAML` | ADMIN |
| `DELETE` | `/api/aml/regles/{id}` | — | `204` | ADMIN |

---

## 11. Enums Reference

Always use the TypeScript enum — never hardcode the string value.

```typescript
import { StatutTransfert } from '../core/models/enums';

// ✅ correct
if (transfert.statut === StatutTransfert.EN_ATTENTE) { ... }

// ❌ wrong — breaks if backend renames the value
if (transfert.statut === 'EN_ATTENTE') { ... }
```

| Enum | Values |
|---|---|
| `StatutTransfert` | `EN_ATTENTE`, `PAYE`, `ANNULE`, `EXPIRE`, `BLOQUE` |
| `RoleUtilisateur` | `ROLE_ADMIN`, `ROLE_MANAGER`, `ROLE_AGENT`, `ROLE_CLIENT` |
| `TypeOperation` | `ENVOI`, `RETRAIT`, `OUVERTURE`, `AJUSTEMENT`, `CLOTURE` |
| `TypePiece` | `CIN`, `PASSEPORT`, `CARTE_SEJOUR`, `PERMIS` |
| `TypeNotification` | `SMS`, `EMAIL`, `PUSH` |

---

## 12. Error Handling

The backend returns errors in this shape (from `GlobalExceptionHandler`):

```json
{
  "code": 404,
  "message": "Transfert introuvable avec l'id : 99",
  "timestamp": "2026-05-23T18:47:38"
}
```

This is typed as `ErrorResponse` in `models/error/error-response.model.ts`.

### Handling errors in components

```typescript
import { HttpErrorResponse } from '@angular/common/http';
import { ErrorResponse } from '../core/models/error';

this.transfertService.create(request).subscribe({
  next: res => { ... },
  error: (err: HttpErrorResponse) => {
    const apiError: ErrorResponse = err.error;
    this.errorMessage = apiError?.message ?? 'Erreur inattendue';

    // Known business errors
    if (err.status === 400) { /* validation failure */ }
    if (err.status === 403) { /* OFAC violation or access denied */ }
    if (err.status === 404) { /* entity not found */ }
  }
});
```

### Known business exceptions from backend

| HTTP Status | Backend Exception | Meaning |
|---|---|---|
| `400` | `PlafondDepasseException` | Transfer exceeds agency daily ceiling |
| `400` | `CorridorInactifException` | Currency corridor is disabled |
| `400` | `CodeRetraitInvalideException` | Withdrawal code not found or already used |
| `403` | `OFACViolationException` | Beneficiary/sender is on watchlist |
| `403` | `AccesRefuseException` | User accessing data outside their scope |
| `404` | `TransfertNotFoundException` | Transfer not found |

---

## 13. Rules & Best Practices

### Do

- **Use the provided services** — never call `HttpClient` directly from a component
- **Use the TypeScript interfaces** — always type your variables with the correct model
- **Use `async` pipe** in templates when possible to avoid manual subscription management
- **Use enums** for any status or role comparison — never compare against raw strings
- **Use standalone components** — all Angular 17 components must have `standalone: true`
- **Use lazy loading** for all feature routes via `loadComponent` or `loadChildren`
- **Handle both `next` and `error`** in every `subscribe()` call
- **Read the current user** from `AuthService.currentUser` — never store it again in a separate variable

### Do not

- **Never call `HttpClient` directly** from a component — always go through a service
- **Never add `Authorization` headers manually** — the interceptor does it automatically
- **Never store the token in a component** — use `AuthService` only
- **Never use `any` type** for API responses — use the typed interfaces
- **Never add services to `providers` in component decorators** — they are all `providedIn: 'root'`
- **Never compare role strings directly** — use the `RoleUtilisateur` enum

### Import order convention

```typescript
// 1. Angular core
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';

// 2. Services
import { TransfertService } from '../../../core/services/transfert.service';

// 3. Models
import { TransfertResponse, CreateTransfertRequest } from '../../../core/models/transfert';
import { StatutTransfert } from '../../../core/models/enums';
```

### Date formatting

Backend dates are ISO strings. Use Angular's `DatePipe` for display:

```html
<!-- template -->
{{ transfert.creeLe | date:'dd/MM/yyyy HH:mm' }}
{{ cloture.date | date:'dd/MM/yyyy' }}
```

For sending dates to the backend, format as `YYYY-MM-DD`:

```typescript
const today = new Date().toISOString().split('T')[0]; // "2026-05-23"
```

### Amount formatting

Backend amounts are numbers (from `BigDecimal`). Format for display:

```html
{{ transfert.montantEnvoye | number:'1.2-2' }} MAD
```

---

*Last updated: May 2026 — Okane Transfer Team*
