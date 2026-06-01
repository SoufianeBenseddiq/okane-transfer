// src/app/features/agent/envoi/envoi.component.ts

import { Component, signal, computed, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';
import { TransfertService } from '../../../core/services/transfert.service';
import { DeviseService } from '../../../core/services/devise.service';
import { UserService } from '../../../core/services/user.service';
import { AuthService } from '../../../core/services/auth.service';
import { DeviseResponse } from '../../../core/models/devise/devise-response.model';
import { CorridorResponse } from '../../../core/models/devise/corridor-response.model';
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
    nom: '', prenom: '', email: '', motDePasse: '', typePiece: 'CIN',
    numeroPiece: '', telephone: '', pays: 'Maroc',
  };
  nouveauClientNomSignal = signal<string | null>(null);

  typesPiece = ['CIN', 'Passeport', 'Titre de sejour', 'Carte de resident'];
  paysList   = ['Maroc', 'France', 'Belgique', 'Espagne', 'Italie', 'Allemagne', 'Pays-Bas'];

  // ─── Étape 2 : Bénéficiaire ───────────────────────────────────────────────
  beneficiaire: BeneficiaireForm = {
    nom: '', prenom: '', telephone: '',
    paysReception: 'Senegal', ville: '', relation: '',
  };
  beneficiaireNomSignal = signal<string | null>(null);
  paysReceptionList = ['Senegal', 'Cote Ivoire', 'Mali', 'Guinee', 'Cameroun', 'Congo'];
  relationsList     = ['Famille', 'Conjoint(e)', 'Parent', 'Enfant', 'Soeur', 'Frere', 'Ami(e)', 'Autre'];

  countryPhoneCodes: Record<string, string> = {
    'Maroc': '+212',
    'France': '+33',
    'Belgique': '+32',
    'Espagne': '+34',
    'Italie': '+39',
    'Allemagne': '+49',
    'Pays-Bas': '+31',
    'Senegal': '+221',
    'Cote Ivoire': '+225',
    'Mali': '+223',
    'Guinee': '+224',
    'Cameroun': '+237',
    'Congo': '+242'
  };

  countryPhonePlaceholders: Record<string, string> = {
    'Maroc': '6 00 00 00 00',
    'France': '6 00 00 00 00',
    'Belgique': '4 00 00 00 00',
    'Espagne': '6 00 00 00 00',
    'Italie': '3 00 000 0000',
    'Allemagne': '15 00000000',
    'Pays-Bas': '6 00000000',
    'Senegal': '77 000 00 00',
    'Cote Ivoire': '07 00 00 00 00',
    'Mali': '70 00 00 00',
    'Guinee': '620 00 00 00',
    'Cameroun': '6 00 00 00 00',
    'Congo': '06 000 00 00'
  };

  countryPhoneLengths: Record<string, number> = {
    'Maroc': 9,
    'France': 9,
    'Belgique': 9,
    'Espagne': 9,
    'Italie': 10,
    'Allemagne': 10,
    'Pays-Bas': 9,
    'Senegal': 9,
    'Cote Ivoire': 10,
    'Mali': 8,
    'Guinee': 9,
    'Cameroun': 9,
    'Congo': 9
  };

  formatPhoneNumber(value: string, country: string): string {
    if (!value) return '';
    const digits = value.replace(/\D/g, '');

    if (country === 'Maroc' || country === 'France' || country === 'Espagne' || country === 'Cameroun') {
      const match = digits.match(/^(\d{1})(\d{0,2})(\d{0,2})(\d{0,2})(\d{0,2})$/);
      if (match) {
        return [match[1], match[2], match[3], match[4], match[5]].filter(Boolean).join(' ');
      }
    } else if (country === 'Belgique') {
      const match = digits.match(/^(\d{1})(\d{0,2})(\d{0,2})(\d{0,2})(\d{0,2})$/);
      if (match) {
        return [match[1], match[2], match[3], match[4], match[5]].filter(Boolean).join(' ');
      }
    } else if (country === 'Senegal' || country === 'Congo') {
      const match = digits.match(/^(\d{2})(\d{0,3})(\d{0,2})(\d{0,2})$/);
      if (match) {
        return [match[1], match[2], match[3], match[4]].filter(Boolean).join(' ');
      }
    } else if (country === 'Cote Ivoire') {
      const match = digits.match(/^(\d{2})(\d{0,2})(\d{0,2})(\d{0,2})(\d{0,2})$/);
      if (match) {
        return [match[1], match[2], match[3], match[4], match[5]].filter(Boolean).join(' ');
      }
    } else if (country === 'Mali') {
      const match = digits.match(/^(\d{2})(\d{0,2})(\d{0,2})(\d{0,2})$/);
      if (match) {
        return [match[1], match[2], match[3], match[4]].filter(Boolean).join(' ');
      }
    } else if (country === 'Guinee') {
      const match = digits.match(/^(\d{3})(\d{0,2})(\d{0,2})(\d{0,2})$/);
      if (match) {
        return [match[1], match[2], match[3], match[4]].filter(Boolean).join(' ');
      }
    } else if (country === 'Italie') {
      const match = digits.match(/^(\d{1})(\d{0,2})(\d{0,3})(\d{0,4})$/);
      if (match) {
        return [match[1], match[2], match[3], match[4]].filter(Boolean).join(' ');
      }
    } else if (country === 'Allemagne') {
      const match = digits.match(/^(\d{2})(\d{0,8})$/);
      if (match) {
        return [match[1], match[2]].filter(Boolean).join(' ');
      }
    } else if (country === 'Pays-Bas') {
      const match = digits.match(/^(\d{1})(\d{0,8})$/);
      if (match) {
        return [match[1], match[2]].filter(Boolean).join(' ');
      }
    }

    return digits;
  }

  onPhoneInput(value: string, field: 'nouveauClient' | 'beneficiaire'): void {
    if (field === 'nouveauClient') {
      this.nouveauClient.telephone = this.formatPhoneNumber(value, this.nouveauClient.pays);
    } else {
      this.beneficiaire.telephone = this.formatPhoneNumber(value, this.beneficiaire.paysReception);
    }
  }

  onPaysChange(pays: string): void {
    this.nouveauClient.pays = pays;
    this.nouveauClient.telephone = this.formatPhoneNumber(this.nouveauClient.telephone, pays);
  }

  onPaysReceptionChange(pays: string): void {
    this.beneficiaire.paysReception = pays;
    this.beneficiaire.telephone = this.formatPhoneNumber(this.beneficiaire.telephone, pays);
  }

  erreursForm: Record<string, boolean> = {};

  // ─── Étape 3 : Montant & Corridor ─────────────────────────────────────────
  montantEnvoye       = signal<number>(2000);
  corridorSelectionne = signal<CorridorResponse | null>(null);
  corridors           = signal<CorridorResponse[]>([]);
  devisesParCode      = signal<Record<string, DeviseResponse>>({});
  fraisResult         = signal<FraisResult | null>(null);
  chargementFrais     = signal<boolean>(false);
  modesReception      = ['Cash au guichet', 'Mobile Money', 'Virement bancaire'];
  modeReception       = signal<string>('Cash au guichet');

  // ─── Étape 4 : Confirmation ───────────────────────────────────────────────
  transfertCree   = signal<TransfertResponse | null>(null);
  chargementEnvoi = signal<boolean>(false);
  erreurEnvoi     = signal<string | null>(null);
  codeCopie       = signal<boolean>(false);
  envoiEmailEnCours = signal<boolean>(false);
  messageEmail = signal<string | null>(null);
  erreurEmail = signal<string | null>(null);

  // ─── Computed ─────────────────────────────────────────────────────────────
  expediteurNom = computed(() => {
    const c = this.clientSelectionne();
    if (c) return `${c.prenom} ${c.nom}`;
    // For new client, use the signal that is updated on each form change
    return this.nouveauClientNomSignal();
  });

  beneficiaireNom = computed(() => {
    return this.beneficiaireNomSignal();
  });

  corridorNom = computed(() => {
    const c = this.corridorSelectionne();
    return c ? `${c.deviseSource} → ${c.deviseDestination}` : null;
  });

  clientsAffiches = computed(() => {
    const list = this.clientsTrouves();
    const sel = this.clientSelectionne();
    if (!sel) return list;
    return list.filter(c => c.id !== sel.id);
  });

  calcul = computed(() => {
    const frais    = this.fraisResult();
    const corridor = this.corridorSelectionne();
    const deviseSource = corridor ? this.devisesParCode()[corridor.deviseSource] : null;
    const deviseDestination = corridor ? this.devisesParCode()[corridor.deviseDestination] : null;
    const taux = frais?.taux ?? (deviseSource && deviseDestination
      ? +(deviseSource.tauxVersEuro / deviseDestination.tauxVersEuro).toFixed(4)
      : null);
    const montantFrais = frais?.montantFrais ?? 0;
    const commissionAgence = frais?.partAgence ?? 0;
    const montantNetSource = frais
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
      taux,
      delaiMin:          frais?.delaiMin ?? 5,
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
    this.chargerDevises();
    this.chargerCorridors();
  }

  chargerDevises(): void {
    this.deviseService.getAllDevises().subscribe({
      next: (devises) => {
        const map = devises.reduce((acc, devise) => {
          acc[devise.code] = devise;
          return acc;
        }, {} as Record<string, DeviseResponse>);
        this.devisesParCode.set(map);
      },
      error: (err) => console.error('Erreur chargement devises', err),
    });
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
    if (e.key === 'Enter' && this.searchQuery().trim().length >= 1) {
      this.onRechercher();
    }
  }

  onSelectionnerClient(client: UserResponse): void {
    this.clientSelectionne.set(client);
    this.pieceSelectionneeId.set(null);
    this.erreursForm = {}; // Clear errors when selecting a client
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

  onChangeToNewClient(): void {
    this.clientSelectionne.set(null);
    this.piecesClient.set([]);
    this.pieceSelectionneeId.set(null);
    this.nouveauClient = {
      nom: '', prenom: '', email: '', motDePasse: '', typePiece: 'CIN',
      numeroPiece: '', telephone: '', pays: 'Maroc',
    };
    this.nouveauClientNomSignal.set(null);
    this.searchQuery.set('');
    this.clientsTrouves.set([]);
    this.rechercheEffectuee.set(false);
    this.erreursForm = {};
  }

  // ─── Étape 3 ──────────────────────────────────────────────────────────────
  onCorridorChange(corridorId: number | string): void {
    const id = Number(corridorId);
    const c  = this.corridors().find(x => x.id === id) ?? null;
    this.corridorSelectionne.set(c);
    this.recalculerFrais();
  }

  onMontantChange(montant: number | string): void {
    const value = Number(montant);
    this.montantEnvoye.set(value);
    if (Number.isFinite(value) && value > 0) {
      delete this.erreursForm['montant'];
    }
    this.recalculerFrais();
  }

  private montantEstValide(): boolean {
    const montant = this.montantEnvoye();
    return Number.isFinite(montant) && montant > 0;
  }

  private telephoneComplet(telephone: string, pays: string): string {
    return `${this.countryPhoneCodes[pays] ?? ''}${telephone}`.replace(/\s/g, '');
  }

  private formatReceiptPhone(telephone?: string | null): string {
    if (!telephone) return '—';

    const trimmed = telephone.trim();
    const digits = trimmed.replace(/\s+/g, '');

    if (digits.startsWith('+')) {
      const countryCodeMatch = digits.match(/^\+(\d{1,3})/);
      if (!countryCodeMatch) {
        return trimmed;
      }

      const countryCode = countryCodeMatch[1];
      const rest = digits.slice(countryCode.length + 1).replace(/\D/g, '');
      const grouped = rest.match(/.{1,2}/g)?.join(' ') ?? rest;
      return `+${countryCode} ${grouped}`.trim();
    }

    return digits.replace(/(\d{2})(?=\d)/g, '$1 ').trim();
  }

  private typePieceApi(typePiece: string): 'CIN' | 'PASSEPORT' | 'CARTE_SEJOUR' | 'PERMIS' {
    const normalized = typePiece.toLowerCase();
    if (normalized === 'passeport') return 'PASSEPORT';
    if (normalized === 'titre de sejour' || normalized === 'carte de resident') return 'CARTE_SEJOUR';
    if (normalized === 'permis') return 'PERMIS';
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
    this.erreursForm = {};
    let hasError = false;

    if (etape === 1) {
      if (!this.clientSelectionne()) {
        // Vérifier que le formulaire nouveau client est rempli
        const nc = this.nouveauClient;
        if (!nc.nom?.trim()) { this.erreursForm['nom'] = true; hasError = true; }
        if (!nc.prenom?.trim()) { this.erreursForm['prenom'] = true; hasError = true; }
        if (!nc.email?.trim()) { this.erreursForm['email'] = true; hasError = true; }
        else if (!/^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/.test(nc.email.trim())) {
          this.erreursForm['email_format'] = true; hasError = true;
        }
        if (!nc.motDePasse?.trim()) { this.erreursForm['motDePasse'] = true; hasError = true; }
        else if (!/^(?=.*[A-Z])(?=.*[0-9]).{8,}$/.test(nc.motDePasse)) {
          this.erreursForm['motDePasse_strength'] = true; hasError = true;
        }
        if (!nc.telephone?.trim()) {
          this.erreursForm['telephone'] = true;
          hasError = true;
        } else {
          const rawPhone = nc.telephone.replace(/\s/g, '');
          const expectedLen = this.countryPhoneLengths[nc.pays] || 9;
          if (!/^\d+$/.test(rawPhone) || rawPhone.length !== expectedLen) {
            this.erreursForm['telephone_format'] = true;
            hasError = true;
          }
        }
        if (!nc.numeroPiece?.trim()) { this.erreursForm['numeroPiece'] = true; hasError = true; }

        if (hasError) {
          return;
        }
      }
      // Push new client name into signal for recap visibility
      if (!this.clientSelectionne()) {
        const nc = this.nouveauClient;
        this.nouveauClientNomSignal.set(
          `${nc.prenom} ${nc.nom}`.trim() || null
        );
      }
      this.etapeActive.update(v => v + 1);

    } else if (etape === 2) {
      const b = this.beneficiaire;
      if (!b.nom?.trim()) { this.erreursForm['b_nom'] = true; hasError = true; }
      if (!b.prenom?.trim()) { this.erreursForm['b_prenom'] = true; hasError = true; }
      if (!b.telephone?.trim()) {
        this.erreursForm['b_telephone'] = true;
        hasError = true;
      } else {
        const rawPhone = b.telephone.replace(/\s/g, '');
        const expectedLen = this.countryPhoneLengths[b.paysReception] || 9;
        if (!/^\d+$/.test(rawPhone) || rawPhone.length !== expectedLen) {
          this.erreursForm['b_telephone_format'] = true;
          hasError = true;
        }
      }
      if (!b.paysReception?.trim()) { this.erreursForm['b_paysReception'] = true; hasError = true; }

      if (hasError) {
        return;
      }
      // Push beneficiary name into signal for recap visibility
      this.beneficiaireNomSignal.set(
        `${b.prenom} ${b.nom}`.trim() || null
      );
      this.recalculerFrais();
      this.etapeActive.update(v => v + 1);
    } else if (etape === 3) {
      if (!this.montantEstValide()) {
        this.erreursForm['montant'] = true;
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

    if (!corridor) {
      this.erreurEnvoi.set('Veuillez sélectionner un corridor.');
      return;
    }
    if (!this.montantEstValide()) {
      this.erreursForm['montant'] = true;
      this.erreurEnvoi.set('Veuillez corriger les erreurs dans le formulaire.');
      return;
    }

    // Forcer reset du chargementFrais au cas où il serait bloqué
    this.chargementFrais.set(false);

    const pieceId = this.pieceSelectionneeId();

    // Bloquer uniquement si le client a des pièces mais aucune n'est sélectionnée
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
          nom: nc.nom.trim(),
          prenom: nc.prenom.trim(),
          email: nc.email.trim(),
          motDePasse: nc.motDePasse,
          telephone: this.telephoneComplet(nc.telephone, nc.pays),
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
        telephoneBeneficiaire: this.telephoneComplet(this.beneficiaire.telephone, this.beneficiaire.paysReception),
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
        error: (err) => {
          this.erreurEnvoi.set(this.messageErreur(err));
          this.chargementEnvoi.set(false);
        },
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
      telephoneBeneficiaire: this.telephoneComplet(this.beneficiaire.telephone, this.beneficiaire.paysReception),
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
      error: (err) => {
        this.erreurEnvoi.set(this.messageErreur(err));
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

    const request: SendTransfertReceiptEmailRequest = {
      codeRetrait: transfert.codeRetrait,
      destinataireEmail,
    };

    this.envoiEmailEnCours.set(true);
    this.erreurEmail.set(null);
    this.messageEmail.set(null);

    this.transfertService.envoyerRecuParEmail(request).subscribe({
      next: () => {
        this.messageEmail.set(`Reçu envoyé à ${destinataireEmail}`);
        this.envoiEmailEnCours.set(false);
      },
      error: (err) => {
        this.erreurEmail.set(this.messageErreur(err));
        this.envoiEmailEnCours.set(false);
      },
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
      numeroPiece: '', telephone: '', pays: 'Maroc',
    };
    this.nouveauClientNomSignal.set(null);
    this.beneficiaire = {
      nom: '', prenom: '', telephone: '',
      paysReception: 'Senegal', ville: '', relation: '',
    };
    this.beneficiaireNomSignal.set(null);
    this.montantEnvoye.set(2000);
    this.fraisResult.set(null);
    this.transfertCree.set(null);
    this.erreurEnvoi.set(null);
    this.messageEmail.set(null);
    this.erreurEmail.set(null);
    if (this.corridors().length > 0) {
      this.corridorSelectionne.set(this.corridors()[0]);
    }
  }

  getFlagEmoji(country?: string | null): string {
    if (!country) return '🏳️';
    const c = country.toLowerCase().trim();
    if (c === 'maroc' || c === 'ma') return '🇲🇦';
    if (c === 'france' || c === 'fr') return '🇫🇷';
    if (c === 'belgique' || c === 'be') return '🇧🇪';
    if (c === 'espagne' || c === 'es') return '🇪🇸';
    if (c === 'italie' || c === 'it') return '🇮🇹';
    if (c === 'allemagne' || c === 'de') return '🇩🇪';
    if (c === 'pays-bas' || c === 'nl') return '🇳🇱';
    if (c === 'senegal' || c === 'sn') return '🇸🇳';
    if (c === 'cote d\'ivoire' || c === 'cote ivoire' || c === 'ci') return '🇨🇮';
    if (c === 'mali' || c === 'ml') return '🇲🇱';
    if (c === 'guinee' || c === 'gn') return '🇬🇳';
    if (c === 'cameroun' || c === 'cm') return '🇨🇲';
    if (c === 'congo' || c === 'cg') return '🇨🇬';
    return '🏳️';
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
          <p style="font-size:12px;color:#888;margin-top:2px">${this.formatReceiptPhone(transfert?.telephoneExpediteur ?? this.nouveauClient.telephone)}</p>
        </div>
        <div class="bloc">
          <label>Bénéficiaire</label>
          <p>${this.beneficiaireNom() ?? '—'}</p>
          <p style="font-size:12px;color:#888;margin-top:2px">${this.formatReceiptPhone(transfert?.telephoneBeneficiaire ?? this.beneficiaire.telephone)}</p>
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
          <p>${this.formatMontant(transfert?.montantEnvoye ?? calcul.montantEnvoye)} ${calcul.deviseSource ?? '—'}</p>
        </div>
        <div class="bloc">
          <label>Frais OkaneTransfer</label>
          <p>${this.formatMontant(transfert?.frais ?? calcul.frais)} ${calcul.deviseSource ?? '—'}</p>
        </div>
        <div class="bloc">
          <label>Commission agence</label>
          <p>${this.formatMontant(transfert?.partAgence ?? calcul.commissionAgence)} ${calcul.deviseSource ?? '—'}</p>
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
  async shareRecu(): Promise<void> {
    const transfert = this.transfertCree();
    const calcul = this.calcul();
    const html = this.buildReceiptHtml(transfert, calcul);

    // Try Web Share API with a blob (works on mobile/modern browsers)
    try {
      const blob = new Blob([html], { type: 'text/html' });
      const file = new File([blob], `recu-${transfert?.numeroReference ?? Date.now()}.html`, { type: 'text/html' });
      // @ts-ignore navigator.canShare
      if (navigator.canShare && navigator.canShare({ files: [file] })) {
        // @ts-ignore navigator.share
        await navigator.share({ files: [file], title: 'Reçu de transfert', text: 'Reçu Okane Transfer' });
        return;
      }
    } catch (e) {
      // ignore and fallback to download
    }

    // Fallback: trigger download of the HTML receipt
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
    return `
    <html>
    <head>
      <meta charset="utf-8" />
      <title>Reçu de transfert</title>
      <style>
        body{font-family: Arial, sans-serif;padding:40px;color:#111}
        h1{color:#d97706;font-size:22px;margin-bottom:4px}
        .subtitle{color:#666;font-size:13px;margin-bottom:30px}
        .code{font-size:36px;font-weight:bold;color:#d97706;letter-spacing:6px;margin:20px 0;font-family:monospace}
        .grid{display:grid;grid-template-columns:1fr 1fr;gap:20px;margin-top:20px}
        .bloc label{font-size:10px;text-transform:uppercase;color:#999;display:block;margin-bottom:4px}
        .bloc p{font-size:14px;font-weight:bold;margin:0}
        .montant{font-size:22px;color:#d97706;font-weight:bold}
        hr{border:none;border-top:1px solid #eee;margin:20px 0}
        .footer{margin-top:40px;font-size:11px;color:#aaa;text-align:center}
      </style>
    </head>
    <body>
      <h1>🏦 Okane Transfer</h1>
      <p class="subtitle">Reçu officiel de transfert d'argent</p>
      <p class="code">${transfert?.codeRetrait ?? '—'}</p>
      <p>Référence : <strong>${transfert?.numeroReference ?? '—'}</strong></p>
      <hr/>
      <div class="grid">
        <div class="bloc"><label>Expéditeur</label><p>${this.expediteurNom() ?? '—'}</p><p style="font-size:12px;color:#888;margin-top:2px">${this.formatReceiptPhone(transfert?.telephoneExpediteur ?? this.nouveauClient.telephone)}</p></div>
        <div class="bloc"><label>Bénéficiaire</label><p>${this.beneficiaireNom() ?? '—'}</p><p style="font-size:12px;color:#888;margin-top:2px">${this.formatReceiptPhone(transfert?.telephoneBeneficiaire ?? this.beneficiaire.telephone)}</p></div>
        <div class="bloc"><label>Pays de réception</label><p>${this.beneficiaire.paysReception}</p></div>
        <div class="bloc"><label>Mode de réception</label><p>${this.modeReception()}</p></div>
        <div class="bloc"><label>Montant envoyé</label><p>${this.formatMontant(transfert?.montantEnvoye ?? calcul.montantEnvoye)} ${calcul.deviseSource ?? '—'}</p></div>
        <div class="bloc"><label>Frais OkaneTransfer</label><p>${this.formatMontant(transfert?.frais ?? calcul.frais)} ${calcul.deviseSource ?? '—'}</p></div>
        <div class="bloc"><label>Commission agence</label><p>${this.formatMontant(transfert?.partAgence ?? calcul.commissionAgence)} ${calcul.deviseSource ?? '—'}</p></div>
        <div class="bloc"><label>Montant reçu</label><p class="montant">${this.formatMontant(transfert?.montantRecu ?? calcul.montantRecu)} ${transfert?.deviseReception ?? calcul.deviseDestination}</p></div>
        <div class="bloc"><label>Valable jusqu'au</label><p>${this.getExpirationDate()}</p></div>
      </div>
      <hr/>
      <div class="footer">Imprimé le ${new Date().toLocaleDateString('fr-FR')} à ${new Date().toLocaleTimeString('fr-FR')} — Conservez ce reçu.</div>
    </body>
    </html>
    `;
  }
}
