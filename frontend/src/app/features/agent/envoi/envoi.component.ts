// src/app/features/agent/envoi/envoi.component.ts

import { Component, signal, computed, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';
import { TransfertService } from '../../../core/services/transfert.service';
import { DeviseService } from '../../../core/services/devise.service';
import { UserService } from '../../../core/services/user.service';
import { AuthService } from '../../../core/services/auth.service';
import { CorridorResponse } from '../../../core/models/devise/corridor-response.model';
import { FraisResult } from '../../../core/models/devise/frais-result.model';
import { CreateTransfertRequest } from '../../../core/models/transfert/create-transfert-request.model';
import { TransfertResponse } from '../../../core/models/transfert/transfert-response.model';
import { UserResponse } from '../../../core/models/user/user-response.model';
import { PieceIdentiteResponse } from '../../../core/models/piece-identite/piece-identite-response.model';



interface BeneficiaireForm {
  nom: string;
  prenom: string;
  telephone: string;
  paysReception: string;
  ville: string;
  relation: string;
}

@Component({
  selector: 'app-envoi',
  standalone: true,
  imports: [CommonModule, FormsModule, TranslateModule],
  templateUrl: './envoi.component.html',
})
export class EnvoiComponent implements OnInit {

  private transfertService = inject(TransfertService);
  private deviseService    = inject(DeviseService);
  private userService      = inject(UserService);
  private authService      = inject(AuthService);

  // ─── Stepper ──────────────────────────────────────────────────────────────
  etapeActive = signal<number>(1);
  etapes = [
    { num: 1, label: 'envoi.step1Label' },
    { num: 2, label: 'envoi.step2Label' },
    { num: 3, label: 'envoi.step3Label' },
    { num: 4, label: 'envoi.step4Label' },
  ];

  // ─── Étape 1 : Expéditeur ─────────────────────────────────────────────────
  searchQuery         = signal<string>('');
  clientsTrouves      = signal<UserResponse[]>([]);
  clientSelectionne   = signal<UserResponse | null>(null);
  piecesClient        = signal<PieceIdentiteResponse[]>([]);
  pieceSelectionneeId = signal<number | null>(null);
  rechercheEffectuee  = signal<boolean>(false);
  chargementRecherche = signal<boolean>(false);

  nouveauClient = {
    nom: '', prenom: '', typePiece: 'CIN',
    numeroPiece: '', telephone: '', pays: 'Maroc',
  };

  typesPiece = ['CIN', 'Passeport', 'Titre de sejour', 'Carte de resident'];
  paysList   = ['Maroc', 'France', 'Belgique', 'Espagne', 'Italie', 'Allemagne', 'Pays-Bas'];

  // ─── Étape 2 : Bénéficiaire ───────────────────────────────────────────────
  beneficiaire: BeneficiaireForm = {
    nom: '', prenom: '', telephone: '',
    paysReception: 'Senegal', ville: '', relation: '',
  };
  paysReceptionList = ['Senegal', 'Cote Ivoire', 'Mali', 'Guinee', 'Cameroun', 'Congo'];
  relationsList     = ['Famille', 'Conjoint(e)', 'Parent', 'Enfant', 'Soeur', 'Frere', 'Ami(e)', 'Autre'];

  // ─── Étape 3 : Montant & Corridor ─────────────────────────────────────────
  montantEnvoye       = signal<number>(2000);
  corridorSelectionne = signal<CorridorResponse | null>(null);
  corridors           = signal<CorridorResponse[]>([]);
  fraisResult         = signal<FraisResult | null>(null);
  chargementFrais     = signal<boolean>(false);
  modesReception      = ['Cash au guichet', 'Mobile Money', 'Virement bancaire'];
  modeReception       = signal<string>('Cash au guichet');

  // ─── Étape 4 : Confirmation ───────────────────────────────────────────────
  transfertCree   = signal<TransfertResponse | null>(null);
  chargementEnvoi = signal<boolean>(false);
  erreurEnvoi     = signal<string | null>(null);
  codeCopie       = signal<boolean>(false);

  // ─── Computed ─────────────────────────────────────────────────────────────
  expediteurNom = computed(() => {
    const c = this.clientSelectionne();
    return c ? `${c.prenom} ${c.nom}` : null;
  });

  beneficiaireNom = computed(() => {
    const b = this.beneficiaire;
    return b.nom && b.prenom ? `${b.prenom} ${b.nom}` : null;
  });

  corridorNom = computed(() => {
    const c = this.corridorSelectionne();
    return c ? `${c.deviseSource} → ${c.deviseDestination}` : null;
  });

  calcul = computed(() => {
    const frais    = this.fraisResult();
    const corridor = this.corridorSelectionne();
    return {
      montantEnvoye:     this.montantEnvoye(),
      deviseSource:      corridor?.deviseSource      ?? 'MAD',
      deviseDestination: corridor?.deviseDestination ?? 'XOF',
      frais:             frais?.montantFrais          ?? 0,
      commissionAgence:  frais?.partAgence            ?? 0,
      montantRecu:       frais?.montantRecu           ?? 0,
      taux:              0,
      delaiMin:          5,
    };
  });

  codeRetrait        = computed(() => this.transfertCree()?.codeRetrait     ?? '—');
  referenceTransfert = computed(() => this.transfertCree()?.numeroReference ?? '—');

  // ─── Getter/Setter pour [(ngModel)] sur signal ────────────────────────────
  get pieceSelectionneeIdValue(): number | null {
    return this.pieceSelectionneeId();
  }
  set pieceSelectionneeIdValue(val: number | null) {
    const v = val as any;
    if (v === null || v === undefined || v === '' || v === 'null') {
      this.pieceSelectionneeId.set(null);
    } else {
      this.pieceSelectionneeId.set(+v);
    }
  }

  // ─── Init ─────────────────────────────────────────────────────────────────
  ngOnInit(): void {
    this.chargerCorridors();
  }

  chargerCorridors(): void {
    this.deviseService.getAllCorridors().subscribe({
      next: (corridors) => {
        const actifs = corridors.filter(c => c.actif);
        this.corridors.set(actifs);
        if (actifs.length > 0) {
          this.corridorSelectionne.set(actifs[0]);
          this.recalculerFrais();
        }
      },
      error: (err) => console.error('Erreur chargement corridors', err),
    });
  }

  // ─── Étape 1 ──────────────────────────────────────────────────────────────
  onRechercher(): void {
    const q = this.searchQuery().trim();
    if (!q) return;

    this.chargementRecherche.set(true);
    this.rechercheEffectuee.set(false);

    this.userService.searchClients(q).subscribe({
      next: (users) => {
        this.clientsTrouves.set(users);
        this.rechercheEffectuee.set(true);
        this.chargementRecherche.set(false);
      },
      error: () => {
        this.clientsTrouves.set([]);
        this.rechercheEffectuee.set(true);
        this.chargementRecherche.set(false);
      },
    });
  }

  onSearchKeyDown(e: KeyboardEvent): void {
    if (e.key === 'Enter') this.onRechercher();
  }

  onSelectionnerClient(client: UserResponse): void {
    this.clientSelectionne.set(client);
    this.pieceSelectionneeId.set(null);
    this.userService.getPieces(client.id).subscribe({
      next: (pieces) => {
        this.piecesClient.set(pieces);
        const principale = pieces.find(p => p.principale === true) ?? pieces[0] ?? null;
        this.pieceSelectionneeId.set(principale ? principale.id : null);
      },
      error: () => {
        this.piecesClient.set([]);
        this.pieceSelectionneeId.set(null);
      },
    });
  }

  // ─── Étape 3 ──────────────────────────────────────────────────────────────
  onCorridorChange(corridorId: number | string): void {
    const id = Number(corridorId);
    const c  = this.corridors().find(x => x.id === id) ?? null;
    this.corridorSelectionne.set(c);
    this.recalculerFrais();
  }

  onMontantChange(montant: number | string): void {
    this.montantEnvoye.set(Number(montant));
    this.recalculerFrais();
  }

  recalculerFrais(): void {
    const corridor = this.corridorSelectionne();
    const montant  = this.montantEnvoye();
    if (!corridor || !montant || montant <= 0) {
      this.fraisResult.set(null);
      return;
    }
    this.chargementFrais.set(true);
    this.deviseService.calculerFrais(corridor.id, montant).subscribe({
      next: (frais) => {
        this.fraisResult.set(frais);
        this.chargementFrais.set(false);
      },
      error: () => {
        this.fraisResult.set(null);
        this.chargementFrais.set(false);
      },
    });
  }

  // ─── Navigation ───────────────────────────────────────────────────────────
  isEtapeComplete(num: number): boolean { return num < this.etapeActive(); }
  isEtapeActive(num: number):   boolean { return num === this.etapeActive(); }

  etapeSuivante(): void {
    const etape = this.etapeActive();
    this.erreurEnvoi.set(null);

    if (etape === 1) {
      if (!this.clientSelectionne()) {
        // Vérifier que le formulaire nouveau client est rempli
        const nc = this.nouveauClient;
        if (!nc.nom?.trim() || !nc.prenom?.trim() || !nc.telephone?.trim() || !nc.numeroPiece?.trim()) {
          this.erreurEnvoi.set('Veuillez sélectionner un client existant ou remplir tous les champs obligatoires (nom, prénom, téléphone, numéro de pièce).');
          return;
        }
      }
      this.etapeActive.update(v => v + 1);

    } else if (etape === 2) {
      const b = this.beneficiaire;
      if (!b.nom?.trim()) {
        this.erreurEnvoi.set('Le nom du bénéficiaire est obligatoire.');
        return;
      }
      if (!b.prenom?.trim()) {
        this.erreurEnvoi.set('Le prénom du bénéficiaire est obligatoire.');
        return;
      }
      if (!b.telephone?.trim()) {
        this.erreurEnvoi.set('Le téléphone du bénéficiaire est obligatoire.');
        return;
      }
      this.recalculerFrais();
      this.etapeActive.update(v => v + 1);

    } else if (etape === 3) {
      this.soumettreTransfert();
    }
  }

  etapePrecedente(): void {
    if (this.etapeActive() > 1) this.etapeActive.update(v => v - 1);
  }

  // ─── Soumission ───────────────────────────────────────────────────────────
  soumettreTransfert(): void {
    const client      = this.clientSelectionne();
    const corridor    = this.corridorSelectionne();
    const currentUser = this.authService.currentUser;

    if (!client) {
      this.erreurEnvoi.set('Veuillez sélectionner un client expéditeur.');
      return;
    }
    if (!corridor) {
      this.erreurEnvoi.set('Veuillez sélectionner un corridor.');
      return;
    }

    // Forcer reset du chargementFrais au cas où il serait bloqué
    this.chargementFrais.set(false);

    const pieceId = this.pieceSelectionneeId();

    // Bloquer uniquement si le client a des pièces mais aucune n'est sélectionnée
    if (pieceId == null && this.piecesClient().length > 0) {
      this.erreurEnvoi.set("Veuillez sélectionner une pièce d'identité.");
      return;
    }

    this.chargementEnvoi.set(true);
    this.erreurEnvoi.set(null);

    const request: CreateTransfertRequest = {
      clientId:              client.id,
      pieceIdentiteId:       pieceId as number,
      agentId:               currentUser?.id ?? null,
      agenceEnvoiId:         null,
      corridorId:            corridor.id,
      grilleTarifaireId:     null,
      nomBeneficiaire:       this.beneficiaire.nom,
      prenomBeneficiaire:    this.beneficiaire.prenom,
      telephoneBeneficiaire: this.beneficiaire.telephone,
      paysBeneficiaire:      this.beneficiaire.paysReception,
      montant:               this.montantEnvoye(),
    };

    this.transfertService.create(request).subscribe({
      next: (transfert) => {
        this.transfertCree.set(transfert);
        this.chargementEnvoi.set(false);
        this.etapeActive.set(4);
      },
      error: (err) => {
        this.erreurEnvoi.set(err?.error?.message ?? 'Erreur lors de la création du transfert');
        this.chargementEnvoi.set(false);
      },
    });
  }

  // ─── Utilitaires ──────────────────────────────────────────────────────────
  copierCode(): void {
    navigator.clipboard.writeText(this.codeRetrait()).then(() => {
      this.codeCopie.set(true);
      setTimeout(() => this.codeCopie.set(false), 2000);
    });
  }

  nouveauTransfert(): void {
    this.etapeActive.set(1);
    this.clientSelectionne.set(null);
    this.piecesClient.set([]);
    this.pieceSelectionneeId.set(null);
    this.rechercheEffectuee.set(false);
    this.clientsTrouves.set([]);
    this.searchQuery.set('');
    this.beneficiaire = {
      nom: '', prenom: '', telephone: '',
      paysReception: 'Senegal', ville: '', relation: '',
    };
    this.montantEnvoye.set(2000);
    this.fraisResult.set(null);
    this.transfertCree.set(null);
    this.erreurEnvoi.set(null);
    if (this.corridors().length > 0) {
      this.corridorSelectionne.set(this.corridors()[0]);
    }
  }

  formatMontant(n: number): string {
    return new Intl.NumberFormat('fr-FR').format(n ?? 0);
  }

  getExpirationDate(): string {
    const expireLe = this.transfertCree()?.expireLe;
    if (expireLe) return new Date(expireLe).toLocaleDateString('fr-FR');
    const d = new Date();
    d.setDate(d.getDate() + 30);
    return d.toLocaleDateString('fr-FR');
  }
  imprimerRecu(): void {
  const transfert = this.transfertCree();
  const calcul    = this.calcul();

  const contenu = `
    <html>
    <head>
      <title>Reçu de transfert</title>
      <style>
        body      { font-family: Arial, sans-serif; padding: 40px; color: #111; }
        h1        { color: #d97706; font-size: 22px; margin-bottom: 4px; }
        .subtitle { color: #666; font-size: 13px; margin-bottom: 30px; }
        .code     { font-size: 36px; font-weight: bold; color: #d97706;
                    letter-spacing: 6px; margin: 20px 0; font-family: monospace; }
        .ref      { font-size: 12px; color: #888; margin-bottom: 30px; }
        .grid     { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-top: 20px; }
        .bloc label { font-size: 10px; text-transform: uppercase;
                      color: #999; display: block; margin-bottom: 4px; }
        .bloc p   { font-size: 14px; font-weight: bold; margin: 0; }
        .montant  { font-size: 22px; color: #d97706; font-weight: bold; }
        hr        { border: none; border-top: 1px solid #eee; margin: 20px 0; }
        .footer   { margin-top: 40px; font-size: 11px; color: #aaa; text-align: center; }
        .badge    { display: inline-block; background: #f0fdf4; color: #16a34a;
                    border: 1px solid #bbf7d0; padding: 4px 12px;
                    border-radius: 20px; font-size: 12px; font-weight: bold; }
      </style>
    </head>
    <body>
      <h1>🏦 Okane Transfer</h1>
      <p class="subtitle">Reçu officiel de transfert d'argent</p>
      <span class="badge">✓ Transfert confirmé</span>

      <p class="code">${transfert?.codeRetrait ?? '—'}</p>
      <p class="ref">Référence : <strong>${transfert?.numeroReference ?? '—'}</strong></p>

      <hr/>

      <div class="grid">
        <div class="bloc">
          <label>Expéditeur</label>
          <p>${this.expediteurNom() ?? '—'}</p>
        </div>
        <div class="bloc">
          <label>Bénéficiaire</label>
          <p>${this.beneficiaireNom() ?? '—'}</p>
          <p style="font-size:12px;color:#888;margin-top:2px">${this.beneficiaire.telephone}</p>
        </div>
        <div class="bloc">
          <label>Pays de réception</label>
          <p>${this.beneficiaire.paysReception}</p>
        </div>
        <div class="bloc">
          <label>Mode de réception</label>
          <p>${this.modeReception()}</p>
        </div>
        <div class="bloc">
          <label>Montant envoyé</label>
          <p>${this.formatMontant(transfert?.montantEnvoye ?? calcul.montantEnvoye)} MAD</p>
        </div>
        <div class="bloc">
          <label>Frais</label>
          <p>${this.formatMontant(transfert?.frais ?? calcul.frais)} MAD</p>
        </div>
        <div class="bloc">
          <label>Montant reçu</label>
          <p class="montant">
            ${this.formatMontant(transfert?.montantRecu ?? calcul.montantRecu)}
            ${transfert?.deviseReception ?? calcul.deviseDestination}
          </p>
        </div>
        <div class="bloc">
          <label>Valable jusqu'au</label>
          <p>${this.getExpirationDate()}</p>
        </div>
      </div>

      <hr/>

      <div class="footer">
        Imprimé le ${new Date().toLocaleDateString('fr-FR')}
        à ${new Date().toLocaleTimeString('fr-FR')}
        &nbsp;—&nbsp; Conservez ce reçu, il est indispensable pour le retrait.
      </div>
    </body>
    </html>
  `;

  const fenetre = window.open('', '_blank', 'width=750,height=650');
  if (fenetre) {
    fenetre.document.write(contenu);
    fenetre.document.close();
    fenetre.focus();
    setTimeout(() => {
      fenetre.print();
      fenetre.close();
    }, 600);
  }
}
}