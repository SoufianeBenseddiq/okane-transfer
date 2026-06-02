import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { forkJoin } from 'rxjs';
import { catchError, map, of } from 'rxjs';
import { AgenceService } from '../../../core/services/agence.service';
import { UserService } from '../../../core/services/user.service';
import { AgenceResponse } from '../../../core/models/agence';
import { UserResponse } from '../../../core/models/user/user-response.model';
import { IconComponent } from '../../../shared/components/icon/icon.component';
import { environment } from '../../../../environments/environment';
import { HttpClient, HttpParams } from '@angular/common/http';

@Component({
  selector: 'app-caisse',
  standalone: true,
  imports: [CommonModule, FormsModule, IconComponent],
  templateUrl: './caisse.component.html',
})
export class CaisseComponent implements OnInit {
  agence: AgenceResponse | null = null;
  agents: UserResponse[] = [];
  loading = true;
  error: string | null = null;

  // Ouverture state per agent
  openingFor: number | null = null;  // agentId being opened
  floatInput: Record<number, number> = {};
  saving: number | null = null;
  openError: string | null = null;

  private readonly caisseBase = `${environment.apiUrl}/api/caisse-operations`;

  constructor(
    private agenceService: AgenceService,
    private userService: UserService,
    private http: HttpClient,
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    forkJoin({
      agence: this.agenceService.getMonAgence().pipe(catchError(() => of(null))),
      agents: this.userService.findAll('ROLE_AGENT' as any).pipe(catchError(() => of([]))),
    }).subscribe({
      next: ({ agence, agents }) => {
        this.agence = agence;
        // Only show agents belonging to this manager's agence
        this.agents = agence ? agents.filter(a => a.agenceId === agence.id) : agents;
        this.loading = false;
      },
      error: () => { this.loading = false; },
    });
  }

  get poolDisponible(): number {
    return this.agence?.soldeCaisseAgence ?? 0;
  }

  isCaisseOuverte(agent: UserResponse): boolean {
    return (agent.soldeCaisse ?? 0) > 0;
  }

  soldeCaisseLabel(agent: UserResponse): string {
    const s = agent.soldeCaisse ?? 0;
    return s.toLocaleString('fr-FR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
  }

  openOuverture(agent: UserResponse): void {
    this.openingFor = agent.id;
    this.floatInput[agent.id] = 0;
    this.openError = null;
  }

  cancelOuverture(): void {
    this.openingFor = null;
    this.openError = null;
  }

  confirmerOuverture(agent: UserResponse): void {
    const montant = this.floatInput[agent.id] ?? 0;
    if (montant <= 0) { this.openError = 'Le montant doit être supérieur à 0.'; return; }
    if (montant > this.poolDisponible) {
      this.openError = `Fonds insuffisants. Disponible : ${this.poolDisponible.toLocaleString('fr-FR')} MAD`;
      return;
    }

    this.saving = agent.id;
    this.openError = null;
    const params = new HttpParams()
      .set('agentEmail', agent.email ?? '')
      .set('montantInitial', montant.toString());

    this.http.post(`${this.caisseBase}/ouvrir`, {}, { params }).subscribe({
      next: () => {
        this.saving = null;
        this.openingFor = null;
        // Refresh data to show updated soldeCaisseAgence and agent soldeCaisse
        this.load();
      },
      error: (err) => {
        this.openError = err?.error?.message ?? 'Erreur lors de l\'ouverture de la caisse.';
        this.saving = null;
      },
    });
  }

  formatMontant(n: number): string {
    return n.toLocaleString('fr-FR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
  }

  get totalSoldeAgents(): number {
    return this.agents.reduce((s, a) => s + (a.soldeCaisse ?? 0), 0);
  }
}
