import { Component, OnInit, OnDestroy, ChangeDetectorRef, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';
import { catchError, map } from 'rxjs';

interface CaisseTransaction {
  id: string;
  type: 'in' | 'out';
  amount: number;
  description: string;
  time: string;
}

@Component({
  selector: 'app-caisse',
  standalone: true,
  imports: [CommonModule, TranslatePipe],
  templateUrl: './caisse.component.html',
  styleUrl: './caisse.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CaisseComponent implements OnInit, OnDestroy {
  openingBalance = 0;
  currentBalance = 0;
  transactions: CaisseTransaction[] = [];
  loading = true;
  error: string | null = null;
  private apiUrl = `${environment.apiUrl}/api`;
  private pollingIntervalId: any;

  constructor(
    private http: HttpClient,
    private changeDetectorRef: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.loadCaisseData();
    // Polling toutes les 5 secondes pour synchroniser les transactions
    this.pollingIntervalId = setInterval(() => {
      this.loadCaisseData();
    }, 5000);
  }

  ngOnDestroy() {
    if (this.pollingIntervalId) {
      clearInterval(this.pollingIntervalId);
    }
  }

  loadCaisseData() {
    this.loading = true;
    this.error = null;

    // Récupérer les transferts pour afficher les transactions
    this.http.get<any[]>(`${this.apiUrl}/transferts`)
      .pipe(
        map(transferts => {
          console.log('📡 API TRANSFERTS REÇUS:', transferts?.length || 0, 'transferts');
          console.log('🕐 Heure:', new Date().toLocaleTimeString());
          
          if (transferts && transferts.length > 0) {
            // Transformer les transferts en transactions
            this.transactions = [...transferts
              .sort((a, b) => new Date(b.creeLe).getTime() - new Date(a.creeLe).getTime())
              .map(t => ({
                id: t.codeRetrait || `TRF-${t.numeroReference}`,
                type: t.statut === 'PAYE' ? 'out' : 'in',
                amount: t.montantEnvoye || 0,
                description: `Transfert - ${t.statut}`,
                time: new Date(t.creeLe).toLocaleTimeString('fr-FR', { 
                  hour: '2-digit', 
                  minute: '2-digit' 
                })
              })) as CaisseTransaction[]];

            console.log('✅ TRANSACTIONS AFFICHÉES:', this.transactions.length);
            this.transactions.forEach(tx => {
              console.log(`   - ${tx.id}: ${tx.description} → ${tx.amount} MAD`);
            });

            // Calculer les soldes
            const transfertsPayes = transferts.filter(t => t.statut === 'PAYE');
            const totalMontants = transfertsPayes.reduce((s, t) => s + (t.montantRecu || 0), 0);
            const totalFrais = transfertsPayes.reduce((s, t) => s + (t.frais || 0), 0);

            this.openingBalance = totalMontants + totalFrais;
            this.currentBalance = totalMontants;
            
            console.log(`💰 SOLDES: Ouverture=${this.openingBalance}, Actuel=${this.currentBalance}`);
          } else {
            console.log('❌ AUCUN TRANSFERT REÇU');
            this.transactions = [];
            this.openingBalance = 0;
            this.currentBalance = 0;
          }
          this.loading = false;
          this.changeDetectorRef.markForCheck();
        }),
        catchError(err => {
          console.error('❌ ERREUR CHARGEMENT TRANSFERTS:', err);
          this.error = 'Les données de caisse ne sont pas disponibles. Vérifiez que le backend fonctionne correctement.';
          this.loading = false;
          this.transactions = [];
          this.changeDetectorRef.markForCheck();
          return [];
        })
      )
      .subscribe();
  }
}
