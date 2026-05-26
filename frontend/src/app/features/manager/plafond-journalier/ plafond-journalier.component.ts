import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Subject, forkJoin } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { CaisseService }    from '../../../core/services/caisse.service';
import { TransfertService } from '../../../core/services/transfert.service';
import { AgenceService }    from '../../../core/services/agence.service';
import { AuthService }      from '../../../core/services/auth.service';

import { ClotureCaisseResponse } from '../../../core/models/caisse';
import { TransfertResponse }     from '../../../core/models/transfert';
import { AgenceResponse }        from '../../../core/models/agence';

// ── Local view-model interfaces ──────────────────────────────────────────────

interface HourlyVolume {
  hour:   string;
  volume: number; // cumulative k MAD up to that hour
}

interface DailyHistory {
  date:            string;
  plafondAutorise: number;
  montantTraite:   number;
  utilisation:     number; // 0–100 %
  depassement:     boolean;
}

interface PlafondStats {
  plafondAutorise:    number;
  montantUtilise:     number;
  pourcentageUtilise: number;
  restant:            number;
  date:               string;
  picHoraire:         string;
  picVolume:          number;
  moyenneHeure:       number;
  heureSaturationEst: string;
  compareHier:        number;
}

@Component({
  selector: 'app-plafond-journalier',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './plafond-journalier.component.html',
  styleUrls: ['./plafond-journalier.component.scss']
})
export class PlafondJournalierComponent implements OnInit, OnDestroy {

  private destroy$ = new Subject<void>();

  // ── State ────────────────────────────────────────────────────────────
  loading       = false;
  error         = '';
  submitSuccess = false;
  submitLoading = false;

  // ── Data ─────────────────────────────────────────────────────────────
  stats:   PlafondStats   = this.emptyStats();
  hourly:  HourlyVolume[] = [];
  history: DailyHistory[] = [];
  agence:  AgenceResponse | null = null;

  // ── Revision form ─────────────────────────────────────────────────────
  revisionForm = this.fb.group({
    nouveauPlafond: [null as number | null, [Validators.required, Validators.min(1)]],
    justification:  ['', [Validators.required, Validators.minLength(20)]]
  });

  // ── Chart config ──────────────────────────────────────────────────────
  readonly chartW = 700;
  readonly chartH = 160;
  readonly pad    = { top: 12, right: 20, bottom: 28, left: 10 };

  constructor(
    private fb:               FormBuilder,
    private caisseService:    CaisseService,
    private transfertService: TransfertService,
    private agenceService:    AgenceService,
    private auth:             AuthService
  ) {}

  ngOnInit():    void { this.loadData(); }
  ngOnDestroy(): void { this.destroy$.next(); this.destroy$.complete(); }

  // ═══════════════════════════════════════════════════════════════════════
  // DATA LOADING — scoped to manager's agency
  //
  // Step 1:  GET /api/agences/responsable/{email}
  //          → resolves agenceId + plafondJournalier
  //          → AgenceService.findByResponsable(email)   ← already exists
  //
  // Step 2 (parallel):
  //
  //  A. GET /api/caisse-operations/agent/{email}/solde
  //     → number  (total MAD processed by this manager's agents today)
  //     → CaisseService.consulterSolde(email)           ← already exists
  //     ⚠  NOTE: this returns ONE agent's balance — see note below
  //
  //  B. GET /api/clotures-caisse/agent/{email}/ecarts   (returns last 30 days)
  //     OR better ─ NEW ENDPOINT REQUIRED (see box below)
  //
  //  C. NEW ENDPOINT REQUIRED ─────────────────────────────────────────────
  //  ┌──────────────────────────────────────────────────────────────────────┐
  //  │  GET /api/transferts/agence/{agenceId}?debut=&fin=                   │
  //  │  → TransfertResponse[]  (today's transfers for hourly chart)         │
  //  │                                                                       │
  //  │  Java — TransfertController.java:                                    │
  //  │  @GetMapping("/agence/{agenceId}")                                   │
  //  │  @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")                      │
  //  │  public ResponseEntity<List<TransfertResponse>> getByAgence(         │
  //  │      @PathVariable Long agenceId,                                    │
  //  │      @RequestParam(required=false)                                   │
  //  │          @DateTimeFormat(iso=DATE) LocalDate debut,                  │
  //  │      @RequestParam(required=false)                                   │
  //  │          @DateTimeFormat(iso=DATE) LocalDate fin) {                  │
  //  │      return ResponseEntity.ok(                                       │
  //  │          transfertService.findByAgence(agenceId, debut, fin));       │
  //  │  }                                                                   │
  //  │                                                                      │
  //  │  Frontend — add to TransfertService:                                 │
  //  │  getByAgence(id, debut?, fin?): Observable<TransfertResponse[]>      │
  //  └──────────────────────────────────────────────────────────────────────┘
  //
  //  D. NEW ENDPOINT REQUIRED ─────────────────────────────────────────────
  //  ┌──────────────────────────────────────────────────────────────────────┐
  //  │  GET /api/clotures-caisse/agence/{agenceId}?limit=30                 │
  //  │  → ClotureCaisseResponse[]  (last 30 days for this agency)           │
  //  │                                                                       │
  //  │  Java — ClotureCaisseController.java:                                │
  //  │  @GetMapping("/agence/{agenceId}")                                   │
  //  │  @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")                      │
  //  │  public ResponseEntity<List<ClotureCaisseResponse>> getByAgence(     │
  //  │      @PathVariable Long agenceId,                                    │
  //  │      @RequestParam(defaultValue="30") int limit) {                   │
  //  │      return ResponseEntity.ok(                                       │
  //  │          clotureCaisseService.findByAgence(agenceId, limit));        │
  //  │  }                                                                   │
  //  │                                                                      │
  //  │  Frontend — add to CaisseService:                                    │
  //  │  findCloтuresByAgence(agenceId, limit?): Observable<...>             │
  //  └──────────────────────────────────────────────────────────────────────┘
  //
  //  ⚠  ABOUT consulterSolde:
  //     Currently returns one agent's caisse balance, not the agency-wide
  //     daily total. The correct value for the donut gauge is:
  //       SUM(montantEnvoye) of today's transferts for this agency
  //     which is derived from the transferts fetched in step C.
  //     So consulterSolde is NOT used here — we compute solde from transferts.
  // ═══════════════════════════════════════════════════════════════════════
  loadData(): void {
   const  email = 'manager.marrakech@okane.com';
    //const email = this.auth.currentUser?.email;
  //  if (!email) return;

    this.loading = true;
    this.error   = '';

    // Step 1 — resolve agenceId
    this.agenceService.findByResponsable(email)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next:  agence => { this.agence = agence; this.loadScopedData(agence); },
        error: err    => { this.error = err.error?.message ?? 'Agence introuvable'; this.loading = false; }
      });
  }

  private loadScopedData(agence: AgenceResponse): void {
    const today     = new Date();
    const todayStr  = this.toDateStr(today);
    const thirtyAgo = this.toDateStr(new Date(today.getTime() - 29 * 86400000));

    forkJoin({
      // Today's transfers → donut gauge + hourly chart + mini-stats
      todayTransferts: this.transfertService.getByAgence(agence.id, todayStr, todayStr),

      // Last 30 days clotures → history table + yesterday comparison
      clotures: this.caisseService.findCloturesByAgence(agence.id, 30)
    })
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: ({ todayTransferts, clotures }) => {
          const plafond = (agence as any).plafondJournalier ?? 200_000;
          this.stats   = this.buildStats(todayTransferts, plafond, clotures);
          this.hourly  = this.buildHourly(todayTransferts);
          this.history = this.buildHistory(clotures, plafond);
          this.loading = false;
        },
        error: err => {
          this.error   = err.error?.message ?? 'Erreur lors du chargement';
          this.loading = false;
        }
      });
  }

  // ═══════════════════════════════════════════════════════════════════════
  // FORM SUBMIT
  //
  //  NEW ENDPOINT REQUIRED:
  //  ┌──────────────────────────────────────────────────────────────────────┐
  //  │  POST /api/agences/id/{id}/revision-plafond                          │
  //  │  Body: { nouveauPlafond: number, justification: string }             │
  //  │  Response: 201 Created                                               │
  //  │                                                                      │
  //  │  Frontend — add to AgenceService:                                    │
  //  │  demanderRevisionPlafond(agenceId, body): Observable<void>           │
  //  └──────────────────────────────────────────────────────────────────────┘
  // ═══════════════════════════════════════════════════════════════════════
  submitRevision(): void {
    if (this.revisionForm.invalid) { this.revisionForm.markAllAsTouched(); return; }

    this.submitLoading = true;
    this.submitSuccess = false;

    const body = {
      nouveauPlafond: this.revisionForm.value.nouveauPlafond as number,
      justification:  this.revisionForm.value.justification  as string
    };

    this.agenceService.demanderRevisionPlafond(this.agence!.id, body)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.submitSuccess = true;
          this.submitLoading = false;
          this.revisionForm.reset();
        },
        error: err => {
          this.error        = err.error?.message ?? "Erreur lors de l'envoi de la demande";
          this.submitLoading = false;
        }
      });
  }

  // ═══════════════════════════════════════════════════════════════════════
  // DATA MAPPING HELPERS
  // ═══════════════════════════════════════════════════════════════════════

  private buildStats(
    transferts: TransfertResponse[],
    plafond:    number,
    clotures:   ClotureCaisseResponse[]
  ): PlafondStats {
    // Total used today = sum of all today's transferts
    const montantUtilise = transferts.reduce((s, t) => s + (t.montantEnvoye ?? 0), 0);

    // Hourly breakdown for pic and moyenne
    const hourMap = new Map<number, number>();
    transferts.forEach(t => {
      const h = new Date(t.creeLe).getHours();
      hourMap.set(h, (hourMap.get(h) ?? 0) + (t.montantEnvoye ?? 0));
    });

    let picHour = 0, picVol = 0;
    hourMap.forEach((vol, h) => { if (vol > picVol) { picVol = vol; picHour = h; } });

    const hoursWorked  = hourMap.size || 1;
    const moyenneHeure = montantUtilise / hoursWorked / 1000;

    // Saturation estimate
    const restant  = Math.max(plafond - montantUtilise, 0);
    const now      = new Date();
    const satLabel = moyenneHeure > 0
      ? (() => {
          const satMs = now.getTime() + (restant / 1000 / moyenneHeure) * 3600000;
          const sat   = new Date(satMs);
          return `≈ ${String(sat.getHours()).padStart(2,'0')}h${String(sat.getMinutes()).padStart(2,'0')}`;
        })()
      : '—';

    // Compare with yesterday via clotures
    const yesterday  = this.toDateStr(new Date(now.getTime() - 86400000));
    const yestClot   = clotures.find(c => c.date?.startsWith(yesterday));
    const yestMontant = (yestClot as any)?.montantTraite ?? 0;
    const compareHier = yestMontant > 0
      ? Math.round(((montantUtilise - yestMontant) / yestMontant) * 100)
      : 0;

    return {
      plafondAutorise:    plafond,
      montantUtilise,
      pourcentageUtilise: plafond > 0 ? Math.min(Math.round((montantUtilise / plafond) * 100), 100) : 0,
      restant,
      date:               now.toLocaleDateString('fr-FR', { day:'2-digit', month:'2-digit', year:'numeric' }),
      picHoraire:         `${String(picHour).padStart(2,'0')}h`,
      picVolume:          Math.round(picVol / 1000),
      moyenneHeure:       Math.round(moyenneHeure * 10) / 10,
      heureSaturationEst: satLabel,
      compareHier
    };
  }

  private buildHourly(transferts: TransfertResponse[]): HourlyVolume[] {
    const hourMap = new Map<number, number>();
    transferts.forEach(t => {
      const h = new Date(t.creeLe).getHours();
      hourMap.set(h, (hourMap.get(h) ?? 0) + (t.montantEnvoye ?? 0));
    });

    const now       = new Date().getHours();
    const startHour = 8;
    const result: HourlyVolume[] = [];
    let cumulative = 0;

    for (let h = startHour; h <= Math.max(now, startHour); h++) {
      cumulative += hourMap.get(h) ?? 0;
      result.push({ hour: `${String(h).padStart(2,'0')}h`, volume: cumulative / 1000 });
    }
    return result;
  }

  private buildHistory(clotures: ClotureCaisseResponse[], plafond: number): DailyHistory[] {
    return [...clotures]
      .sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime())
      .map(c => {
        const montant     = (c as any).montantTraite ?? 0;
        const utilisation = plafond > 0 ? Math.min(Math.round((montant / plafond) * 100), 100) : 0;
        const d           = new Date(c.date);
        return {
          date:            `${String(d.getDate()).padStart(2,'0')}/${String(d.getMonth()+1).padStart(2,'0')} · ${d.getFullYear()}`,
          plafondAutorise: plafond,
          montantTraite:   montant,
          utilisation,
          depassement:     montant > plafond
        };
      });
  }

  private emptyStats(): PlafondStats {
    return {
      plafondAutorise: 0, montantUtilise: 0, pourcentageUtilise: 0,
      restant: 0, date: '—', picHoraire: '—', picVolume: 0,
      moyenneHeure: 0, heureSaturationEst: '—', compareHier: 0
    };
  }

  private toDateStr(d: Date): string {
    return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`;
  }

  // ── Donut SVG helpers ──────────────────────────────────────────────────
  get donutRadius():        number { return 70; }
  get donutCircumference(): number { return 2 * Math.PI * this.donutRadius; }
  get donutOffset(): number {
    return this.donutCircumference * (1 - this.stats.pourcentageUtilise / 100);
  }
  get donutColor(): string {
    const p = this.stats.pourcentageUtilise;
    if (p >= 95) return '#ef4444';
    if (p >= 80) return '#f97316';
    if (p >= 60) return '#f59e0b';
    return '#22c55e';
  }

  // ── Area chart SVG helpers ─────────────────────────────────────────────
  get chartPath(): string {
    if (this.hourly.length < 2) return '';
    const maxVol = Math.max(...this.hourly.map(h => h.volume), 1);
    const iW = this.chartW - this.pad.left - this.pad.right;
    const iH = this.chartH - this.pad.top  - this.pad.bottom;
    const pts = this.hourly.map((h, i) => ({
      x: this.pad.left + (i / (this.hourly.length - 1)) * iW,
      y: this.pad.top  + iH - (h.volume / maxVol) * iH
    }));
    let d = `M ${pts[0].x} ${pts[0].y}`;
    for (let i = 1; i < pts.length; i++) {
      const c1x = pts[i-1].x + (pts[i].x - pts[i-1].x) / 3;
      const c2x = pts[i].x   - (pts[i].x - pts[i-1].x) / 3;
      d += ` C ${c1x} ${pts[i-1].y}, ${c2x} ${pts[i].y}, ${pts[i].x} ${pts[i].y}`;
    }
    return d;
  }

  get chartAreaPath(): string {
    const line = this.chartPath;
    if (!line) return '';
    const iW = this.chartW - this.pad.left - this.pad.right;
    const iH = this.chartH - this.pad.top  - this.pad.bottom;
    return `${line} L ${this.pad.left + iW} ${this.pad.top + iH} L ${this.pad.left} ${this.pad.top + iH} Z`;
  }

  get xLabels(): { x: number; label: string }[] {
    if (!this.hourly.length) return [];
    const iW = this.chartW - this.pad.left - this.pad.right;
    return this.hourly
      .filter((_, i) => i % 2 === 0)
      .map(h => {
        const i = this.hourly.indexOf(h);
        return { x: this.pad.left + (i / (this.hourly.length - 1)) * iW, label: h.hour };
      });
  }

  // ── Misc helpers ───────────────────────────────────────────────────────
  barColor(pct: number): string {
    if (pct >= 95) return '#ef4444';
    if (pct >= 80) return '#f59e0b';
    if (pct >= 60) return '#22c55e';
    return '#06b6d4';
  }

  fmt(n: number): string { return new Intl.NumberFormat('fr-MA').format(n); }
}