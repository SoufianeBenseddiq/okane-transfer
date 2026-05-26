import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, forkJoin } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { TransfertService } from '../../../core/services/transfert.service';
import { UserService }      from '../../../core/services/user.service';
import { AgenceService }    from '../../../core/services/agence.service';
import { AuthService }      from '../../../core/services/auth.service';

import { TransfertResponse, TransfertWithMeta }                  from '../../../core/models/transfert';
import { UserResponse }                       from '../../../core/models/user';
import { AgenceResponse }                     from '../../../core/models/agence';
import { StatutTransfert, RoleUtilisateur }   from '../../../core/models/enums';

export type PeriodFilter = 'today' | 'week' | 'month' | 'custom';

interface KpiStats {
  totalTransferts:         number;
  caTotal:                 number;
  commissionsAgence:       number;
  meilleurAgent:           UserResponse | null;
  meilleurAgentCommission: number;
  growthTransferts:        number;
  growthCa:                number;
}

interface DailyVolume {
  day:    number;
  volume: number;
}

@Component({
  selector: 'app-rapport-agence',
  standalone: true,
  imports: [CommonModule, FormsModule, DatePipe],
  templateUrl: './ rapport-agence.component.html',
  styleUrls: ['./rapport-agence.component.scss']
})
export class RapportAgenceComponent implements OnInit, OnDestroy {

  private destroy$ = new Subject<void>();

  loading = true;
  error   = '';
  selectedPeriod: PeriodFilter = 'month';

  transferts: TransfertWithMeta[] = [];
filteredTransferts: TransfertWithMeta[] = [];
  agents:             UserResponse[]      = [];
  agence:             AgenceResponse | null = null;

  kpis: KpiStats = {
    totalTransferts: 0, caTotal: 0, commissionsAgence: 0,
    meilleurAgent: null, meilleurAgentCommission: 0,
    growthTransferts: 0, growthCa: 0
  };
  dailyVolumes: DailyVolume[] = [];

  filterDate      = '';
  filterAgent     = 'tous';
  filterCorridor  = 'tous';
  filterStatuts: string[] = [];

  StatutTransfert = StatutTransfert;

  readonly chartWidth   = 900;
  readonly chartHeight  = 160;
  readonly chartPadding = { top: 10, right: 20, bottom: 25, left: 20 };
Math: Math = Math; // to use Math in template

  get currentUser():     UserResponse | null { return this.auth.currentUser; }
  get agentOptions():    string[] { return ['tous', ...this.agents.map(a => a.email)]; }
  get corridorOptions(): string[] {
    const cors = [...new Set(this.transferts.map(t => (t as any).corridor?.libelle).filter(Boolean))];
    return ['tous', ...cors];
  }

  constructor(
    private transfertService: TransfertService,
    private userService:      UserService,
    private agenceService:    AgenceService,
    private auth:             AuthService
  ) {}

  ngOnInit():  void { this.loadData(); }
  ngOnDestroy(): void { this.destroy$.next(); this.destroy$.complete(); }

  loadData(): void {
    //const email = this.auth.currentUser?.email;
    //if (!email) return;
    const email='manager.marrakech@okane.com';

    this.loading = true;
    this.error   = '';

    this.agenceService.findByResponsable(email)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: agence => {
          this.agence = agence;
          this.loadScopedData(agence.id);
        },
        error: err => {
          this.error   = err.error?.message ?? 'Impossible de charger les données de l\'agence';
          this.loading = false;
        }
      });
  }

  private loadScopedData(agenceId: number): void {
    const { debut, fin } = this.getPeriodRange();

    forkJoin({
      transferts: this.transfertService.getByAgence(agenceId, debut, fin),
      agents: this.userService.findByAgence(agenceId, RoleUtilisateur.ROLE_AGENT)
    })
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: ({ transferts, agents }) => {
          this.transferts = transferts;
          this.agents     = agents;
          this.applyFilters();
          this.computeKpis();
          this.computeDailyVolumes();
          this.loading = false;
        },
        error: err => {
          this.error   = err.error?.message ?? 'Erreur lors du chargement des données';
          this.loading = false;
        }
      });
  }

  setPeriod(period: PeriodFilter): void {
    this.selectedPeriod = period;
    if (this.agence) {
      this.loading = true;
      this.loadScopedData(this.agence.id);
    }
  }

  private getPeriodRange(): { debut: Date; fin: Date } {
    const now    = new Date();
    const pad    = (n: number) => String(n).padStart(2, '0');
    const toStr  = (d: Date)   => `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())}`;
    const today  = now;

    if (this.selectedPeriod === 'today') {
      return { debut: today, fin: today };
    }
    if (this.selectedPeriod === 'week') {
      const weekAgo = new Date(now.getTime() - 6 * 86400000);
      return { debut: weekAgo, fin: today };
    }
    if (this.selectedPeriod === 'month') {
      const firstDay = new Date(now.getFullYear(), now.getMonth(), 1);
      return { debut: firstDay, fin: today };
    }
    return { debut: new Date(''), fin: new Date('') };
  }

  applyFilters(): void {
    let result = [...this.transferts];
    if (this.filterDate)                result = result.filter(t => t.creeLe?.startsWith(this.filterDate));
    if (this.filterAgent !== 'tous')    result = result.filter(t => (t as any).agent?.email === this.filterAgent);
    if (this.filterCorridor !== 'tous') result = result.filter(t => (t as any).corridor?.libelle === this.filterCorridor);
    this.filteredTransferts = result;
  }

  computeKpis(): void {
    const data = this.filteredTransferts;
    this.kpis.totalTransferts   = data.length;
    this.kpis.caTotal           = data.reduce((s, t) => s + (t.montantEnvoye ?? 0), 0);
    this.kpis.commissionsAgence = data.reduce((s, t) => s + ((t as any).frais ?? 0), 0);

    const map = new Map<number, { agent: UserResponse; commission: number }>();
    data.forEach(t => {
      const a = (t as any).agent as UserResponse | undefined;
      if (a) {
        const cur = map.get(a.id);
        if (cur) cur.commission += (t as any).frais ?? 0;
        else     map.set(a.id, { agent: a, commission: (t as any).frais ?? 0 });
      }
    });
    const best = Array.from(map.values()).reduce<{ agent: UserResponse; commission: number } | null>(
      (acc, v) => (!acc || v.commission > acc.commission ? v : acc),
      null
    );
    this.kpis.meilleurAgent           = best ? best.agent : null;
    this.kpis.meilleurAgentCommission = best ? best.commission : 0;

    this.kpis.growthTransferts = 0;
    this.kpis.growthCa         = 0;
  }

  computeDailyVolumes(): void {
    const now         = new Date();
    const daysInMonth = new Date(now.getFullYear(), now.getMonth()+1, 0).getDate();
    const volumes: DailyVolume[] = [];
    for (let d = 1; d <= Math.min(daysInMonth, now.getDate()); d++) {
      const prefix = `${now.getFullYear()}-${String(now.getMonth()+1).padStart(2,'0')}-${String(d).padStart(2,'0')}`;
      const vol = this.transferts
        .filter(t => t.creeLe?.startsWith(prefix))
        .reduce((s, t) => s + (t.montantEnvoye ?? 0), 0);
      volumes.push({ day: d, volume: vol / 1000 });
    }
    this.dailyVolumes = volumes;
  }

  get chartPath(): string {
    if (this.dailyVolumes.length < 2) return '';
    const maxVol = Math.max(...this.dailyVolumes.map(d => d.volume), 1);
    const iW = this.chartWidth  - this.chartPadding.left - this.chartPadding.right;
    const iH = this.chartHeight - this.chartPadding.top  - this.chartPadding.bottom;
    const pts = this.dailyVolumes.map((d, i) => ({
      x: this.chartPadding.left + (i / (this.dailyVolumes.length - 1)) * iW,
      y: this.chartPadding.top  + iH - (d.volume / maxVol) * iH
    }));
    let path = `M ${pts[0].x} ${pts[0].y}`;
    for (let i = 1; i < pts.length; i++) {
      const c1x = pts[i-1].x + (pts[i].x - pts[i-1].x) / 3;
      const c2x = pts[i].x   - (pts[i].x - pts[i-1].x) / 3;
      path += ` C ${c1x} ${pts[i-1].y}, ${c2x} ${pts[i].y}, ${pts[i].x} ${pts[i].y}`;
    }
    return path;
  }

  get chartAreaPath(): string {
    const line = this.chartPath;
    if (!line) return '';
    const iW = this.chartWidth  - this.chartPadding.left - this.chartPadding.right;
    const iH = this.chartHeight - this.chartPadding.top  - this.chartPadding.bottom;
    return `${line} L ${this.chartPadding.left + iW} ${this.chartPadding.top + iH} L ${this.chartPadding.left} ${this.chartPadding.top + iH} Z`;
  }

  get chartXLabels(): { x: number; label: number }[] {
    if (!this.dailyVolumes.length) return [];
    const iW   = this.chartWidth - this.chartPadding.left - this.chartPadding.right;
    const step = Math.ceil(this.dailyVolumes.length / 5);
    return this.dailyVolumes
      .filter((_, i) => i % step === 0 || i === this.dailyVolumes.length - 1)
      .map(d => {
        const idx = this.dailyVolumes.indexOf(d);
        return { x: this.chartPadding.left + (idx / (this.dailyVolumes.length - 1)) * iW, label: d.day };
      });
  }

  getStatutClass(statut: StatutTransfert): string {
    return ({ [StatutTransfert.EN_ATTENTE]: 'statut-attente', [StatutTransfert.PAYE]: 'statut-paye',
               [StatutTransfert.ANNULE]: 'statut-annule', [StatutTransfert.EXPIRE]: 'statut-expire',
               [StatutTransfert.BLOQUE]: 'statut-bloque' } as Record<string,string>)[statut] ?? '';
  }

  getStatutLabel(statut: StatutTransfert): string {
    return ({ [StatutTransfert.EN_ATTENTE]: 'En attente', [StatutTransfert.PAYE]: 'Payé',
               [StatutTransfert.ANNULE]: 'Annulé', [StatutTransfert.EXPIRE]: 'Expiré',
               [StatutTransfert.BLOQUE]: 'Bloqué' } as Record<string,string>)[statut] ?? statut;
  }

  getAgentInitials(agent: UserResponse | null | undefined): string {
    if (!agent) return '?';
    return `${agent.prenom?.[0] ?? ''}${agent.nom?.[0] ?? ''}`.toUpperCase();
  }

  formatAmount(n: number): string { return new Intl.NumberFormat('fr-MA').format(n); }
  exportPdf(): void { window.print(); }
  onFilterChange(): void { this.applyFilters(); this.computeKpis(); }
  getMaxVolume(): number {
  if (!this.dailyVolumes.length) return 1;

  return Math.max(
    ...this.dailyVolumes.map((d) => d.volume),
    1
  );
}
}
