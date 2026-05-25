import { Component, OnInit } from '@angular/core';
import { CommonModule }      from '@angular/common';
import { FormsModule }       from '@angular/forms';
import { TranslateModule }   from '@ngx-translate/core';

import { CaisseService }           from '../../../core/services/caisse.service';
import { CaisseOperationResponse } from '../../../core/models/caisse';
import { TypeOperation }           from '../../../core/models/enums';

type DateFilter = 'today' | 'week' | 'month' | 'custom';

const PAGE_SIZE = 3;

@Component({
  selector: 'app-historique',
  standalone: true,
  imports: [CommonModule, FormsModule, TranslateModule],
  templateUrl: './historique.component.html',
})
export class HistoriqueComponent implements OnInit {

  // ─── Filtres ───────────────────────────────────────────────────────────────
  searchQuery  = '';
  filterType: 'all' | 'ENVOI' | 'RETRAIT' | 'OUVERTURE' = 'all';
  filterDate: DateFilter = 'today';
  customDateDebut = '';   // yyyy-MM-dd
  customDateFin   = '';   // yyyy-MM-dd

  // ─── Données ───────────────────────────────────────────────────────────────
  allOperations: CaisseOperationResponse[] = [];
  filtered:      CaisseOperationResponse[] = [];

  // ─── Pagination ────────────────────────────────────────────────────────────
  currentPage = 1;
  totalPages  = 1;

  // ─── État ──────────────────────────────────────────────────────────────────
  loading = false;
  error   = '';

  // ─── Agent (statique en attendant l'auth) ──────────────────────────────────
  private agentEmail = 'agent.test@okanetransfer.com';

  readonly TypeOperation = TypeOperation;

  constructor(private caisseService: CaisseService) {}

  ngOnInit(): void {
    this.loadFromBackend();
  }

  // ─── Getters pills ────────────────────────────────────────────────────────

  get envoiCount(): number {
    return this.filtered.filter(o => o.type === TypeOperation.ENVOI).length;
  }

  get retraitCount(): number {
    return this.filtered.filter(o => o.type === TypeOperation.RETRAIT).length;
  }

  get pages(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i + 1);
  }

  /** 6 enregistrements par page, du plus récent au plus ancien */
  get pagedOperations(): CaisseOperationResponse[] {
    const start = (this.currentPage - 1) * PAGE_SIZE;
    return this.filtered.slice(start, start + PAGE_SIZE);
  }

  /** Borne haute affichée dans "X–Y de Z" sans pipe min */
  get showingEnd(): number {
    const end = this.currentPage * PAGE_SIZE;
    return end > this.filtered.length ? this.filtered.length : end;
  }

  // ─── Chargement backend ───────────────────────────────────────────────────

  loadFromBackend(): void {
    const { dateDebut, dateFin } = this.buildDateRange();
    this.loading = true;
    this.error   = '';

    this.caisseService
      .historiqueFiltre(this.agentEmail, dateDebut, dateFin)
      .subscribe({
        next: (data) => {
          // tri du plus récent au plus ancien (sécurité si le backend ne trie pas)
          this.allOperations = data.sort(
            (a, b) => new Date(b.dateHeure).getTime() - new Date(a.dateHeure).getTime()
          );
          this.applyLocalFilters();
          this.loading = false;
        },
        error: () => {
          this.error   = 'Erreur lors du chargement des opérations.';
          this.loading = false;
        },
      });
  }

  // ─── Filtres ───────────────────────────────────────────────────────────────

  /** Changement de date → recharge le backend */
  onDateFilterChange(): void {
    this.currentPage = 1;
    this.loadFromBackend();
  }

  /** Changement de type ou de recherche → filtre local uniquement */
  applyLocalFilters(): void {
    let result = [...this.allOperations];

    if (this.filterType !== 'all') {
      result = result.filter(o => o.type === this.filterType);
    }

    const q = this.searchQuery.trim().toLowerCase();
    if (q) {
      result = result.filter(o =>
        o.agentNom.toLowerCase().includes(q) ||
        (o.referenceTransfert ?? '').toLowerCase().includes(q)
      );
    }

    this.filtered    = result;
    this.totalPages  = Math.max(1, Math.ceil(result.length / PAGE_SIZE));
    this.currentPage = 1;
  }

  goToPage(p: number): void {
    if (p >= 1 && p <= this.totalPages) this.currentPage = p;
  }

  // ─── Utilitaires ──────────────────────────────────────────────────────────

  private buildDateRange(): { dateDebut: string; dateFin: string } {
    const now   = new Date();
    const fmt   = (d: Date) => {
      const y = d.getFullYear();
      const m = String(d.getMonth() + 1).padStart(2, '0');
      const j = String(d.getDate()).padStart(2, '0');
      return `${y}-${m}-${j}`;
    };

    switch (this.filterDate) {

      case 'today':
        return { dateDebut: fmt(now), dateFin: fmt(now) };

      case 'week': {
        // lundi de la semaine en cours
        const monday = new Date(now);
        monday.setDate(now.getDate() - ((now.getDay() + 6) % 7));
        return { dateDebut: fmt(monday), dateFin: fmt(now) };
      }


      case 'month': {
        const first = new Date(now.getFullYear(), now.getMonth(), 1);
        // dateFin = dernier jour du mois, pas aujourd'hui
        const last  = new Date(now.getFullYear(), now.getMonth() + 1, 0);
        return { dateDebut: fmt(first), dateFin: fmt(last) };
      }

      case 'custom':
        return {
          dateDebut: this.customDateDebut || fmt(now),
          dateFin:   this.customDateFin   || fmt(now),
        };
    }
  }

  formatDate(dateHeure: string): string {
    const d = new Date(dateHeure);
    return `${String(d.getDate()).padStart(2,'0')}/${String(d.getMonth()+1).padStart(2,'0')}/${d.getFullYear()}`;
  }

  formatTime(dateHeure: string): string {
    const d = new Date(dateHeure);
    return `${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}`;
  }

  formatAmount(montant: number): string {
    return new Intl.NumberFormat('fr-MA').format(montant) + ' MAD';
  }

  badgeClass(type: TypeOperation): string {
    switch (type) {
      case TypeOperation.ENVOI:     return 'bg-blue-500/15 text-blue-400';
      case TypeOperation.RETRAIT:   return 'bg-purple-500/15 text-purple-400';
      case TypeOperation.OUVERTURE: return 'bg-amber-500/15 text-amber-400';
      default:                      return 'bg-gray-500/15 text-gray-400';
    }
  }

  amountClass(type: TypeOperation): string {
    return type === TypeOperation.RETRAIT ? 'text-red-400' : 'text-emerald-400';
  }

  amountSign(type: TypeOperation): string {
    return type === TypeOperation.RETRAIT ? '−' : '+';
  }
}
