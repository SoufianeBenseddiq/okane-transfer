import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';
import { RouterLink } from '@angular/router';
import { CaisseService } from '../../../core/services/caisse.service';
import { CaisseOperationResponse } from '../../../core/models/caisse';
import { TypeOperation } from '../../../core/models/enums';

@Component({
  selector: 'app-caisse',
  standalone: true,
  imports: [CommonModule, TranslateModule, RouterLink],
  templateUrl: './caisse.component.html',
})
export class CaisseComponent implements OnInit {

  private readonly agentEmail = 'agent.test@okanetransfer.com';

  TypeOperation = TypeOperation;

  solde: number = 0;
  lastUpdate: string = '';
  openingBalance: number = 0;
  loading = true;
  error: string | null = null;
  soldeJour: number = 0;

  cashStats = {
    envoiOps: 0,
    retraitOps: 0,
    totalEnvois: 0,
    totalRetraits: 0,
  };

  operations: CaisseOperationResponse[] = [];

  // ── Pagination ─────────────────────────────────────────────────
  readonly pageSize = 6;
  currentPage = 1;

  get totalPages(): number {
    return Math.ceil(this.operations.length / this.pageSize);
  }

  get pages(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i + 1);
  }

  get pagedOperations(): CaisseOperationResponse[] {
    const start = (this.currentPage - 1) * this.pageSize;
    return this.operations.slice(start, start + this.pageSize);
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
    }
  }

  constructor(private caisseService: CaisseService) {}

  ngOnInit(): void {
    this.loadSoldeJour();
    this.loadOperations();
  }

  // private loadSolde(): void {
  //   this.caisseService.consulterSolde(this.agentEmail).subscribe({
  //     next: (s) => (this.solde = s),
  //     error: (e) => console.error('Erreur solde', e),
  //   });
  // }

  private loadSoldeJour(): void {
    this.caisseService.consulterSoldeDuJour(this.agentEmail).subscribe({
      next: (s) => (this.soldeJour = s),
      error: (e) => console.error('Erreur solde jour', e),
    });
  }

  private loadOperations(): void {
    this.loading = true;
    this.caisseService.operationsDuJour(this.agentEmail).subscribe({
      next: (ops) => {
        // tri décroissant : dernière opération en premier
        this.operations = [...ops].sort(
          (a, b) => new Date(b.dateHeure).getTime() - new Date(a.dateHeure).getTime()
        );
        this.cashStats      = this.computeStats(ops);
        this.lastUpdate     = new Date().toLocaleTimeString('fr-MA', { hour: '2-digit', minute: '2-digit' });
        const ouverture     = ops.find(o => o.type === TypeOperation.OUVERTURE);
        this.openingBalance = ouverture?.montant ?? 0;
        this.loading        = false;
      },
      error: (e) => {
        console.error('Erreur opérations', e);
        this.error   = 'Impossible de charger les opérations.';
        this.loading = false;
      },
    });
  }

  private computeStats(ops: CaisseOperationResponse[]) {
    const envois   = ops.filter(o => o.type === TypeOperation.ENVOI);
    const retraits = ops.filter(o => o.type === TypeOperation.RETRAIT);
    return {
      envoiOps:      envois.length,
      retraitOps:    retraits.length,
      totalEnvois:   envois.reduce((sum, o) => sum + o.montant, 0),
      totalRetraits: retraits.reduce((sum, o) => sum + o.montant, 0),
    };
  }

  formatMontant(n: number): string {
    return n.toLocaleString('fr-MA');
  }

  formatTime(dateHeure: string): string {
    return new Date(dateHeure).toLocaleTimeString('fr-MA', { hour: '2-digit', minute: '2-digit' });
  }

  getAmountDisplay(op: CaisseOperationResponse): string {
    const formatted = op.montant.toLocaleString('fr-MA');
    return op.type === TypeOperation.RETRAIT ? `-${formatted}` : `+${formatted}`;
  }

  getPageEnd(): number {
    return Math.min(this.currentPage * this.pageSize, this.operations.length);
  }
}
