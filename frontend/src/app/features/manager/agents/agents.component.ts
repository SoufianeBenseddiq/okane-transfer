import { Component, OnInit, OnDestroy, ChangeDetectorRef, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';
import { AgentsService, Agent } from './agents.service';
import { AgenceService } from '../../../core/services/agence.service';
import { catchError, forkJoin, of } from 'rxjs';

interface AgentDisplay extends Agent {
  transferts: number;
  commissionsJour: number;
  performance: number;
  status: 'active' | 'inactive';
}

@Component({
  selector: 'app-agents',
  standalone: true,
  imports: [CommonModule, TranslatePipe],
  templateUrl: './agents.component.html',
  styleUrl: './agents.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AgentsComponent implements OnInit, OnDestroy {
  agents: AgentDisplay[] = [];
  topAgent: AgentDisplay | null = null;
  otherAgents: AgentDisplay[] = [];
  loading = true;
  error: string | null = null;
  totalAgentsTransferts = 0;
  private pollingIntervalId: any;

  private agenceId: number | null = null;

  constructor(
    private agentsService: AgentsService,
    private agenceService: AgenceService,
    private changeDetectorRef: ChangeDetectorRef
  ) {}

  ngOnInit() {
    // Load manager's agence first to know which agents belong to it
    this.agenceService.getMonAgence().pipe(catchError(() => of(null))).subscribe(agence => {
      this.agenceId = agence?.id ?? null;
      this.loadAgents();
      this.pollingIntervalId = setInterval(() => this.loadAgents(), 5000);
    });
  }

  ngOnDestroy() {
    if (this.pollingIntervalId) {
      clearInterval(this.pollingIntervalId);
    }
  }

  loadAgents() {
    this.loading = true;
    this.error = null;

    forkJoin({
      agents: this.agentsService.getAgents().pipe(
        catchError(err => {
          console.error('Erreur chargement agents:', err);
          return of([]);
        })
      ),
      transferts: this.agentsService.getAgentsPerformance().pipe(
        catchError(err => {
          console.error('Erreur chargement transferts:', err);
          return of([]);
        })
      ),
      caisseOperations: this.agentsService.getCaisseOperations().pipe(
        catchError(err => {
          console.error('Erreur chargement opérations de caisse:', err);
          return of([]);
        })
      )
    }).subscribe(({ agents, transferts, caisseOperations }) => {

      // Filter to ROLE_AGENT only AND belonging to this manager's agence
      const agentsOnly = (agents as Agent[]).filter(a =>
        a.role === 'ROLE_AGENT' && (this.agenceId == null || a.agenceId === this.agenceId)
      );

      const today = new Date().toISOString().slice(0, 10); // 'YYYY-MM-DD'
      const isToday = (dateHeure?: string) => !!dateHeure && dateHeure.slice(0, 10) === today;

      // Transferts envoyés (créés) aujourd'hui
      const envoisAujourdhui = (transferts as any[]).filter(t => isToday(t.creeLe));

      // Retraits (paiements) effectués aujourd'hui
      const retraitsAujourdhui = (caisseOperations as any[]).filter(
        op => op.type === 'RETRAIT' && isToday(op.dateHeure)
      );

      // Transformer les agents avec les stats du jour
      this.agents = agentsOnly.map(agent => {
        const envois = envoisAujourdhui.filter(t => t.agentId === agent.id);
        const retraits = retraitsAujourdhui.filter(op => op.agentId === agent.id);

        const commissionsJour = envois
          .filter(t => t.statut === 'PAYE')
          .reduce((sum, t) => sum + (t.frais || 0), 0);

        return {
          ...agent,
          transferts: envois.length + retraits.length,  // ✅ envois créés + retraits payés, aujourd'hui
          commissionsJour,
          performance: 0, // calculée ci-dessous, une fois le total connu
          status: agent.actif ? 'active' : 'inactive'
        } as AgentDisplay;
      });

      // Calculer le total des transferts (envois + retraits) de tous les agents, aujourd'hui
      this.totalAgentsTransferts = this.agents.reduce((sum, a) => sum + a.transferts, 0);

      // Performance = part de l'activité du jour (envois + retraits) gérée par cet agent
      this.agents.forEach(a => {
        a.performance = this.totalAgentsTransferts > 0
          ? Math.round((a.transferts / this.totalAgentsTransferts) * 100)
          : 0;
      });

      // Identifier le meilleur agent (le plus de transferts, envois + retraits)
      if (this.agents.length > 0) {
        this.topAgent = this.agents.reduce((best, current) =>
          (current.transferts > best.transferts) ? current : best
        );
        this.otherAgents = this.agents.filter(a => a.id !== this.topAgent?.id);
      } else {
        this.topAgent = null;
        this.otherAgents = [];
      }

      this.loading = false;
      this.changeDetectorRef.markForCheck();
    });
  }

  toggleAgentStatus(agent: AgentDisplay) {
    const newStatus = agent.status === 'active' ? 'inactive' : 'active';

    if (newStatus === 'inactive') {
      this.agentsService.deactivateAgent(agent.id)
        .subscribe(
          () => {
            agent.actif = false;
            agent.status = 'inactive';
          },
          err => {
            console.error('Erreur désactivation agent:', err);
            this.error = 'Impossible de désactiver l\'agent';
          }
        );
    } else {
      this.agentsService.reactivateAgent(agent.id)
        .subscribe(
          () => {
            agent.actif = true;
            agent.status = 'active';
          },
          err => {
            console.error('Erreur réactivation agent:', err);
            this.error = 'Impossible de réactiver l\'agent';
          }
        );
    }
  }
}