import { Component, OnInit, OnDestroy, ChangeDetectorRef, ChangeDetectionStrategy, NgZone } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';
import { DashboardService, StatCard, Transfer, AgentPerformance } from './dashboard.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, TranslatePipe],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DashboardComponent implements OnInit, OnDestroy {
  stats: StatCard[] = [];
  agentPerformance: AgentPerformance[] = [];
  pendingValidations: Transfer[] = [];
  currentDate = new Date();

  dailyLimit = 200000;
  dailyUsed = 0;
  dailyUsagePercent = 0;
  maxAgentCount = 1;
  totalTransferts = 0;
  
  private performanceIntervalId: any;

  constructor(
    private dashboardService: DashboardService,
    private changeDetectorRef: ChangeDetectorRef,
    private ngZone: NgZone
  ) {}

  ngOnInit() {
    this.loadDashboardData();
    // Polling toutes les 5 secondes pour METTRE À JOUR LES BARRES en temps réel
    this.performanceIntervalId = setInterval(() => {
      this.loadAgentPerformance();
    }, 5000);
  }

  ngOnDestroy() {
    if (this.performanceIntervalId) {
      clearInterval(this.performanceIntervalId);
    }
  }

  loadDashboardData() {
    this.loadDashboardStats();
    this.loadAgentPerformance();
    this.loadPendingValidations();
  }

  private loadDashboardStats() {
    this.dashboardService.getDashboardStats().subscribe({
      next: (stats) => {
        this.ngZone.run(() => {
          // Créer une nouvelle instance pour forcer le changement
          this.stats = [...stats];

          // Calculer dailyUsed depuis la stat "Plafond restant"
          const plafondStat = stats.find(s => s.label.includes('PLAFOND'));
          if (plafondStat && plafondStat.progress !== undefined) {
            this.dailyUsagePercent = Math.round(plafondStat.progress);
            this.dailyUsed = Math.round((plafondStat.progress / 100) * this.dailyLimit);
          }

          this.changeDetectorRef.markForCheck();
        });
      },
      error: (err) => console.error('❌ Erreur stats:', err)
    });
  }

  private loadAgentPerformance() {
    this.dashboardService.getAgentPerformance().subscribe({
      next: (performance) => {
        // Utiliser NgZone.run() pour forcer Angular à redessiner le DOM
        this.ngZone.run(() => {
          // Créer une NOUVELLE instance du tableau pour que OnPush détecte le changement
          this.agentPerformance = [...performance];
          this.maxAgentCount = Math.max(...performance.map(a => a.count), 1);
          this.totalTransferts = performance.reduce((sum, agent) => sum + agent.count, 0);
          
          // Afficher les vraies données des agents
          console.log('\n📊 PERFORMANCE DES AGENTS (DASHBOARD):');
          console.log(`� TOTAL TRANSFERTS: ${this.totalTransferts}`);
          performance.forEach(agent => {
            console.log(`\n👤 ${agent.prenom} ${agent.nom}:`);
            console.log(`   Transferts (count): ${agent.count}`);
            console.log(`   % du total: ${(agent.count / this.totalTransferts) * 100}%`);
            console.log(`   Commissions: ${(agent.commissionsGenerees || 0).toFixed(2)} MAD`);
            console.log(`   Taux de réussite: ${agent.tauxReussite}%`);
          });
          
          this.changeDetectorRef.markForCheck();
        });
      },
      error: (err) => console.error('❌ Erreur performance:', err)
    });
  }

  private loadPendingValidations() {
    this.dashboardService.getPendingValidations().subscribe({
      next: (validations) => {
        this.ngZone.run(() => {
          // Créer une nouvelle instance pour forcer le changement
          this.pendingValidations = [...validations];
          this.changeDetectorRef.markForCheck();
        });
      },
      error: (err) => console.error('❌ Erreur validations:', err)
    });
  }

  exportReport() {
    console.log('Export report clicked');
  }

  approvePending(transfer: Transfer) {
    console.log('Approving:', transfer.id);
    
    // Extraire l'ID de la déclaration depuis transfer.sender qui contient "Déclaration #15001"
    const declarationId = parseInt(transfer.sender.replace('Déclaration #', ''), 10);
    
    this.dashboardService.approveValidation(declarationId).subscribe({
      next: () => {
        console.log('✅ Validation approuvée');
        // Recharger la liste des validations
        this.reloadValidations();
      },
      error: (err) => {
        console.error('❌ Erreur approbation:', err);
      }
    });
  }

  rejectPending(transfer: Transfer) {
    console.log('Rejecting:', transfer.id);
    
    // Extraire l'ID de la déclaration
    const declarationId = parseInt(transfer.sender.replace('Déclaration #', ''), 10);
    
    this.dashboardService.rejectValidation(declarationId).subscribe({
      next: () => {
        console.log('✅ Validation rejetée');
        // Recharger la liste des validations
        this.reloadValidations();
      },
      error: (err) => {
        console.error('❌ Erreur rejet:', err);
      }
    });
  }

  private reloadValidations() {
    this.dashboardService.getPendingValidations().subscribe({
      next: (validations) => {
        this.ngZone.run(() => {
          this.pendingValidations = [...validations];
          this.changeDetectorRef.markForCheck();
        });
      },
      error: (err) => console.error('❌ Erreur rechargement validations:', err)
    });
  }

  // TrackBy pour ngFor - aide Angular à détecter les changements
  trackByAgentId(index: number, agent: AgentPerformance): number {
    return agent.agentId;
  }

  trackByStatId(index: number, stat: StatCard): string {
    return stat.label;
  }

  trackByTransferId(index: number, transfer: Transfer): string {
    return transfer.id;
  }
}