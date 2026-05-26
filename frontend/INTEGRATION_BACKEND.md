# 📌 Intégration Backend - Module Manager

## ✅ Services créés/mis à jour

### 1. **DashboardService** (`dashboard.service.ts`)
**Endpoints connectés:**
- `GET /api/transferts` - Récupère les transferts pour calculer statistiques
- `GET /api/utilisateurs` - Récupère les utilisateurs et agents
- `GET /api/aml/declarations` - Récupère les validations AML en attente

**Fonctionnalités:**
```typescript
getDashboardStats()        // Stats dashboard temps réel
getAgentPerformance()      // Performance des agents
getPendingValidations()    // Validations AML en attente
```

---

### 2. **AgentsService** (`agents.service.ts`)
**Endpoints connectés:**
- `GET /api/utilisateurs?role=ROLE_AGENT` - Liste des agents
- `GET /api/utilisateurs/{id}` - Détails agent
- `POST /api/utilisateurs` - Créer agent
- `PUT /api/utilisateurs/{id}` - Modifier agent
- `PUT /api/utilisateurs/{id}/desactiver` - Désactiver
- `PUT /api/utilisateurs/{id}/reactiver` - Réactiver

**Fonctionnalités:**
```typescript
getAgents()           // Lister tous les agents
getAgentById(id)      // Récupérer un agent
createAgent(data)     // Créer un agent
updateAgent(id, data) // Modifier
deactivateAgent(id)   // Désactiver
reactivateAgent(id)   // Réactiver
```

---

### 3. **CaisseService** (`caisse.service.ts`)
**Endpoints connectés:**
- `GET /api/caisse-operations/agent/{email}` - Opérations d'un agent
- `GET /api/caisse-operations/agent/{email}/solde` - Solde agent
- `POST /api/caisse-operations/ouvrir` - Ouvrir caisse
- `GET /api/clotures-caisse/*` - Gestion des clotures

**Fonctionnalités:**
```typescript
getOperationsAgent(email)        // Opérations caisse
getSoldeAgent(email)             // Solde de l'agent
ouvrirCaisse(email, montant)     // Ouvrir caisse
getClotureByDate(email, date)    // Cloture du jour
cloturerCaisse(email)            // Cloturer caisse
```

---

### 4. **AmlService** (`aml.service.ts`)
**Endpoints connectés:**
- `GET /api/aml/declarations` - Déclarations AML
- `POST /api/aml/declarations` - Créer déclaration
- `PUT /api/aml/declarations/{id}` - Modifier
- `GET /api/aml/regles` - Règles AML
- `GET /api/aml/audit` - Journal audit

**Fonctionnalités:**
```typescript
getDeclarations()              // Lister validations
createDeclaration(data)        // Créer validation
approveDeclaration(id)         // Approuver
rejectDeclaration(id)          // Rejeter
getRegles()                    // Règles AML
```

---

### 5. **RapportsAgenceService** (`rapports-agence.service.ts`)
**Endpoints connectés:**
- `GET /api/agences/all` - Lister agences
- `GET /api/agences/{id}` - Détails agence
- `POST /api/agences/add-one` - Créer agence
- `PUT /api/agences/id/{id}` - Modifier

**Fonctionnalités:**
```typescript
getAllAgences()          // Toutes les agences
getAgenceByNom(nom)      // Chercher par nom
createAgence(data)       // Créer agence
updateAgence(id, data)   // Modifier
```

---

### 6. **PlafondService** (`plafond.service.ts`)
**Endpoints connectés:**
- `GET /api/agences/id/{id}/plafond` - Info plafond
- `GET /api/transferts/jour` - Transferts du jour
- `POST /api/agences/id/{id}/verifier-plafond` - Vérifier plafond

**Fonctionnalités:**
```typescript
getPlafondDuJour(agenceId)      // Plafond du jour
getTransfertsJour(agenceId)      // Transferts du jour
verifierPlafond(id, montant)     // Vérifier si montant OK
updatePlafondJournalier(id, val) // Changer plafond
```

---

## 🔗 Configuration Backend

**URL du backend** (fichier `.env`):
```
http://localhost:8080/okane-transfer-1.0-SNAPSHOT
```

Si différent, mettez à jour dans: [`environment.ts`](src/environments/environment.ts)

**Authentication:**
- Header: `Authorization: Bearer <token>`
- Le token est ajouté automatiquement par `auth.interceptor`

---

## ✅ Vérifications

- ✅ Compilation réussie (pas d'erreurs TypeScript)
- ✅ Tous les services ont des fallbacks (données mock en cas d'erreur API)
- ✅ HttpClient configuré avec HttpParams pour requêtes paramétrisées
- ✅ Gestion d'erreur avec `catchError()` et retour de données par défaut

---

## 🚀 Prochaines étapes

1. **Démarrer le backend:**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

2. **Tester les endpoints** via Postman ou directement dans le navigateur

3. **Implémenter dans les composants:**
   - Injecter les services dans les composants
   - Appeler les méthodes du service
   - Afficher les données reçues

**Exemple dans un composant:**
```typescript
constructor(private dashboardService: DashboardService) {}

ngOnInit() {
  this.dashboardService.getDashboardStats().subscribe(stats => {
    console.log('Stats:', stats);
  });
}
```

---

## 📄 Fichiers modifiés

- ✅ `src/app/features/manager/dashboard/dashboard.service.ts`
- ✅ `src/app/features/manager/agents/agents.service.ts`
- ✅ `src/app/features/manager/caisse/caisse.service.ts`
- ✅ `src/app/features/manager/aml/aml.service.ts`
- ✅ `src/app/features/manager/rapports-agence/rapports-agence.service.ts`
- ✅ `src/app/features/manager/plafond/plafond.service.ts`
