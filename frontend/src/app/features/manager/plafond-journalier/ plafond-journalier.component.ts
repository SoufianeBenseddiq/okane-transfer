import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';

// ── MOCK DATA (replace with real service calls when backend is ready) ──────────

interface HourlyVolume {
  hour: string;
  volume: number; // in k MAD
}

interface DailyHistory {
  date: string;
  plafondAutorise: number;
  montantTraite: number;
  utilisation: number; // percentage
  depassement: boolean;
}

interface PlafondStats {
  plafondAutorise: number;
  montantUtilise: number;
  pourcentageUtilise: number;
  restant: number;
  date: string;
  picHoraire: string;
  picVolume: number;
  moyenneHeure: number;
  heureSaturationEst: string;
  compareHier: number;
}

const MOCK_STATS: PlafondStats = {
  plafondAutorise:    200_000,
  montantUtilise:     185_000,
  pourcentageUtilise: 93,
  restant:            15_000,
  date:               '22 / 05 / 2026',
  picHoraire:         '14h',
  picVolume:          24,
  moyenneHeure:       16.8,
  heureSaturationEst: '≈ 16h30',
  compareHier:        4
};

const MOCK_HOURLY: HourlyVolume[] = [
  { hour: '08h', volume: 5  },
  { hour: '09h', volume: 12 },
  { hour: '10h', volume: 22 },
  { hour: '11h', volume: 35 },
  { hour: '12h', volume: 52 },
  { hour: '13h', volume: 74 },
  { hour: '14h', volume: 98 },
  { hour: '15h', volume: 120 },
  { hour: '16h', volume: 148 },
  { hour: '17h', volume: 168 },
  { hour: '18h', volume: 185 },
];

const MOCK_HISTORY: DailyHistory[] = [
  { date: '21/05 · 2026', plafondAutorise: 200_000, montantTraite: 178_000, utilisation: 89, depassement: false },
  { date: '20/05 · 2026', plafondAutorise: 200_000, montantTraite: 152_000, utilisation: 76, depassement: false },
  { date: '19/05 · 2026', plafondAutorise: 200_000, montantTraite: 196_000, utilisation: 98, depassement: true  },
  { date: '18/05 · 2026', plafondAutorise: 200_000, montantTraite:  84_000, utilisation: 42, depassement: false },
  { date: '17/05 · 2026', plafondAutorise: 200_000, montantTraite: 162_000, utilisation: 81, depassement: false },
  { date: '16/05 · 2026', plafondAutorise: 200_000, montantTraite: 145_000, utilisation: 73, depassement: false },
];

// ────────────────────────────────────────────────────────────────────────────────

@Component({
  selector: 'app-plafond-journalier',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './plafond-journalier.component.html',
  styleUrls: ['./plafond-journalier.component.scss']
})
export class PlafondJournalierComponent implements OnInit {

  // ── State ──────────────────────────────────────────────────────────
  loading = false;
  error   = '';
  submitSuccess = false;
  submitLoading = false;

  // ── Data ───────────────────────────────────────────────────────────
  stats:   PlafondStats   = MOCK_STATS;
  hourly:  HourlyVolume[] = MOCK_HOURLY;
  history: DailyHistory[] = MOCK_HISTORY;

  // ── Revision form ──────────────────────────────────────────────────
  revisionForm = this.fb.group({
    nouveauPlafond: [null as number | null, [Validators.required, Validators.min(1)]],
    justification:  ['', [Validators.required, Validators.minLength(20)]]
  });

  // ── Chart config ───────────────────────────────────────────────────
  readonly chartW = 700;
  readonly chartH = 160;
  readonly pad = { top: 12, right: 20, bottom: 28, left: 10 };

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    // ── Using mock data ────────────────────────────────────────────────
    // Real calls to uncomment when the backend endpoints are ready:
    //
    // this.loading = true;
    //
    // forkJoin({
    //   stats:   this.caisseService.consulterSolde(this.auth.currentUser!.email),
    //   history: this.caisseService.findAllClotures()          // or a dedicated plafond endpoint
    // }).pipe(takeUntil(this.destroy$)).subscribe({
    //   next: ({ stats, history }) => {
    //     // map response to this.stats / this.history
    //     this.loading = false;
    //   },
    //   error: err => {
    //     this.error = err.error?.message ?? 'Erreur de chargement';
    //     this.loading = false;
    //   }
    // });

    this.stats   = MOCK_STATS;
    this.hourly  = MOCK_HOURLY;
    this.history = MOCK_HISTORY;
  }

  submitRevision(): void {
    if (this.revisionForm.invalid) {
      this.revisionForm.markAllAsTouched();
      return;
    }

    this.submitLoading = true;
    this.submitSuccess = false;

    // ── Using mock submission ──────────────────────────────────────────
    // Real call to uncomment when the backend endpoint is ready:
    //
    // const body = {
    //   nouveauPlafond: this.revisionForm.value.nouveauPlafond,
    //   justification:  this.revisionForm.value.justification
    // };
    // this.agenceService.demanderRevisionPlafond(body).subscribe({
    //   next: () => {
    //     this.submitSuccess = true;
    //     this.submitLoading = false;
    //     this.revisionForm.reset();
    //   },
    //   error: err => {
    //     this.error = err.error?.message ?? 'Erreur lors de l\'envoi';
    //     this.submitLoading = false;
    //   }
    // });

    setTimeout(() => {
      this.submitSuccess = true;
      this.submitLoading = false;
      this.revisionForm.reset();
    }, 1000);
  }

  // ── Donut chart SVG helpers ────────────────────────────────────────
  get donutRadius(): number { return 70; }
  get donutCx(): number     { return 100; }
  get donutCy(): number     { return 100; }
  get donutCircumference(): number { return 2 * Math.PI * this.donutRadius; }

  get donutOffset(): number {
    const pct = this.stats.pourcentageUtilise / 100;
    return this.donutCircumference * (1 - pct);
  }

  get donutColor(): string {
    if (this.stats.pourcentageUtilise >= 95) return '#ef4444';
    if (this.stats.pourcentageUtilise >= 80) return '#f97316';
    if (this.stats.pourcentageUtilise >= 60) return '#f59e0b';
    return '#22c55e';
  }

  // ── Area chart SVG helpers ─────────────────────────────────────────
  get chartPath(): string {
    if (!this.hourly.length) return '';
    const maxVol = Math.max(...this.hourly.map(h => h.volume));
    const innerW = this.chartW - this.pad.left - this.pad.right;
    const innerH = this.chartH - this.pad.top  - this.pad.bottom;

    const pts = this.hourly.map((h, i) => ({
      x: this.pad.left + (i / (this.hourly.length - 1)) * innerW,
      y: this.pad.top  + innerH - (h.volume / maxVol) * innerH
    }));

    let d = `M ${pts[0].x} ${pts[0].y}`;
    for (let i = 1; i < pts.length; i++) {
      const cp1x = pts[i-1].x + (pts[i].x - pts[i-1].x) / 3;
      const cp2x = pts[i].x   - (pts[i].x - pts[i-1].x) / 3;
      d += ` C ${cp1x} ${pts[i-1].y}, ${cp2x} ${pts[i].y}, ${pts[i].x} ${pts[i].y}`;
    }
    return d;
  }

  get chartAreaPath(): string {
    const line = this.chartPath;
    if (!line) return '';
    const innerW = this.chartW - this.pad.left - this.pad.right;
    const innerH = this.chartH - this.pad.top  - this.pad.bottom;
    const lastX  = this.pad.left + innerW;
    const botY   = this.pad.top  + innerH;
    return `${line} L ${lastX} ${botY} L ${this.pad.left} ${botY} Z`;
  }

  get xLabels(): { x: number; label: string }[] {
    const innerW = this.chartW - this.pad.left - this.pad.right;
    return this.hourly
      .filter((_, i) => i % 2 === 0)
      .map(h => {
        const i = this.hourly.indexOf(h);
        return {
          x: this.pad.left + (i / (this.hourly.length - 1)) * innerW,
          label: h.hour
        };
      });
  }

  // ── History bar helpers ────────────────────────────────────────────
  barColor(pct: number): string {
    if (pct >= 95) return '#ef4444';
    if (pct >= 80) return '#f59e0b';
    if (pct >= 60) return '#22c55e';
    return '#06b6d4';
  }

  // ── Utils ──────────────────────────────────────────────────────────
  fmt(n: number): string {
    return new Intl.NumberFormat('fr-MA').format(n);
  }
}