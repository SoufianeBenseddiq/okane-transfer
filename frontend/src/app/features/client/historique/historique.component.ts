import { Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { TransfertService } from '../../../core/services/transfert.service';
import { AuthService } from '../../../core/services/auth.service';
import { TransfertResponse } from '../../../core/models/transfert';

type FiltreStatut = 'TOUS' | 'EN_ATTENTE' | 'PAYE' | 'ANNULE' | 'EXPIRE' | 'BLOQUE';

const AVATAR_COLORS = ['#F5A623', '#1a1f36', '#4CAF50', '#9c27b0', '#2196F3', '#ef4444', '#00bcd4', '#ff9800'];

const FLAGS_PAR_PAYS: Record<string, string> = {
  'Sénégal': 'sn',
  "Côte d'Ivoire": 'ci',
  'France': 'fr',
  'Maroc': 'ma',
  'Mali': 'ml',
  'Guinée': 'gn',
  'Cameroun': 'cm',
  'Bénin': 'bj',
  'Togo': 'tg',
  'Burkina Faso': 'bf',
  'Niger': 'ne',
  'Gabon': 'ga',
  'Espagne': 'es',
  'Italie': 'it',
  'Belgique': 'be',
  'Allemagne': 'de',
  'Pays-Bas': 'nl',
  'Royaume-Uni': 'gb',
  'Congo': 'cg',
  'Algérie': 'dz',
  'Tunisie': 'tn',
  'Égypte': 'eg',
  'Canada': 'ca',
  'Arabie Saoudite': 'sa',
  'Émirats Arabes Unis': 'ae',
  'États-Unis': 'us',
};

const PAGE_SIZE = 6;

@Component({
  selector: 'app-historique',
  standalone: true,
  imports: [CommonModule, FormsModule, TranslateModule],
  templateUrl: './historique.component.html',
  styleUrl: './historique.component.css'
})
export class HistoriqueComponent implements OnInit {

  private transfertService = inject(TransfertService);
  private authService       = inject(AuthService);
  private router            = inject(Router);

  // ─── État ─────────────────────────────────────────────────────────────────
  transferts  = signal<TransfertResponse[]>([]);
  chargement  = signal<boolean>(true);
  erreur      = signal<string | null>(null);

  filtreStatut      = signal<FiltreStatut>('TOUS');
  filtreDestination = signal<string>('TOUS');
  dateDebut         = signal<string>('');
  dateFin           = signal<string>('');
  pageActuelle      = signal<number>(1);

  filtres: { key: FiltreStatut; labelKey: string }[] = [
    { key: 'TOUS',       labelKey: 'historique.statuts.tous' },
    { key: 'EN_ATTENTE', labelKey: 'historique.statuts.enAttente' },
    { key: 'PAYE',       labelKey: 'historique.statuts.paye' },
    { key: 'ANNULE',     labelKey: 'historique.statuts.annule' },
    { key: 'EXPIRE',     labelKey: 'historique.statuts.expire' },
    { key: 'BLOQUE',     labelKey: 'historique.statuts.bloque' },
  ];

  // ─── Computed : liste des destinations disponibles (pour le dropdown) ────
  destinationsDisponibles = computed(() => {
    const pays = new Set<string>();
    for (const t of this.transferts()) {
      if (t.paysBeneficiaire) pays.add(t.paysBeneficiaire);
    }
    return Array.from(pays).sort();
  });

  // ─── Computed : liste filtrée par statut + destination + dates ───────────
  transfertsFiltres = computed(() => {
    const statut      = this.filtreStatut();
    const destination = this.filtreDestination();
    const debut       = this.dateDebut();
    const fin         = this.dateFin();

    return this.transferts().filter(t => {
      if (statut !== 'TOUS' && t.statut !== statut) return false;
      if (destination !== 'TOUS' && t.paysBeneficiaire !== destination) return false;

      const creeLe = new Date(t.creeLe).getTime();
      if (debut && creeLe < new Date(debut).getTime()) return false;
      if (fin) {
        const finDate = new Date(fin);
        finDate.setHours(23, 59, 59, 999);
        if (creeLe > finDate.getTime()) return false;
      }
      return true;
    });
  });

  // ─── Total envoyé (sur la sélection filtrée) ──────────────────────────────
  totalEnvoye = computed(() =>
    this.transfertsFiltres().reduce((sum, t) => sum + (t.montantEnvoye ?? 0), 0)
  );

  // ─── Pagination ────────────────────────────────────────────────────────────
  totalPages = computed(() =>
    Math.max(1, Math.ceil(this.transfertsFiltres().length / PAGE_SIZE))
  );

  transfertsPagines = computed(() => {
    const page = this.pageActuelle();
    const start = (page - 1) * PAGE_SIZE;
    return this.transfertsFiltres().slice(start, start + PAGE_SIZE);
  });

  pagesAffichees = computed(() => {
    const total = this.totalPages();
    return Array.from({ length: total }, (_, i) => i + 1);
  });

  ngOnInit(): void {
    this.chargerTransferts();
  }

  chargerTransferts(): void {
    const clientId = this.authService.currentUser?.id;
    if (!clientId) {
      this.erreur.set('Utilisateur non identifié.');
      this.chargement.set(false);
      return;
    }

    this.chargement.set(true);
    this.erreur.set(null);

    this.transfertService.getMesTransferts(clientId).subscribe({
      next: (transferts) => {
        const triés = [...transferts].sort((a, b) =>
          new Date(b.creeLe).getTime() - new Date(a.creeLe).getTime()
        );
        this.transferts.set(triés);
        this.chargement.set(false);
      },
      error: () => {
        this.erreur.set('Impossible de charger vos transferts.');
        this.chargement.set(false);
      },
    });
  }

  // ─── Actions filtres ───────────────────────────────────────────────────────
  selectionnerFiltre(filtre: FiltreStatut): void {
    this.filtreStatut.set(filtre);
    this.pageActuelle.set(1);
  }

  onDateDebutChange(value: string): void {
    this.dateDebut.set(value);
    this.pageActuelle.set(1);
  }

  onDateFinChange(value: string): void {
    this.dateFin.set(value);
    this.pageActuelle.set(1);
  }

  onDestinationChange(value: string): void {
    this.filtreDestination.set(value);
    this.pageActuelle.set(1);
  }

  reinitialiser(): void {
    this.filtreStatut.set('TOUS');
    this.filtreDestination.set('TOUS');
    this.dateDebut.set('');
    this.dateFin.set('');
    this.pageActuelle.set(1);
  }

  // ─── Pagination actions ────────────────────────────────────────────────────
  allerPage(page: number): void {
    this.pageActuelle.set(page);
  }

  pagePrecedente(): void {
    if (this.pageActuelle() > 1) this.pageActuelle.update(p => p - 1);
  }

  pageSuivante(): void {
    if (this.pageActuelle() < this.totalPages()) this.pageActuelle.update(p => p + 1);
  }

  // ─── Navigation ─────────────────────────────────────────────────────────────
  voirDetail(transfert: TransfertResponse): void {
    this.router.navigate(['/client/suivi', transfert.numeroReference]);
  }

  // ─── Helpers d'affichage ────────────────────────────────────────────────────
  formatMontant(n: number | null | undefined): string {
    return new Intl.NumberFormat('fr-FR').format(n ?? 0);
  }

  formatDate(date: string | Date | null | undefined): string {
    if (!date) return '—';
    return new Date(date).toLocaleDateString('fr-FR', {
      day: '2-digit', month: '2-digit',
    }) + ' · ' + new Date(date).toLocaleTimeString('fr-FR', {
      hour: '2-digit', minute: '2-digit',
    });
  }

  statutLabelKey(statut: string): string {
    const keys: Record<string, string> = {
      EN_ATTENTE: 'historique.statuts.enAttente',
      PAYE: 'historique.statuts.paye',
      ANNULE: 'historique.statuts.annule',
      EXPIRE: 'historique.statuts.expire',
      BLOQUE: 'historique.statuts.bloque',
    };
    return keys[statut] ?? statut;
  }

  statutClass(statut: string): string {
    const classes: Record<string, string> = {
      EN_ATTENTE: 'badge-attente',
      PAYE: 'badge-paye',
      ANNULE: 'badge-annule',
      EXPIRE: 'badge-expire',
      BLOQUE: 'badge-bloque',
    };
    return classes[statut] ?? '';
  }

  initiales(nom: string | null | undefined): string {
    if (!nom) return '?';
    const parts = nom.trim().split(/\s+/);
    if (parts.length === 1) return parts[0].substring(0, 2).toUpperCase();
    return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
  }

  avatarColor(nom: string | null | undefined): string {
    if (!nom) return AVATAR_COLORS[0];
    let hash = 0;
    for (let i = 0; i < nom.length; i++) {
      hash = (hash << 5) - hash + nom.charCodeAt(i);
      hash |= 0;
    }
    return AVATAR_COLORS[Math.abs(hash) % AVATAR_COLORS.length];
  }

  flagCode(pays: string | null | undefined): string {
    if (!pays) return '';
    return FLAGS_PAR_PAYS[pays] ?? '';
  }

  maskCode(code: string | null | undefined): string {
    if (!code) return '——•• - ••——';
    const cleaned = code.replace(/[\s-]/g, '');
    if (cleaned.length < 4) return code;
    const visibleStart = cleaned.substring(0, 2);
    const visibleEnd = cleaned.substring(cleaned.length - 2);
    return `${visibleStart}•• - ••${visibleEnd}`;
  }
}