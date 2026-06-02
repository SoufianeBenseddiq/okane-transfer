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
      )
    }).subscribe(({ agents, transferts }) => {

      // Filter to ROLE_AGENT only AND belonging to this manager's agence
      const agentsOnly = (agents as Agent[]).filter(a =>
        a.role === 'ROLE_AGENT' && (this.agenceId == null || a.agenceId === this.agenceId)
      );

      // Calculer les stats par agent
      const statsParAgent = new Map<number, any>();

      if (transferts && (transferts as any[]).length > 0) {
        (transferts as any[]).forEach(t => {
          const agentId = t.agentId;
          if (agentId) {
            if (!statsParAgent.has(agentId)) {
              statsParAgent.set(agentId, {
                transferts: 0,
                commissions: 0,
                payees: 0,
                total: 0
              });
            }
            const stats = statsParAgent.get(agentId)!;
            stats.total++;
            if (t.statut === 'PAYE') {
              stats.transferts++;
              stats.commissions += t.frais || 0;
              stats.payees++;
            }
          }
        });
      }

      // Calculer les totaux globaux
      const totalPayees = Array.from(statsParAgent.values()).reduce((sum, s) => sum + s.payees, 0);

      // Transformer les agents avec les stats
      this.agents = [...agentsOnly.map(agent => {
        const stats = statsParAgent.get(agent.id);
        return {
          ...agent,
          transferts: stats?.total || 0,  // ✅ TOUS les transferts (pas seulement PAYÉS)
          commissionsJour: stats?.commissions || 0,
          performance: totalPayees > 0 ? Math.round((stats?.payees || 0) / totalPayees * 100) : 0,  // ✅ % du total global PAYÉS
          status: agent.actif ? 'active' : 'inactive'
        } as AgentDisplay;
      })];

      // Calculer le total des transferts de tous les agents
      this.totalAgentsTransferts = this.agents.reduce((sum, a) => sum + a.transferts, 0);

      // Identifier le meilleur agent (plus de commissions)
      if (this.agents.length > 0) {
        this.topAgent = this.agents.reduce((best, current) =>
          (current.commissionsJour > best.commissionsJour) ? current : best
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