import { Component, OnInit } from '@angular/core';
import { DatePipe, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TranslateService, TranslatePipe } from '@ngx-translate/core';
import { TransfertService } from '../../../core/services/transfert.service';
import { TransfertResponse } from '../../../core/models/transfert';
import { StatutTransfert } from '../../../core/models/enums/statut-transfert.enum';
import { TopbarComponent } from '../../../shared/components/topbar/topbar.component';
import { IconComponent } from '../../../shared/components/icon/icon.component';

type Period = 'all' | 'today' | 'week' | 'month';

@Component({
  selector: 'app-rapports',
  standalone: true,
  host: { class: 'flex flex-col flex-1 min-w-0 overflow-hidden' },
  imports: [TopbarComponent, IconComponent, FormsModule, DatePipe, DecimalPipe, TranslatePipe],
  templateUrl: './rapports.component.html',
})
export class RapportsComponent implements OnInit {
  transferts: TransfertResponse[] = [];
  loading = true;
  search = '';
  period: Period = 'all';

  readonly statuts = Object.values(StatutTransfert);
  readonly statutColors: Record<string, string> = {
    EN_ATTENTE: '#F5A623',
    PAYE:       '#00C48C',
    ANNULE:     '#FF4D4F',
    EXPIRE:     '#9CA3AF',
    BLOQUE:     '#3B82F6',
  };

  constructor(
    private transfertService: TransfertService,
    private translate: TranslateService,
  ) {}

  ngOnInit(): void {
    this.transfertService.getAll().subscribe({
      next: data => { this.transferts = data; this.loading = false; },
      error: () => { this.loading = false; },
    });
  }

  get subtitle(): string {
    return `${this.filteredByPeriod.length} ${this.translate.instant('rapports.subtitle')}`;
  }

  private get filteredByPeriod(): TransfertResponse[] {
    if (this.period === 'all') return this.transferts;
    const now = new Date();
    return this.transferts.filter(t => {
      const d = new Date(t.creeLe);
      if (this.period === 'today') {
        return d.toDateString() === now.toDateString();
      }
      const days = this.period === 'week' ? 7 : 30;
      const cutoff = new Date(now);
      cutoff.setDate(cutoff.getDate() - days);
      return d >= cutoff;
    });
  }

  get filtered(): TransfertResponse[] {
    const q = this.search.toLowerCase().trim();
    if (!q) return this.filteredByPeriod;
    return this.filteredByPeriod.filter(t =>
      t.numeroReference.toLowerCase().includes(q) ||
      t.nomExpediteur.toLowerCase().includes(q) ||
      t.nomBeneficiaire.toLowerCase().includes(q)
    );
  }

  get totalCount(): number { return this.filteredByPeriod.length; }
  get totalVolume(): number { return this.filteredByPeriod.reduce((s, t) => s + t.montantEnvoye, 0); }
  get avgAmount(): number { return this.totalCount ? this.totalVolume / this.totalCount : 0; }
  get pendingCount(): number { return this.filteredByPeriod.filter(t => t.statut === StatutTransfert.EN_ATTENTE).length; }

  countByStatut(statut: string): number {
    return this.filteredByPeriod.filter(t => t.statut === statut).length;
  }

  barWidth(statut: string): number {
    if (!this.totalCount) return 0;
    return Math.round((this.countByStatut(statut) / this.totalCount) * 100);
  }

  statutColor(statut: string): string {
    return this.statutColors[statut] ?? '#9CA3AF';
  }

  statutBg(statut: string): string {
    const c = this.statutColors[statut] ?? '#9CA3AF';
    return `${c}18`;
  }

  exportCsv(): void {
    const headers = ['Référence', 'Expéditeur', 'Bénéficiaire', 'Montant (MAD)', 'Devise reçue', 'Statut', 'Date'];
    const rows = this.filtered.map(t => [
      t.numeroReference,
      t.nomExpediteur,
      t.nomBeneficiaire,
      t.montantEnvoye.toFixed(2),
      t.deviseReception,
      t.statut,
      new Date(t.creeLe).toLocaleString('fr-FR'),
    ]);

    const csv = [headers, ...rows]
      .map(row => row.map(cell => `"${String(cell).replace(/"/g, '""')}"`).join(';'))
      .join('\n');

    const blob = new Blob(['﻿' + csv], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `transferts_${new Date().toISOString().slice(0, 10)}.csv`;
    a.click();
    URL.revokeObjectURL(url);
  }
}
