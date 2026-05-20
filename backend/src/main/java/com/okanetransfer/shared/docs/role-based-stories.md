# Détail complet des actions par rôle — avec exemples concrets

---

## 👑 ROLE_ADMIN — Administrateur Central

### Gestion des Devises & Pays

- **Créer une devise**
  > Ex : L'admin ajoute le *Dirham Marocain (MAD)* avec son symbole et son code ISO

- **Modifier une devise**
  > Ex : L'admin met à jour le nom affiché de *"Dollar"* en *"Dollar Américain (USD)"*

- **Supprimer une devise**
  > Ex : L'admin supprime une devise obsolète qui n'est plus utilisée dans aucun corridor

- **Activer un corridor de transfert**
  > Ex : L'admin active le corridor *Maroc → Sénégal* pour permettre les envois vers Dakar

- **Désactiver un corridor**
  > Ex : L'admin suspend temporairement le corridor *France → Mali* suite à une restriction réglementaire

- **Mettre à jour les taux manuellement**
  > Ex : L'admin saisit que 1 EUR = 10.85 MAD pour ce jour

- **Mise à jour automatique via API**
  > Ex : Chaque matin à 8h, le système appelle une API externe (ex: exchangeratesapi.io) et met à jour tous les taux automatiquement

- **Consulter l'historique des taux**
  > Ex : L'admin voit que le 15 mai, 1 USD valait 9.80 MAD, et le 16 mai il valait 9.95 MAD, mis à jour par l'API

---

### Gestion des Frais & Commissions

- **Configurer une grille tarifaire par tranche**
  > Ex : 0–500 MAD = 20 MAD de frais / 501–2000 MAD = 35 MAD / 2001–5000 MAD = 60 MAD

- **Paramétrer la répartition agence / centrale**
  > Ex : Sur 35 MAD de frais, 15 MAD vont à l'agence qui a traité l'opération, 20 MAD vont au siège central

- **Simuler des frais avant validation**
  > Ex : L'admin teste : *"Si un client envoie 1200 MAD vers le Sénégal, combien de frais ?"* → le système affiche 35 MAD avant que la grille soit officiellement publiée

- **Exporter la grille tarifaire**
  > Ex : L'admin télécharge un fichier PDF listant toutes les tranches et frais pour les afficher en agence

---

### Gestion des Agences

- **Créer une agence**
  > Ex : L'admin crée l'agence *"Okane Casablanca Centre"*, adresse : Bd Mohammed V, Casablanca, plafond journalier : 200 000 MAD

- **Affecter un responsable**
  > Ex : L'admin désigne *Karim Benjelloun* comme ROLE_MANAGER de l'agence Casablanca

- **Affecter des agents**
  > Ex : L'admin affecte *Nadia El Fassi* et *Youssef Amrani* comme agents de la même agence

- **Suspendre une agence**
  > Ex : L'admin suspend l'agence de Marrakech suite à un contrôle, aucun transfert ne peut être traité depuis cette agence

- **Consulter les performances**
  > Ex : L'admin voit que l'agence de Rabat a traité 450 transferts ce mois pour un total de 1.2M MAD

---

### Supervision & Rapports

- **Volume journalier par corridor**
  > Ex : Ce jour, 120 transferts Maroc→France pour 450 000 MAD, 45 transferts Maroc→Sénégal pour 180 000 MAD

- **Chiffre d'affaires et commissions**
  > Ex : Ce mois, le système a généré 85 000 MAD de commissions, dont 32 000 MAD reversés aux agences

- **Alertes automatiques**
  > Ex : Le système envoie une alerte : *"L'agence de Fès a dépassé son plafond journalier de 200 000 MAD"*

- **Journal d'audit**
  > Ex : L'admin voit que le 17 mai à 14h32, l'agent *Nadia El Fassi* a annulé le transfert #TRF-00892

---

### Conformité KYC/AML

- **Tableau de bord de conformité**
  > Ex : L'admin voit 3 transactions flagguées aujourd'hui, dont 1 impliquant un bénéficiaire dont le nom correspond à la liste de surveillance

- **Consulter les déclarations de soupçon**
  > Ex : Le système a généré automatiquement une déclaration car un client a envoyé 3 fois 4 900 MAD en 2 heures (juste sous le seuil de 5 000 MAD)

- **Gérer la liste de surveillance**
  > Ex : L'admin ajoute le nom *"Ahmed Fictif"* à la liste noire OFAC fictive du système

---

### Gestion des Utilisateurs

- **Créer un compte manager**
  > Ex : L'admin crée le compte de *Sara Bennani* avec le rôle ROLE_MANAGER et l'affecte à l'agence d'Agadir

- **Désactiver un compte**
  > Ex : L'admin désactive le compte d'un agent qui a quitté l'entreprise — il ne peut plus se connecter

- **Modifier un rôle**
  > Ex : L'admin passe *Youssef Amrani* de ROLE_AGENT à ROLE_MANAGER après une promotion

---

## 🏢 ROLE_MANAGER — Responsable d'Agence

### Gestion de son Équipe

- **Voir la liste de ses agents**
  > Ex : Le manager de Casablanca voit uniquement ses 4 agents : Nadia, Youssef, Hassan, Fatima — pas ceux des autres agences

- **Désactiver un agent**
  > Ex : Le manager suspend temporairement le compte de Hassan qui est en congé maladie

- **Consulter les performances individuelles**
  > Ex : Le manager voit que Nadia a traité 85 transferts cette semaine pour un total de 320 000 MAD, contre 60 transferts pour Youssef

---

### Validation des Opérations Sensibles

- **Valider une opération dépassant le seuil**
  > Ex : Un client veut envoyer 45 000 MAD en une seule opération — l'agent ne peut pas valider seul, il soumet à son manager qui approuve après vérification

- **Débloquer un transfert suspendu**
  > Ex : Le transfert #TRF-01245 a été bloqué automatiquement car le nom du bénéficiaire ressemblait à un nom sur liste de surveillance — le manager vérifie manuellement et débloque

---

### Rapports d'Agence

- **Rapport de son agence**
  > Ex : Le manager de Rabat consulte : 230 transferts ce mois, 950 000 MAD traités, 18 500 MAD de commissions générées pour son agence

- **Volume par agent**
  > Ex : Le manager voit que Fatima a généré 6 200 MAD de commissions ce mois, soit le meilleur score de l'agence

- **Exporter un rapport**
  > Ex : Le manager télécharge le rapport mensuel en PDF pour le transmettre à l'admin central

---

### Suivi des Plafonds

- **Voir le plafond en temps réel**
  > Ex : À 15h, l'agence a déjà traité 185 000 MAD sur son plafond de 200 000 MAD — il reste 15 000 MAD disponibles

- **Alerte de plafond**
  > Ex : Le manager reçoit une notification : *"Attention, votre agence a atteint 90% de son plafond journalier"*

- **Demander une révision du plafond**
  > Ex : Le manager envoie une demande à l'admin pour passer le plafond de 200 000 MAD à 300 000 MAD pour le mois de Ramadan

---

## 🧑‍💼 ROLE_AGENT — Agent en Agence

### Enregistrement d'un Envoi

- **Saisir les infos de l'expéditeur**
  > Ex : L'agent saisit — Nom : *Mohamed Alami*, CIN : *BE123456*, Téléphone : *0661234567*, Pays : *Maroc*

- **Saisir les infos du bénéficiaire**
  > Ex : L'agent saisit — Nom : *Aminata Diallo*, Téléphone : *+221 77 123 45 67*, Pays de réception : *Sénégal*

- **Renseigner le montant et la devise**
  > Ex : L'agent saisit 2 000 MAD à envoyer vers le Sénégal en XOF

- **Voir le calcul automatique des frais**
  > Ex : Le système affiche automatiquement : *Frais : 35 MAD — Montant net reçu par Aminata : 215 400 XOF (selon le taux du jour)*

- **Générer le code de retrait**
  > Ex : Après validation du paiement par le client, le système génère le code *K7X2-M9QA* que Mohamed donne à Aminata par téléphone

- **Envoyer le reçu**
  > Ex : L'agent clique sur *"Envoyer par SMS"* → Mohamed reçoit un SMS avec le code de retrait et le récapitulatif du transfert

---

### Paiement d'un Transfert (Retrait)

- **Rechercher par code de retrait**
  > Ex : Aminata arrive à l'agence de Dakar et donne le code *K7X2-M9QA* — l'agent le saisit et le transfert apparaît immédiatement

- **Rechercher par téléphone**
  > Ex : Aminata a oublié le code mais donne son numéro *+221 77 123 45 67* — l'agent retrouve le transfert via ce numéro

- **Vérifier le statut**
  > Ex : Le système affiche le statut *EN_ATTENTE* — le transfert est prêt à être payé

- **Contrôler la pièce d'identité**
  > Ex : L'agent saisit le numéro de la carte d'identité d'Aminata *SN-789456* avant de pouvoir confirmer le paiement

- **Confirmer le paiement**
  > Ex : L'agent clique sur *"Confirmer le paiement"* → le statut passe à *PAYÉ*, la caisse de l'agent est débitée du montant en XOF

- **Imprimer le reçu**
  > Ex : L'agent imprime le reçu que signe Aminata comme preuve de réception des fonds

---

### Gestion de Caisse

- **Voir le solde en temps réel**
  > Ex : À 11h, l'agent voit que sa caisse contient 42 500 MAD après 8 opérations du matin

- **Historique des opérations du jour**
  > Ex : L'agent voit la liste de ses 8 opérations : 5 envois et 3 retraits avec les montants et heures

- **Clôture de caisse**
  > Ex : En fin de journée, l'agent clique sur *"Clôturer la caisse"* — le système compare le solde théorique (calculé) au solde saisi par l'agent

- **Signaler un écart**
  > Ex : L'agent constate qu'il a 200 MAD de moins que prévu — il signale l'écart avec une note explicative pour que le manager soit informé

---

## 👤 ROLE_CLIENT — Client (Self-Service)

### Inscription & Connexion

- **S'inscrire**
  > Ex : *Aminata Diallo* crée son compte avec email *<aminata@gmail.com>*, mot de passe sécurisé, et numéro de téléphone *+221 77 123 45 67*

- **Se connecter avec 2FA**
  > Ex : Aminata saisit son email + mot de passe → le système envoie un code OTP à 6 chiffres par SMS → elle le saisit pour accéder à son espace

---

### Suivi des Transferts

- **Tableau de bord**
  > Ex : Aminata voit en page d'accueil ses 3 derniers transferts reçus avec leurs statuts : 2 *PAYÉS*, 1 *EN_ATTENTE*

- **Suivi par numéro de référence**
  > Ex : Mohamed saisit la référence *TRF-01456* et voit en temps réel : *"Votre transfert est EN_ATTENTE de retrait à Dakar"*

- **Historique avec filtres**
  > Ex : Mohamed filtre ses transferts du mois de janvier vers le Sénégal avec statut *PAYÉ* → il obtient une liste de 4 transferts pour un total de 8 000 MAD envoyés

---

### Gestion du Profil

- **Modifier ses informations**
  > Ex : Mohamed change son numéro de téléphone de *0661234567* à *0698765432* dans son profil

- **Gérer les notifications**
  > Ex : Aminata active les notifications email et désactive les SMS pour recevoir les alertes de statut

- **Recevoir une notification**
  > Ex : Dès que l'agent de Dakar confirme le paiement, Mohamed reçoit automatiquement un email : *"Votre transfert TRF-01456 a bien été reçu par Aminata Diallo le 17/05/2026 à 15h42"*

- **Droit à l'effacement (RGPD)**
  > Ex : Mohamed soumet une demande de suppression de son compte et de toutes ses données personnelles — le système pseudonymise ses données dans les 30 jours

---

### Chatbot

- **Poser une question sur les frais**
  > Ex : Mohamed tape *"Combien ça coûte d'envoyer 3000 MAD au Sénégal ?"* → le chatbot répond *"Les frais pour ce montant sont de 60 MAD, le bénéficiaire recevra environ 323 100 XOF"*

- **Vérifier un statut via chatbot**
  > Ex : Aminata tape *"Où en est mon transfert ?"* → le chatbot lui demande son numéro de référence et affiche le statut en temps réel

- **Changer de langue**
  > Ex : Aminata passe le chatbot en arabe et repose sa question — le bot répond en arabe

- **Escalade vers agent humain**
  > Ex : Mohamed pose une question complexe sur un remboursement → le chatbot détecte qu'il ne peut pas répondre et transfère la conversation à un agent humain disponible

---

## Récap en une phrase par rôle

```
ADMIN    → Il configure, surveille et contrôle l'ensemble du système
MANAGER  → Il gère son agence, son équipe et valide les cas complexes
AGENT    → Il traite les envois et retraits au guichet au quotidien
CLIENT   → Il suit ses transferts depuis chez lui en toute autonomie
```

Tu veux passer aux diagrammes UML ou au schéma de base de données maintenant ?
