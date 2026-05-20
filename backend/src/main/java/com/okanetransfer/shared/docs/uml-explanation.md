Je vais t'expliquer chaque classe en détail, groupe par groupe.

---

## 👤 Hiérarchie des Utilisateurs

### `Utilisateur` — classe mère de tous les profils
C'est la base commune à tous les comptes du système.

```
id             → identifiant unique en base
nom/prenom     → identité de la personne
email          → utilisé pour la connexion
motDePasseHash → mot de passe chiffré BCrypt (jamais en clair)
telephone      → utilisé pour le 2FA par SMS
pays           → pays de résidence
role           → ROLE_ADMIN / ROLE_MANAGER / ROLE_AGENT / ROLE_CLIENT
actif          → si false, la personne ne peut plus se connecter
creeLe         → date de création du compte
```

Les 4 classes qui en héritent ajoutent chacune leurs propres attributs et méthodes.

---

### `Administrateur` hérite de `Utilisateur`
Pas d'attributs supplémentaires — son pouvoir vient de ses méthodes :
```
gererDevises()        → CRUD sur les devises
gererAgences()        → créer, suspendre, configurer les agences
gererUtilisateurs()   → créer/désactiver tous les comptes
configurerFrais()     → définir les grilles tarifaires
voirJournalAudit()    → voir qui a fait quoi et quand
gererListeOFAC()      → ajouter/supprimer des noms suspects manuellement
```

---

### `ResponsableAgence` hérite de `Utilisateur`
```
agence                    → référence vers son agence (1 seule)
validerOperationSensible() → approuver un transfert bloqué ou hors plafond
gererAgents()             → activer/désactiver les agents de son agence
voirRapportAgence()       → stats de son agence uniquement
demanderRevisionPlafond() → envoyer une demande à l'admin
```

---

### `Agent` hérite de `Utilisateur`
```
agence           → l'agence où il travaille
soldesCaisse     → montant actuellement dans sa caisse (en temps réel)
enregistrerEnvoi()  → saisir un nouveau transfert au guichet
payerTransfert()    → remettre les fonds au bénéficiaire contre code
cloturerCaisse()    → fin de journée : comparer théorique vs réel
signalerEcartCaisse() → déclarer une différence inexpliquée
```

---

### `Client` hérite de `Utilisateur`
```
twoFactorActive    → est-ce que le 2FA SMS est activé sur ce compte
suivreTransfert()  → voir le statut en temps réel d'un transfert
voirHistorique()   → liste filtrée de tous ses transferts
gererProfil()      → modifier ses infos personnelles
demanderEffacement() → droit RGPD : supprimer toutes ses données
```

---

## 🏢 `Agence`
Représente une agence physique du réseau.
```
id                      → identifiant unique
nom                     → ex : "Okane Casablanca Centre"
adresse / pays          → localisation physique
plafondJournalier       → montant max autorisé par jour (ex: 200 000 MAD)
montantTraiteAujourdhui → compteur remis à zéro chaque minuit
active                  → si false, aucune opération possible depuis cette agence
verifierPlafond()       → avant chaque transfert : montantTraite + nouveau ≤ plafond ?
calculerCommissions()   → total des commissions générées par l'agence
```

---

## 💸 `Transfert` — entité centrale du système
C'est l'objet le plus important, relié à presque tout.
```
id               → identifiant unique en base
codeRetrait      → ex : "K7X2-M9QA" — donné au bénéficiaire pour retirer
numeroReference  → ex : "TRF-01456" — donné à l'expéditeur pour suivre
montantEnvoye    → ce que paie l'expéditeur (ex: 2 000 MAD)
montantRecu      → ce que reçoit le bénéficiaire (ex: 215 400 XOF)
frais            → ce que prend l'agence (ex: 35 MAD)
statut           → EN_ATTENTE / PAYE / ANNULE / EXPIRE / BLOQUE
creeLe           → date/heure de création
payeLe           → date/heure du retrait effectif
genererCodeRetrait() → crée le code unique alphanumérique après paiement
changerStatut()      → mise à jour du statut avec journalisation
verifierExpiration() → si non retiré après X jours → passe à EXPIRE
```

**Relations du `Transfert` :**
- → `Expediteur` : qui envoie
- → `Beneficiaire` : qui reçoit
- → `Agent` : qui a saisi l'opération
- → `Agence` (envoi) : agence de l'expéditeur
- → `Agence` (retrait) : agence du bénéficiaire
- → `Corridor` : le chemin devise source → devise destination
- → `GrilleTarifaire` : la grille qui a calculé les frais

---

## 💱 Gestion des Devises

### `Devise`
```
code            → "MAD", "EUR", "USD", "XOF"
nom             → "Dirham Marocain"
symbole         → "د.م."
active          → peut-on l'utiliser dans un transfert ?
tauxVersEuro    → taux de référence (tout est converti via l'Euro)
derniereMaj     → quand le taux a été mis à jour la dernière fois
sourceTaux      → "manuel" ou "API:exchangeratesapi.io"
```

### `Corridor`
Représente une route de transfert entre deux devises/pays.
```
deviseSource      → ex : MAD
deviseDestination → ex : XOF
actif             → ce corridor est-il ouvert ?
dateActivation    → depuis quand ce corridor est disponible
convertir()       → calcule le montant reçu selon le taux du jour
estActif()        → vérifie si le corridor n'est pas suspendu
```
> Ex : Le corridor MAD→XOF permet d'envoyer du Maroc vers le Sénégal

### `GrilleTarifaire`
```
montantMin/Max      → tranche (ex: 501 MAD à 2000 MAD)
fraisFixe           → montant fixe prélevé (ex: 35 MAD)
fraisPourcentage    → pourcentage en plus si applicable
partAgence          → portion des frais reversée à l'agence (ex: 15 MAD)
partCentrale        → portion gardée par le siège (ex: 20 MAD)
calculerFrais()     → reçoit un montant, retourne les frais applicables
```

### `HistoriqueTaux`
```
devise        → quelle devise a changé
ancienTaux    → valeur avant
nouveauTaux   → valeur après
date          → quand
source        → "manuel" (admin) ou "API" (automatique)
```
> Permet de savoir : le 15 mai, l'admin a changé EUR→MAD de 10.80 à 10.85

---

## 👥 `Expediteur` et `Beneficiaire`

### `Expediteur`
```
nom/prenom      → identité
numeroPiece     → numéro de CIN ou passeport (chiffré AES-256 en base)
typePiece       → CIN, PASSEPORT, CARTE_SEJOUR
telephone       → pour le reçu SMS
pays            → pays de résidence
```

### `Beneficiaire`
```
nom/prenom              → identité
telephone               → numéro pour Mobile Money ou contact
pays                    → pays de réception
surListeSurveillance    → true si son nom matche la liste OFAC
```
> Attention : `surListeSurveillance` est mis à jour automatiquement à chaque nouveau transfert

---

## 🏦 Gestion de Caisse

### `CaisseOperation`
Chaque mouvement d'argent dans la caisse d'un agent.
```
agent      → quel agent
type       → ENVOI, RETRAIT, OUVERTURE, AJUSTEMENT
montant    → valeur de l'opération
dateHeure  → timestamp précis
reference  → numéro du transfert lié (si applicable)
```

### `ClotureCaisse`
Bilan en fin de journée.
```
agent            → quel agent a clôturé
date             → jour de clôture
soldeTheorique   → calculé automatiquement par le système
soldeSaisi       → ce que l'agent déclare avoir physiquement
ecart            → soldeSaisi - soldeTheorique (doit être 0)
signale          → true si l'agent a déclaré un écart inexpliqué
```

---

## 🔍 Conformité KYC/AML

### `ListeSurveillanceOFAC`
```
nom/prenom/alias  → identité de la personne blacklistée
pays              → nationalité ou pays d'opération
motifInscription  → pourquoi cette personne est sur la liste
dateAjout         → quand elle a été ajoutée
actif             → elle est toujours surveillée ?
ajoutManuel       → true = ajouté par l'admin / false = ajouté automatiquement
```

### `RegleAML`
Définit les règles de détection automatique.
```
nom/description         → ex : "Détection fractionnement"
seuilMontant            → ex : 5 000 MAD
seuilNbTransactions     → ex : 3 transactions
fenêtreTempsMinutes     → ex : 180 minutes (3h)
active                  → règle activée ou non
evaluer()               → analyse un transfert et retourne true si suspect
```
> Ex : Si 3 transferts de plus de 4 500 MAD en moins de 3h → règle déclenchée

### `DeclarationSoupcon`
Générée automatiquement quand une règle AML est déclenchée.
```
transfert    → le transfert qui a déclenché l'alerte
client       → le client impliqué
motif        → ex : "Fractionnement détecté : 3 transactions en 2h"
montantTotal → cumul des montants suspects
genereLe     → timestamp de génération
traitee      → l'admin a-t-il examiné cette déclaration ?
generer()    → crée et sauvegarde la déclaration + alerte l'admin
```

---

## 📢 `Notification`
```
destinataire  → quel utilisateur reçoit la notif
message       → texte de la notification
type          → SMS, EMAIL, PUSH
lue           → l'utilisateur a-t-il vu la notification ?
envoyeLe      → timestamp d'envoi
envoyer()     → déclenche l'envoi réel (SMS gateway, email SMTP)
```
> Ex : Mohamed reçoit "Votre transfert TRF-01456 a été retiré le 17/05 à 15h42"

---

## 📱 `TransfertMobileMoney`
Extension d'un `Transfert` classique vers un opérateur mobile.
```
transfert         → le transfert parent associé
operateur         → "ORANGE_MONEY", "WAVE", "M_PESA"
numeroCible       → numéro de téléphone du compte mobile money
statut            → EN_ATTENTE, ENVOYE, CONFIRME, ECHEC
referenceOperateur → identifiant retourné par l'opérateur (pour réconciliation)
envoyer()         → appelle l'API simulée de l'opérateur
reconcilier()     → compare le relevé opérateur avec les données internes
```

---

## 📊 `JournalAudit`
Trace absolument toutes les actions sensibles du système.
```
acteur       → qui a fait l'action (admin, agent, manager...)
action       → ex : "ANNULATION_TRANSFERT", "MODIFICATION_TAUX"
entiteCible  → quelle table est concernée (ex: "Transfert")
idCible      → quel enregistrement (ex: id=892)
detailAvant  → état avant modification (JSON)
detailApres  → état après modification (JSON)
dateHeure    → timestamp précis
ipAdresse    → depuis quelle adresse IP
```
> Ex : "Le 17/05 à 14h32, l'agent Nadia (IP: 192.168.1.5) a annulé le Transfert #892. Avant: STATUT=EN_ATTENTE. Après: STATUT=ANNULE"

---

## Les deux Enums

### `StatutEnum`
```
EN_ATTENTE → transfert créé, en attente de retrait
PAYE       → bénéficiaire a retiré les fonds
ANNULE     → annulé par l'agent ou le manager
EXPIRE     → délai de retrait dépassé (ex: 30 jours)
BLOQUE     → suspendu suite à un contrôle AML/KYC
```

### `RoleEnum`
```
ROLE_ADMIN    → administrateur central
ROLE_MANAGER  → responsable d'agence
ROLE_AGENT    → agent au guichet
ROLE_CLIENT   → client self-service
```
Agence         ←──── Manager         (OneToOne)
Agence         ←──── Agent[]         (OneToMany)

Transfert      ────► Expediteur      (ManyToOne)
Transfert      ────► Beneficiaire    (ManyToOne)
Transfert      ────► Agent           (ManyToOne)
Transfert      ────► Agence envoi    (ManyToOne)
Transfert      ────► Agence retrait  (ManyToOne)
Transfert      ────► Corridor        (ManyToOne)
Transfert      ────► GrilleTarifaire (ManyToOne)

Corridor       ────► Devise source   (ManyToOne)
Corridor       ────► Devise dest     (ManyToOne)

GrilleTarifaire────► Corridor        (ManyToOne)
HistoriqueTaux ────► Devise          (ManyToOne)

CaisseOperation────► Agent           (ManyToOne)
ClotureCaisse  ────► Agent           (ManyToOne)

DeclarationSoupcon ► Transfert       (ManyToOne)
DeclarationSoupcon ► RegleAML        (ManyToOne)
JournalAudit   ────► Utilisateur     (ManyToOne)

Notification   ────► Utilisateur     (ManyToOne)
TransfertMobileMoney► Transfert      (OneToOne)