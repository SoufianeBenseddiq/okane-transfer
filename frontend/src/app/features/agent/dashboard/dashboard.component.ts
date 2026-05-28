import { Component, OnInit, AfterViewInit, ElementRef, ViewChild, OnDestroy } from '@angular/core';
import { CommonModule }      from '@angular/common';
import { RouterModule }      from '@angular/router';
import { TranslateModule }   from '@ngx-translate/core';
import { forkJoin, of }      from 'rxjs';
import { catchError }        from 'rxjs/operators';
import { Chart, registerables } from 'chart.js';

import { CaisseService }           from '../../../core/services/caisse.service';
import { CaisseOperationResponse } from '../../../core/models/caisse';
import { TypeOperation }           from '../../../core/models/enums';
import { AuthService }             from '../../../core/services/auth.service';

Chart.register(...registerables);

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, TranslateModule],
  templateUrl: './dashboard.component.html',
})
export class DashboardComponent implements OnInit, AfterViewInit, OnDestroy {

  @ViewChild('activityChart') activityChartRef!: ElementRef<HTMLCanvasElement>;
  private chartInstance: Chart | null = null;

  // ─── Agent connecté ───────────────────────────────────────────────────────
  private get agentEmail(): string { return this.auth.currentUser?.email ?? ''; }

  // ─── État ─────────────────────────────────────────────────────────────────
  loading = true;
  error   = '';

  // ─── Plafond journalier (CDC §3.1) ────────────────────────────────────────
  readonly dailyCap = 50_000; // MAD — à externaliser depuis la config/API

  get capUsagePercent(): number {
    const raw = parseFloat(this.stats.totalAmount.replace(/\s/g, '').replace('MAD', '').replace(',', '.')) || 0;
    return Math.min((raw / this.dailyCap) * 100, 100);
  }

  get capRemaining(): number {
    const raw = parseFloat(this.stats.totalAmount.replace(/\s/g, '').replace('MAD', '').replace(',', '.')) || 0;
    return Math.max(this.dailyCap - raw, 0);
  }

  // ─── Stats ────────────────────────────────────────────────────────────────
  stats = {
    transfersCount : 0,
    sends          : 0,
    withdrawals    : 0,
    totalAmount    : '0 MAD',
    commissions    : '0 MAD',
    // Nouveaux — CDC §3.2 : statuts transferts
    pendingCount   : 0,
    paidCount      : 0,
    cancelledCount : 0,
  };

  // ─── 6 dernières opérations du jour ───────────────────────────────────────
  operations: CaisseOperationResponse[] = [];

  // ─── Données graphique 7 jours ────────────────────────────────────────────
  private weeklyData: { labels: string[]; sends: number[]; withdrawals: number[] } = {
    labels: [], sends: [], withdrawals: [],
  };

  readonly TypeOperation = TypeOperation;

  constructor(
    private caisseService: CaisseService,
    private auth: AuthService,
  ) {}

  ngOnInit(): void {
    this.loadDashboard();
  }

  ngAfterViewInit(): void {
    // Le graphique est initialisé après le chargement des données
  }

  ngOnDestroy(): void {
    this.chartInstance?.destroy();
  }

  // ─── Chargement ───────────────────────────────────────────────────────────

  loadDashboard(): void {
    this.loading = true;
    this.error   = '';

    forkJoin({
      opsJour : this.caisseService.operationsDuJour(this.agentEmail).pipe(catchError(() => of<CaisseOperationResponse[]>([])) ),
      solde   : this.caisseService.consulterSolde(this.agentEmail).pipe(catchError(() => of(0))),
    }).subscribe({
      next: ({ opsJour: rawOps, solde }) => {
        const opsJour = rawOps ?? [];

        // ── Stats de base ──────────────────────────────────────────────────
        const envois   = opsJour.filter(o => o.type === TypeOperation.ENVOI);
        const retraits = opsJour.filter(o => o.type === TypeOperation.RETRAIT);

        const totalMAD = envois.reduce((s, o) => s + o.montant, 0)
          + retraits.reduce((s, o) => s + o.montant, 0);

        // ── Statuts transferts (CDC §3.2) ──────────────────────────────────
        // Adaptation : si vos opérations portent un champ `statut`, filtrez ici.
        // Sinon, connectez à transfertService.getTransfertsParStatut() :
        const pending   = opsJour.filter(o => (o as any).statut === 'EN_ATTENTE');
        const paid      = opsJour.filter(o => (o as any).statut === 'PAYÉ');
        const cancelled = opsJour.filter(o => (o as any).statut === 'ANNULÉ' || (o as any).statut === 'EXPIRÉ');

        this.stats = {
          transfersCount : envois.length + retraits.length,
          sends          : envois.length,
          withdrawals    : retraits.length,
          totalAmount    : this.fmt(totalMAD),
          commissions    : '— MAD',
          pendingCount   : pending.length,
          paidCount      : paid.length,
          cancelledCount : cancelled.length,
        };

        // ── Tableau : 6 dernières opérations triées du plus récent ─────────
        this.operations = [...opsJour]
          .sort((a, b) => new Date(b.dateHeure).getTime() - new Date(a.dateHeure).getTime())
          .slice(0, 6);

        // ── Données graphique 7 jours (simulées si pas d'endpoint dédié) ──
        this.buildWeeklyData(opsJour);

        this.loading = false;

        // Initialiser le graphique après le rendu du canvas
        setTimeout(() => this.initChart(), 0);
      },
      error: () => {
        this.error   = 'Erreur lors du chargement du tableau de bord.';
        this.loading = false;
      },
    });
  }

  // ─── Graphique Chart.js (CDC §3.2) ────────────────────────────────────────

  private buildWeeklyData(todayOps: CaisseOperationResponse[]): void {
    const labels: string[]      = [];
    const sendsArr: number[]    = [];
    const withdrawArr: number[] = [];

    for (let i = 6; i >= 0; i--) {
      const d = new Date();
      d.setDate(d.getDate() - i);
      labels.push(d.toLocaleDateString('fr-MA', { weekday: 'short', day: 'numeric' }));

      // Pour aujourd'hui (i === 0) : données réelles ; pour les autres jours, mettre 0
      // (à remplacer par un vrai appel API semaine)
      if (i === 0) {
        sendsArr.push(todayOps.filter(o => o.type === TypeOperation.ENVOI).length);
        withdrawArr.push(todayOps.filter(o => o.type === TypeOperation.RETRAIT).length);
      } else {
        // Données fictives pour l'affichage — remplacer par l'API
        sendsArr.push(0);
        withdrawArr.push(0);
      }
    }

    this.weeklyData = { labels, sends: sendsArr, withdrawals: withdrawArr };
  }

  private initChart(): void {
    const canvas = this.activityChartRef?.nativeElement;
    if (!canvas) return;

    this.chartInstance?.destroy();

    this.chartInstance = new Chart(canvas, {
      type: 'bar',
      data: {
        labels: this.weeklyData.labels,
        datasets: [
          {
            label: 'Envois',
            data: this.weeklyData.sends,
            backgroundColor: 'rgba(251, 191, 36, 0.7)',
            borderColor: '#f59e0b',
            borderWidth: 1.5,
            borderRadius: 6,
          },
          {
            label: 'Retraits',
            data: this.weeklyData.withdrawals,
            backgroundColor: 'rgba(167, 139, 250, 0.7)',
            borderColor: '#a78bfa',
            borderWidth: 1.5,
            borderRadius: 6,
          },
        ],
      },
      options: {
        responsive: false,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
          tooltip: {
            backgroundColor: '#1e293b',
            titleColor: '#94a3b8',
            bodyColor: '#f1f5f9',
            borderColor: 'rgba(255,255,255,0.08)',
            borderWidth: 1,
            padding: 10,
          },
        },
        scales: {
          x: {
            grid: { color: 'rgba(255,255,255,0.04)' },
            ticks: { color: '#6b7280', font: { size: 11 } },
          },
          y: {
            grid: { color: 'rgba(255,255,255,0.04)' },
            ticks: { color: '#6b7280', font: { size: 11 }, stepSize: 1 },
            beginAtZero: true,
          },
        },
      },
    });
  }

  // ─── Impression du reçu (CDC §3.2) ───────────────────────────────────────

  printReceipt(op: CaisseOperationResponse): void {
    const sign    = op.type === TypeOperation.RETRAIT ? '−' : '+';
    const amount  = `${sign} ${this.fmt(op.montant)}`;
    const time    = this.formatTime(op.dateHeure);
    const ref     = op.referenceTransfert ?? '—';
    const typeLabel = op.type;
    const agent   = op.agentNom;
    const date    = new Date(op.dateHeure).toLocaleDateString('fr-MA', {
      year: 'numeric', month: 'long', day: 'numeric',
    });

    const html = `
      <!DOCTYPE html>
      <html lang="fr">
      <head>
        <meta charset="UTF-8"/>
        <title>Reçu — ${ref}</title>
        <style>
          * { margin: 0; padding: 0; box-sizing: border-box; }
          body { font-family: 'Courier New', monospace; padding: 24px; color: #111; font-size: 13px; }
          .header { text-align: center; border-bottom: 2px dashed #ccc; padding-bottom: 12px; margin-bottom: 16px; }
          .header h1 { font-size: 18px; font-weight: 700; letter-spacing: 2px; }
          .header p  { font-size: 11px; color: #555; margin-top: 4px; }
          .row { display: flex; justify-content: space-between; padding: 5px 0; border-bottom: 1px dashed #eee; }
          .row .label { color: #555; }
          .row .value { font-weight: 600; }
          .amount { font-size: 22px; font-weight: 700; text-align: center; margin: 16px 0; }
          .footer { text-align: center; font-size: 10px; color: #888; margin-top: 20px; border-top: 2px dashed #ccc; padding-top: 12px; }
          @media print { body { padding: 0; } }
        </style>
      </head>
      <body>
        <div class="header">
          <h1>OKANE TRANSFER</h1>
          <p>Reçu de transaction</p>
        </div>
        <div class="row"><span class="label">Date</span><span class="value">${date}</span></div>
        <div class="row"><span class="label">Heure</span><span class="value">${time}</span></div>
        <div class="row"><span class="label">Référence</span><span class="value">${ref}</span></div>
        <div class="row"><span class="label">Type</span><span class="value">${typeLabel}</span></div>
        <div class="row"><span class="label">Agent</span><span class="value">${agent}</span></div>
        <div class="amount">${amount}</div>
        <div class="footer">
          <p>Merci pour votre confiance.</p>
          <p>Conservez ce reçu comme justificatif.</p>
        </div>
      </body>
      </html>
    `;

    const win = window.open('', '_blank', 'width=400,height=600');
    if (win) {
      win.document.write(html);
      win.document.close();
      win.focus();
      setTimeout(() => { win.print(); win.close(); }, 300);
    }
  }

  // ─── Helpers template ─────────────────────────────────────────────────────

  formatTime(dateHeure: string): string {
    const d = new Date(dateHeure);
    return `${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}`;
  }

  formatAmount(op: CaisseOperationResponse): string {
    const sign = op.type === TypeOperation.RETRAIT ? '−' : '+';
    return `${sign} ${this.fmt(op.montant)}`;
  }

  amountClass(op: CaisseOperationResponse): string {
    return op.type === TypeOperation.RETRAIT ? 'text-red-400' : 'text-emerald-400';
  }

  badgeClass(type: TypeOperation): string {
    switch (type) {
      case TypeOperation.ENVOI:     return 'bg-blue-500/15 text-blue-400';
      case TypeOperation.RETRAIT:   return 'bg-purple-500/15 text-purple-400';
      case TypeOperation.OUVERTURE: return 'bg-amber-500/15 text-amber-400';
      default:                      return 'bg-gray-500/15 text-gray-400';
    }
  }

  // ─── Utilitaires privés ───────────────────────────────────────────────────

  private fmt(n: number): string {
    return new Intl.NumberFormat('fr-MA').format(n) + ' MAD';
  }

  private todayStr(): string {
    return this.fmtDate(new Date());
  }

  private fmtDate(d: Date): string {
    const y = d.getFullYear();
    const m = String(d.getMonth() + 1).padStart(2, '0');
    const j = String(d.getDate()).padStart(2, '0');
    return `${y}-${m}-${j}`;
  }
}
