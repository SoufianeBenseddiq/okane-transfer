import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';
import { Router } from '@angular/router';
import { CaisseService } from '../../../core/services/caisse.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-cloture-caisse',
  standalone: true,
  imports: [CommonModule, FormsModule, TranslateModule],
  templateUrl: './cloture-caisse.component.html',
})
export class ClotureCaisseComponent implements OnInit {

  private router     = inject(Router);
  private caisse     = inject(CaisseService);
  private auth       = inject(AuthService);

  today = new Date();

  // ── State ──────────────────────────────────────────────────────────────────
  loading         = true;
  submitting      = false;
  errorMsg: string | null = null;
  successMsg: string | null = null;

  // ── Data loaded from API ────────────────────────────────────────────────────
  stats = {
    envois:      0,
    retraits:    0,
    entrants:    '0',
    sortants:    '0',
    theoretique: 0,
  };

  // ── Form ────────────────────────────────────────────────────────────────────
  realBalance: number | null = null;
  gap: number | null = null;
  note = '';

  // ── Agent email (real from session, fallback to mock) ───────────────────────
  private get agentEmail(): string {
    return this.auth.currentUser?.email ?? 'agent.test@okanetransfer.com';
  }

  ngOnInit(): void {
    this.loadSoldeTheorique();
  }

  private loadSoldeTheorique(): void {
    this.loading = true;
    this.caisse.operationsDuJour(this.agentEmail).subscribe({
      next: (ops) => {
        const envois   = ops.filter(o => o.type === 'ENVOI');
        const retraits = ops.filter(o => o.type === 'RETRAIT');
        const totalIn  = envois.reduce((s, o) => s + o.montant, 0);
        const totalOut = retraits.reduce((s, o) => s + o.montant, 0);

        this.stats = {
          envois:      envois.length,
          retraits:    retraits.length,
          entrants:    totalIn.toLocaleString('fr-MA'),
          sortants:    totalOut.toLocaleString('fr-MA'),
          theoretique: 0, // will be set from solde-jour
        };

        // Load the actual theoretical balance computed by the backend
        this.caisse.consulterSoldeDuJour(this.agentEmail).subscribe({
          next: (solde) => {
            // Round to 2dp so comparison with agent-entered value is exact
            this.stats = { ...this.stats, theoretique: Math.round(Number(solde) * 100) / 100 };
            this.loading = false;
          },
          error: () => {
            this.stats = { ...this.stats, theoretique: Math.round((totalIn - totalOut) * 100) / 100 };
            this.loading = false;
          },
        });
      },
      error: () => {
        this.loading = false;
        this.errorMsg = 'Impossible de charger les données de la journée.';
      },
    });
  }

  computeGap(): void {
    const rb = Number(this.realBalance);
    if (this.realBalance === null || this.realBalance === undefined || isNaN(rb)) {
      this.gap = null;
      return;
    }
    // Round to 2 decimal places to avoid floating-point noise
    this.gap = Math.round((rb - this.stats.theoretique) * 100) / 100;
  }

  /** True when gap is exactly 0 (within 1 centime tolerance) */
  get hasNoGap(): boolean {
    return this.gap !== null && Math.abs(this.gap) < 0.01;
  }

  get canValidate(): boolean {
    if (this.loading || this.submitting) return false;
    if (this.gap === null) return false;
    if (!this.hasNoGap && !this.note.trim()) return false;
    return true;
  }

  cancel(): void {
    this.router.navigate(['/agent/caisse']);
  }

  validate(): void {
    if (!this.canValidate) return;

    this.submitting = true;
    this.errorMsg   = null;

    const today = new Date();
    const dateStr = today.toISOString().split('T')[0]; // yyyy-MM-dd

    this.caisse.saveCloture({
      agentEmail:  this.agentEmail,
      date:        dateStr,
      soldeSaisi:  Number(this.realBalance),
    }).subscribe({
      next: () => {
        this.submitting = false;
        this.router.navigate(['/agent/dashboard']);
      },
      error: (err) => {
        this.submitting = false;
        const msg = err?.error?.message ?? err?.error ?? null;
        if (typeof msg === 'string' && msg.includes('existe déjà')) {
          this.errorMsg = 'Une clôture a déjà été enregistrée pour aujourd\'hui.';
        } else {
          this.errorMsg = 'Erreur lors de la clôture. Veuillez réessayer.';
        }
      },
    });
  }
}

