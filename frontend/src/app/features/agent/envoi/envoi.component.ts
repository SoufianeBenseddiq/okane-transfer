// src/app/features/agent/envoi/envoi.component.ts

import { Component, signal, computed, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';
import { TransfertService } from '../../../core/services/transfert.service';
import { DeviseService } from '../../../core/services/devise.service';
import { PaysService } from '../../../core/services/pays.service';
import { UserService } from '../../../core/services/user.service';
import { AuthService } from '../../../core/services/auth.service';
import { DeviseResponse } from '../../../core/models/devise/devise-response.model';
import { CorridorResponse } from '../../../core/models/devise/corridor-response.model';
import { PaysResponse } from '../../../core/models/pays/pays-response.model';
import { FraisResult } from '../../../core/models/devise/frais-result.model';
import { CreateTransfertRequest } from '../../../core/models/transfert/create-transfert-request.model';
import { CreateTransfertAvecNouveauClientRequest } from '../../../core/models/transfert/create-transfert-avec-nouveau-client-request.model';
import { SendTransfertReceiptEmailRequest } from '../../../core/models/transfert/send-transfert-receipt-email-request.model';
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
  private paysService      = inject(PaysService);
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

  // ─── Étape 1 : Corridor + Expéditeur ─────────────────────────────────────
  searchQuery         = signal<string>('');
  clientsTrouves      = signal<UserResponse[]>([]);
  clientSelectionne   = signal<UserResponse | null>(null);
  piecesClient        = signal<PieceIdentiteResponse[]>([]);
  pieceSelectionneeId = signal<number | null>(null);
  rechercheEffectuee  = signal<boolean>(false);
  chargementRecherche = signal<boolean>(false);

  nouveauClient = {
    nom: '', prenom: '', email: '', motDePasse: '', typePiece: 'CIN',
    numeroPiece: '', telephone: '', pays: '',
  };
  nouveauClientNomSignal = signal<string | null>(null);

  typesPiece = ['CIN', 'Passeport', 'Titre de sejour', 'Carte de resident'];

  // ─── Étape 2 : Bénéficiaire ───────────────────────────────────────────────
  beneficiaire: BeneficiaireForm = {
    nom: '', prenom: '', telephone: '',
    paysReception: '', ville: '', relation: '',
  };
  beneficiaireNomSignal = signal<string | null>(null);
  relationsList = ['Famille', 'Conjoint(e)', 'Parent', 'Enfant', 'Soeur', 'Frere', 'Ami(e)', 'Autre'];

  erreursForm: Record<string, boolean> = {};

  // ─── Étape 3 : Montant ────────────────────────────────────────────────────
  montantEnvoye       = signal<number>(2000);
  corridorSelectionne  = signal<CorridorResponse | null>(null);
  corridors            = signal<CorridorResponse[]>([]);
  corridorSearch       = signal<string>('');
  pays                = signal<PaysResponse[]>([]);
  devisesParCode      = signal<Record<string, DeviseResponse>>({});
  fraisResult         = signal<FraisResult | null>(null);
  fraisError          = signal<string | null>(null);
  chargementFrais     = signal<boolean>(false);
  modesReception      = ['Cash au guichet', 'Mobile Money', 'Virement bancaire'];
  modeReception       = signal<string>('Cash au guichet');

  // ─── Étape 4 : Confirmation ───────────────────────────────────────────────
  transfertCree     = signal<TransfertResponse | null>(null);
  chargementEnvoi   = signal<boolean>(false);
  erreurEnvoi       = signal<string | null>(null);
  codeCopie         = signal<boolean>(false);
  envoiEmailEnCours = signal<boolean>(false);
  messageEmail      = signal<string | null>(null);
  erreurEmail       = signal<string | null>(null);

  // ─── Computed ─────────────────────────────────────────────────────────────
  sourceCountry = computed(() => this.corridorSelectionne()?.paysSource ?? null);
  destCountry   = computed(() => this.corridorSelectionne()?.paysDestination ?? null);

  corridorsFiltres = computed(() => {
    const q = this.corridorSearch().trim().toLowerCase();
    if (!q) return this.corridors();
    return this.corridors().filter(c => {
      const tokens = [
        c.paysSource?.nom, c.paysDestination?.nom,
        c.paysSource?.codeIso, c.paysDestination?.codeIso,
        c.deviseSource, c.deviseDestination,
      ];
      return tokens.some(t => t?.toLowerCase().includes(q));
    });
  });

  expediteurNom = computed(() => {
    const c = this.clientSelectionne();
    if (c) return `${c.prenom} ${c.nom}`;
    return this.nouveauClientNomSignal();
  });

  beneficiaireNom = computed(() => this.beneficiaireNomSignal());

  corridorNom = computed(() => {
    const c = this.corridorSelectionne();
    if (!c) return null;
    const src = c.paysSource?.nom ?? c.deviseSource;
    const dst = c.paysDestination?.nom ?? c.deviseDestination;
    return `${src} → ${dst}`;
  });

  clientsAffiches = computed(() => {
    const list = this.clientsTrouves();
    const sel  = this.clientSelectionne();
    if (!sel) return list;
    return list.filter(c => c.id !== sel.id);
  });

  calcul = computed(() => {
    const frais    = this.fraisResult();
    const corridor = this.corridorSelectionne();
    const deviseSource      = corridor ? this.devisesParCode()[corridor.deviseSource] : null;
    const deviseDestination = corridor ? this.devisesParCode()[corridor.deviseDestination] : null;
    const taux = frais?.taux ?? (deviseSource && deviseDestination
      ? +(deviseSource.tauxVersEuro / deviseDestination.tauxVersEuro).toFixed(4)
      : null);
    const montantFrais      = frais?.montantFrais ?? 0;
    const commissionAgence  = frais?.partAgence ?? 0;
    const montantNetSource  = frais
      ? this.montantEnvoye() - montantFrais - commissionAgence
      : null;
    const montantRecu = montantNetSource == null
      ? frais?.montantRecu ?? 0
      : taux != null
        ? +(montantNetSource * taux).toFixed(2)
        : montantNetSource;
    return {
      montantEnvoye:     this.montantEnvoye(),
      deviseSource:      corridor?.deviseSource      ?? null,
      deviseDestination: corridor?.deviseDestination ?? null,
      frais:             montantFrais,
      commissionAgence,
      montantRecu,
      montantNetMAD:     montantNetSource ?? 0, // net amount in source devise (MAD) = montantRecu in MAD before conversion
      taux,
      delaiMin:          frais?.delaiMin ?? 5,
    };
  });

  codeRetrait        = computed(() => this.transfertCree()?.codeRetrait     ?? '—');
  referenceTransfert = computed(() => this.transfertCree()?.numeroReference ?? '—');

  // ─── Getter/Setter pour [(ngModel)] sur signal ────────────────────────────
  get pieceSelectionneeIdValue(): number | null { return this.pieceSelectionneeId(); }
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
    this.deviseService.getAllDevises().subscribe({
      next: (devises) => {
        const map = devises.reduce((acc, d) => { acc[d.code] = d; return acc; }, {} as Record<string, DeviseResponse>);
        this.devisesParCode.set(map);
      },
    });
    this.paysService.getAll().subscribe({ next: (p) => this.pays.set(p) });
    this.deviseService.getAllCorridors().subscribe({
      next: (corridors) => {
        const actifs = corridors.filter(c => c.actif && c.paysSource && c.paysDestination);
        this.corridors.set(actifs);
      },
    });
  }

  // ─── Corridor selection (step 1) ──────────────────────────────────────────
  onCorridorSelect(corridor: CorridorResponse): void {
    this.corridorSelectionne.set(corridor);
    // Pre-fill sender country from corridor source
    this.nouveauClient.pays     = corridor.paysSource?.nom ?? '';
    this.nouveauClient.telephone = '';
    // Pre-fill beneficiary country from corridor destination (read-only)
    this.beneficiaire.paysReception = corridor.paysDestination?.nom ?? '';
    this.beneficiaire.telephone     = '';
    this.fraisResult.set(null);
  }

  // ─── Phone helpers ────────────────────────────────────────────────────────
  phonePrefix(country: PaysResponse | null): string {
    return country?.indicatifTel ?? '';
  }

  phonePlaceholder(country: PaysResponse | null): string {
    return country?.formatTel ?? '00 00 00 00';
  }

  phoneLength(country: PaysResponse | null): number {
    return country?.longueurTel ?? 9;
  }

  formatPhoneNumber(value: string): string {
    const digits = value.replace(/\D/g, '');
    return digits.match(/.{1,2}/g)?.join(' ') ?? digits;
  }

  onPhoneInput(value: string, field: 'nouveauClient' | 'beneficiaire'): void {
    if (field === 'nouveauClient') {
      this.nouveauClient.telephone = this.formatPhoneNumber(value);
    } else {
      this.beneficiaire.telephone = this.formatPhoneNumber(value);
    }
  }

  private telephoneComplet(telephone: string, country: PaysResponse | null): string {
    const prefix = country?.indicatifTel ?? '';
    return `${prefix}${telephone}`.replace(/\s/g, '');
  }

  private validatePhone(telephone: string, country: PaysResponse | null): boolean {
    const digits = telephone.replace(/\s/g, '');
    const expected = country?.longueurTel ?? 9;
    return /^\d+$/.test(digits) && digits.length === expected;
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
    if (e.key === 'Enter' && this.searchQuery().trim().length >= 1) {
      this.onRechercher();
    }
  }

  onSelectionnerClient(client: UserResponse): void {
    this.clientSelectionne.set(client);
    this.pieceSelectionneeId.set(null);
    this.erreursForm = {};
    this.userService.getPieces(client.id).subscribe({
      next: (pieces) => {
        this.piecesClient.set(pieces);
        const principale = pieces.find(p => p.principale === true) ?? pieces[0] ?? null;
        this.pieceSelectionneeId.set(principale ? principale.id : null);
      },
      error: () => { this.piecesClient.set([]); this.pieceSelectionneeId.set(null); },
    });
  }

  onChangeToNewClient(): void {
    this.clientSelectionne.set(null);
    this.piecesClient.set([]);
    this.pieceSelectionneeId.set(null);
    this.nouveauClient = {
      nom: '', prenom: '', email: '', motDePasse: '', typePiece: 'CIN',
      numeroPiece: '', telephone: '',
      pays: this.corridorSelectionne()?.paysSource?.nom ?? '',
    };
    this.nouveauClientNomSignal.set(null);
    this.searchQuery.set('');
    this.clientsTrouves.set([]);
    this.rechercheEffectuee.set(false);
    this.erreursForm = {};
  }

  // ─── Étape 3 ──────────────────────────────────────────────────────────────
  onMontantChange(montant: number | string): void {
    const value = Number(montant);
    this.montantEnvoye.set(value);
    if (Number.isFinite(value) && value > 0) delete this.erreursForm['montant'];
    this.recalculerFrais();
  }

  private montantEstValide(): boolean {
    const montant = this.montantEnvoye();
    return Number.isFinite(montant) && montant > 0;
  }

  private typePieceApi(typePiece: string): 'CIN' | 'PASSEPORT' | 'CARTE_SEJOUR' | 'PERMIS' {
    const n = typePiece.toLowerCase();
    if (n === 'passeport') return 'PASSEPORT';
    if (n === 'titre de sejour' || n === 'carte de resident') return 'CARTE_SEJOUR';
    if (n === 'permis') return 'PERMIS';
    return 'CIN';
  }

  private messageErreur(err: any): string {
    const error = err?.error;
    if (typeof error === 'string') return error;
    if (error?.message) return error.message;
    if (error && typeof error === 'object') {
      const first = Object.values(error).find(Boolean);
      if (first) return String(first);
    }
    return 'Erreur lors de la création du transfert';
  }

  recalculerFrais(): void {
    const corridor = this.corridorSelectionne();
    const montant  = this.montantEnvoye();
    if (!corridor || !montant || montant <= 0) {
      this.fraisResult.set(null);
      this.fraisError.set(null);
      return;
    }
    this.chargementFrais.set(true);
    this.fraisError.set(null);
    this.deviseService.calculerFrais(corridor.id, montant).subscribe({
      next: (frais) => {
        this.fraisResult.set(frais);
        this.fraisError.set(null);
        this.chargementFrais.set(false);
      },
      error: (err) => {
        this.fraisResult.set(null);
        this.fraisError.set(
          err?.error?.message ?? 'Aucune tranche tarifaire pour ce montant sur ce corridor.'
        );
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
    this.erreursForm = {};
    let hasError = false;

    if (etape === 1) {
      // Must have a corridor selected
      if (!this.corridorSelectionne()) {
        this.erreursForm['corridor'] = true;
        return;
      }

      if (!this.clientSelectionne()) {
        const nc = this.nouveauClient;
        if (!nc.nom?.trim())    { this.erreursForm['nom'] = true;    hasError = true; }
        if (!nc.prenom?.trim()) { this.erreursForm['prenom'] = true; hasError = true; }
        if (!nc.email?.trim())  { this.erreursForm['email'] = true;  hasError = true; }
        else if (!/^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/.test(nc.email.trim())) {
          this.erreursForm['email_format'] = true; hasError = true;
        }
        if (!nc.motDePasse?.trim()) { this.erreursForm['motDePasse'] = true; hasError = true; }
        else if (!/^(?=.*[A-Z])(?=.*[0-9]).{8,}$/.test(nc.motDePasse)) {
          this.erreursForm['motDePasse_strength'] = true; hasError = true;
        }
        if (!nc.telephone?.trim()) {
          this.erreursForm['telephone'] = true; hasError = true;
        } else if (!this.validatePhone(nc.telephone, this.sourceCountry())) {
          this.erreursForm['telephone_format'] = true; hasError = true;
        }
        if (!nc.numeroPiece?.trim()) { this.erreursForm['numeroPiece'] = true; hasError = true; }
        if (hasError) return;
      }
      if (!this.clientSelectionne()) {
        const nc = this.nouveauClient;
        this.nouveauClientNomSignal.set(`${nc.prenom} ${nc.nom}`.trim() || null);
      }
      this.etapeActive.update(v => v + 1);

    } else if (etape === 2) {
      const b = this.beneficiaire;
      if (!b.nom?.trim())    { this.erreursForm['b_nom'] = true;    hasError = true; }
      if (!b.prenom?.trim()) { this.erreursForm['b_prenom'] = true; hasError = true; }
      if (!b.telephone?.trim()) {
        this.erreursForm['b_telephone'] = true; hasError = true;
      } else if (!this.validatePhone(b.telephone, this.destCountry())) {
        this.erreursForm['b_telephone_format'] = true; hasError = true;
      }
      if (hasError) return;
      this.beneficiaireNomSignal.set(`${b.prenom} ${b.nom}`.trim() || null);
      this.recalculerFrais();
      this.etapeActive.update(v => v + 1);

    } else if (etape === 3) {
      if (!this.montantEstValide()) { this.erreursForm['montant'] = true; return; }
      if (!this.fraisResult()) {
        this.erreurEnvoi.set(
          this.fraisError() ?? 'Aucune tranche tarifaire configurée pour ce montant. Modifiez le montant.'
        );
        return;
      }
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

    if (!corridor) { this.erreurEnvoi.set('Veuillez sélectionner un corridor.'); return; }
    if (!this.montantEstValide()) { this.erreursForm['montant'] = true; return; }

    this.chargementFrais.set(false);
    const pieceId = this.pieceSelectionneeId();
    if (client && pieceId == null && this.piecesClient().length > 0) {
      this.erreurEnvoi.set("Veuillez sélectionner une pièce d'identité.");
      return;
    }

    this.chargementEnvoi.set(true);
    this.erreurEnvoi.set(null);

    if (!client) {
      const nc = this.nouveauClient;
      const request: CreateTransfertAvecNouveauClientRequest = {
        nouveauClient: {
          nom: nc.nom.trim(), prenom: nc.prenom.trim(),
          email: nc.email.trim(), motDePasse: nc.motDePasse,
          telephone: this.telephoneComplet(nc.telephone, this.sourceCountry()),
          pays: nc.pays,
        },
        pieceIdentite: {
          numero: nc.numeroPiece.trim(),
          type: this.typePieceApi(nc.typePiece),
          paysEmetteur: nc.pays,
        },
        agentId:               currentUser?.id ?? null,
        agenceEnvoiId:         null,
        corridorId:            corridor.id,
        grilleTarifaireId:     this.fraisResult()?.grilleTarifaireId ?? null,
        nomBeneficiaire:       this.beneficiaire.nom,
        prenomBeneficiaire:    this.beneficiaire.prenom,
        telephoneBeneficiaire: this.telephoneComplet(this.beneficiaire.telephone, this.destCountry()),
        paysBeneficiaire:      this.beneficiaire.paysReception,
        montant:               this.montantEnvoye(),
      };
      this.transfertService.createAvecNouveauClient(request).subscribe({
        next: (transfert) => {
          this.transfertCree.set(transfert);
          this.chargementEnvoi.set(false);
          this.messageEmail.set(null);
          this.erreurEmail.set(null);
          this.etapeActive.set(4);
        },
        error: (err) => { this.erreurEnvoi.set(this.messageErreur(err)); this.chargementEnvoi.set(false); },
      });
      return;
    }

    const request: CreateTransfertRequest = {
      clientId:              client.id,
      pieceIdentiteId:       pieceId as number,
      agentId:               currentUser?.id ?? null,
      agenceEnvoiId:         null,
      corridorId:            corridor.id,
      grilleTarifaireId:     this.fraisResult()?.grilleTarifaireId ?? null,
      nomBeneficiaire:       this.beneficiaire.nom,
      prenomBeneficiaire:    this.beneficiaire.prenom,
      telephoneBeneficiaire: this.telephoneComplet(this.beneficiaire.telephone, this.destCountry()),
      paysBeneficiaire:      this.beneficiaire.paysReception,
      montant:               this.montantEnvoye(),
    };

    this.transfertService.create(request).subscribe({
      next: (transfert) => {
        this.transfertCree.set(transfert);
        this.chargementEnvoi.set(false);
        this.messageEmail.set(null);
        this.erreurEmail.set(null);
        this.etapeActive.set(4);
      },
      error: (err) => { this.erreurEnvoi.set(this.messageErreur(err)); this.chargementEnvoi.set(false); },
    });
  }

  // ─── Utilitaires ──────────────────────────────────────────────────────────
  copierCode(): void {
    navigator.clipboard.writeText(this.codeRetrait()).then(() => {
      this.codeCopie.set(true);
      setTimeout(() => this.codeCopie.set(false), 2000);
    });
  }

  envoyerRecuParEmail(): void {
    const transfert = this.transfertCree();
    if (!transfert?.codeRetrait) {
      this.erreurEmail.set('Aucun reçu de transfert disponible.');
      return;
    }
    const destinataireEmail = (this.clientSelectionne()?.email ?? this.nouveauClient.email ?? '').trim();
    if (!destinataireEmail) {
      this.erreurEmail.set("Aucune adresse email n'est disponible pour cet expéditeur.");
      return;
    }
    const request: SendTransfertReceiptEmailRequest = { codeRetrait: transfert.codeRetrait, destinataireEmail };
    this.envoiEmailEnCours.set(true);
    this.erreurEmail.set(null);
    this.messageEmail.set(null);
    this.transfertService.envoyerRecuParEmail(request).subscribe({
      next: () => { this.messageEmail.set(`Reçu envoyé à ${destinataireEmail}`); this.envoiEmailEnCours.set(false); },
      error: (err) => { this.erreurEmail.set(this.messageErreur(err)); this.envoiEmailEnCours.set(false); },
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
    this.nouveauClient = {
      nom: '', prenom: '', email: '', motDePasse: '', typePiece: 'CIN',
      numeroPiece: '', telephone: '', pays: '',
    };
    this.nouveauClientNomSignal.set(null);
    this.beneficiaire = { nom: '', prenom: '', telephone: '', paysReception: '', ville: '', relation: '' };
    this.beneficiaireNomSignal.set(null);
    this.montantEnvoye.set(2000);
    this.fraisResult.set(null);
    this.fraisError.set(null);
    this.transfertCree.set(null);
    this.erreurEnvoi.set(null);
    this.messageEmail.set(null);
    this.erreurEmail.set(null);
    this.corridorSelectionne.set(null);
  }

  getFlagEmoji(codeIso?: string | null): string {
    if (!codeIso || codeIso.length !== 2) return '🏳️';
    const code = codeIso.toUpperCase();
    return code.split('').map(c => String.fromCodePoint(0x1F1E6 + c.charCodeAt(0) - 65)).join('');
  }

  formatMontant(n: number): string {
    return new Intl.NumberFormat('fr-FR').format(n ?? 0);
  }

  formatTaux(n: number | null | undefined): string {
    if (n === null || n === undefined) return '—';
    return new Intl.NumberFormat('fr-FR', { minimumFractionDigits: 2, maximumFractionDigits: 2 }).format(n);
  }

  getExpirationDate(): string {
    const expireLe = this.transfertCree()?.expireLe;
    if (expireLe) return new Date(expireLe).toLocaleDateString('fr-FR');
    const d = new Date();
    d.setDate(d.getDate() + 30);
    return d.toLocaleDateString('fr-FR');
  }

  private formatReceiptPhone(telephone?: string | null): string {
    if (!telephone) return '—';
    const digits = telephone.trim().replace(/\s+/g, '');
    if (digits.startsWith('+')) {
      const m = digits.match(/^\+(\d{1,3})/);
      if (!m) return telephone.trim();
      const rest = digits.slice(m[1].length + 1).replace(/\D/g, '');
      return `+${m[1]} ${rest.match(/.{1,2}/g)?.join(' ') ?? rest}`.trim();
    }
    return digits.replace(/(\d{2})(?=\d)/g, '$1 ').trim();
  }

  imprimerRecu(): void {
    const transfert = this.transfertCree();
    const calcul    = this.calcul();
    const contenu   = this.buildReceiptHtml(transfert, calcul);
    const fenetre   = window.open('', '_blank', 'width=750,height=650');
    if (fenetre) {
      fenetre.document.write(contenu);
      fenetre.document.close();
      fenetre.focus();
      setTimeout(() => { fenetre.print(); fenetre.close(); }, 600);
    }
  }

  async shareRecu(): Promise<void> {
    const transfert = this.transfertCree();
    const calcul    = this.calcul();
    const html      = this.buildReceiptHtml(transfert, calcul);
    try {
      const blob = new Blob([html], { type: 'text/html' });
      const file = new File([blob], `recu-${transfert?.numeroReference ?? Date.now()}.html`, { type: 'text/html' });
      // @ts-ignore
      if (navigator.canShare && navigator.canShare({ files: [file] })) {
        // @ts-ignore
        await navigator.share({ files: [file], title: 'Reçu de transfert', text: 'Reçu Okane Transfer' });
        return;
      }
    } catch (e) { /* fallback */ }
    const a = document.createElement('a');
    const url = URL.createObjectURL(new Blob([html], { type: 'text/html' }));
    a.href = url;
    a.download = `recu-${transfert?.numeroReference ?? Date.now()}.html`;
    document.body.appendChild(a);
    a.click();
    a.remove();
    setTimeout(() => URL.revokeObjectURL(url), 5000);
  }

  private buildReceiptHtml(transfert: any, calcul: any): string {
    return `<html><head><meta charset="utf-8"/><title>Reçu de transfert</title>
      <style>body{font-family:Arial,sans-serif;padding:40px;color:#111}h1{color:#d97706;font-size:22px}
      .code{font-size:36px;font-weight:bold;color:#d97706;letter-spacing:6px;margin:20px 0;font-family:monospace}
      .grid{display:grid;grid-template-columns:1fr 1fr;gap:20px;margin-top:20px}
      .bloc label{font-size:10px;text-transform:uppercase;color:#999;display:block;margin-bottom:4px}
      .bloc p{font-size:14px;font-weight:bold;margin:0}
      .montant{font-size:22px;color:#d97706;font-weight:bold}
      hr{border:none;border-top:1px solid #eee;margin:20px 0}
      .footer{margin-top:40px;font-size:11px;color:#aaa;text-align:center}</style></head>
      <body>
      <h1>🏦 Okane Transfer</h1>
      <p>Reçu officiel de transfert d'argent</p>
      <p class="code">${transfert?.codeRetrait ?? '—'}</p>
      <p>Référence : <strong>${transfert?.numeroReference ?? '—'}</strong></p><hr/>
      <div class="grid">
        <div class="bloc"><label>Expéditeur</label><p>${this.expediteurNom() ?? '—'}</p><p style="font-size:12px;color:#888">${this.formatReceiptPhone(transfert?.telephoneExpediteur ?? this.nouveauClient.telephone)}</p></div>
        <div class="bloc"><label>Bénéficiaire</label><p>${this.beneficiaireNom() ?? '—'}</p><p style="font-size:12px;color:#888">${this.formatReceiptPhone(transfert?.telephoneBeneficiaire ?? this.beneficiaire.telephone)}</p></div>
        <div class="bloc"><label>Corridor</label><p>${this.corridorNom() ?? '—'}</p></div>
        <div class="bloc"><label>Pays de réception</label><p>${this.beneficiaire.paysReception}</p></div>
        <div class="bloc"><label>Mode de réception</label><p>${this.modeReception()}</p></div>
        <div class="bloc"><label>Montant envoyé</label><p>${this.formatMontant(transfert?.montantEnvoye ?? calcul.montantEnvoye)} ${calcul.deviseSource ?? '—'}</p></div>
        <div class="bloc"><label>Frais OkaneTransfer</label><p>${this.formatMontant(transfert?.frais ?? calcul.frais)} ${calcul.deviseSource ?? '—'}</p></div>
        <div class="bloc"><label>Commission agence</label><p>${this.formatMontant(transfert?.partAgence ?? calcul.commissionAgence)} ${calcul.deviseSource ?? '—'}</p></div>
        <div class="bloc"><label>Montant reçu</label><p class="montant">${this.formatMontant(transfert?.montantRecu ?? calcul.montantRecu)} ${transfert?.deviseReception ?? calcul.deviseDestination}</p></div>
        <div class="bloc"><label>Valable jusqu'au</label><p>${this.getExpirationDate()}</p></div>
      </div><hr/>
      <div class="footer">Imprimé le ${new Date().toLocaleDateString('fr-FR')} à ${new Date().toLocaleTimeString('fr-FR')} — Conservez ce reçu.</div>
      </body></html>`;
  }
}
