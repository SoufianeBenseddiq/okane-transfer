import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, forkJoin } from 'rxjs';
import { delay, map, switchMap } from 'rxjs/operators';
import {
  DashboardData,
  Transfer,
  DashboardStats,
  Beneficiary,
  TransferStatus,
} from './dashboard.models';

// ────────────────────────────────────────────────────────────────────────────
// MOCK DATA - Comment out when APIs are ready
// ────────────────────────────────────────────────────────────────────────────
/*
const MOCK_BENEFICIARIES: Beneficiary[] = [
  { id: '1', initials: 'AD', name: 'Aminata Diallo', country: 'Sénégal' },
  { id: '2', initials: 'KT', name: 'Kadiatou Touré', country: "Côte d'Ivoire" },
  { id: '3', initials: 'OS', name: 'Omar Sané', country: 'Sénégal' },
  { id: '4', initials: 'FB', name: 'Fatima Belakacem', country: 'France' },
];

const MOCK_ACTIVE_TRANSFER: Transfer = {
  id: 'tr-001',
  reference: '094821',
  recipientName: 'Aminata Diallo',
  amount: 2000,
  currency: 'MAD',
  convertedAmount: 215400,
  convertedCurrency: 'XOF',
  destinationCountry: 'Sénégal',
  destinationFlag: '🇸🇳',
  status: 'to_withdraw',
  createdAt: new Date(),
  withdrawalCode: 'K7••-••QA',
  steps: [
    { label: 'Créé', time: '09:14', completed: true, current: false },
    { label: 'Validé', time: '09:15', completed: true, current: false },
    { label: 'À retirer', time: 'en cours', completed: false, current: true },
    { label: 'Payé', time: '', completed: false, current: false },
  ],
};

const MOCK_RECENT_TRANSFERS: Transfer[] = [
  {
    id: 'tr-001',
    reference: '094821',
    recipientName: 'Aminata Diallo',
    amount: 2000,
    currency: 'MAD',
    convertedAmount: 215400,
    convertedCurrency: 'XOF',
    destinationCountry: 'Sénégal',
    destinationFlag: '🇸🇳',
    status: 'pending',
    createdAt: new Date("2026-06-14T09:14:00"),
    steps: [],
  },
  {
    id: 'tr-002',
    reference: '094215',
    recipientName: 'Kadiatou Touré',
    amount: 1500,
    currency: 'MAD',
    convertedAmount: 161600,
    convertedCurrency: 'XOF',
    destinationCountry: "Côte d'Ivoire",
    destinationFlag: '🇨🇮',
    status: 'paid',
    createdAt: new Date("2026-05-18T14:22:00"),
    steps: [],
  },
  {
    id: 'tr-003',
    reference: '093888',
    recipientName: 'Omar Sané',
    amount: 900,
    currency: 'MAD',
    convertedAmount: 96900,
    convertedCurrency: 'XOF',
    destinationCountry: 'Sénégal',
    destinationFlag: '🇸🇳',
    status: 'paid',
    createdAt: new Date("2026-05-11T11:05:00"),
    steps: [],
  },
  {
    id: 'tr-004',
    reference: '093182',
    recipientName: 'Fatima Belakacem',
    amount: 2000,
    currency: 'MAD',
    convertedAmount: 184,
    convertedCurrency: 'EUR',
    destinationCountry: 'France',
    destinationFlag: '🇫🇷',
    status: 'expired',
    createdAt: new Date("2026-05-02T17:48:00"),
    steps: [],
  },
];

const MOCK_STATS: DashboardStats = {
  sentThisMonth: 6400,
  currency: 'MAD',
  changeVsLastMonth: 2200,
  activeBeneficiariesCount: 4,
  beneficiaries: MOCK_BENEFICIARIES,
};

const MOCK_DASHBOARD_DATA: DashboardData = {
  userName: 'Mohamed',
  activeTransfer: MOCK_ACTIVE_TRANSFER,
  stats: MOCK_STATS,
  recentTransfers: MOCK_RECENT_TRANSFERS,
};
*/

@Injectable({
  providedIn: 'root',
})
export class DashboardService {
  private apiBase = 'http://localhost:8080/okane_transfer_1_0_SNAPSHOT_war/api';

  constructor(private http: HttpClient) {}

  /**
   * Fetches the full dashboard data for the authenticated user.
   * Combines multiple API calls:
   * - GET /api/utilisateurs/me → user info (nom, prenom)
   * - GET /api/transferts/mes-transferts?clientId={id} → transfers list
   * - GET /api/beneficiaires → beneficiaries list
   * 
   * Backend needs a dedicated endpoint:
   * GET /api/client/dashboard
   * Response: { userName, activeTransfer, stats, recentTransfers }
   */
  getDashboardData(): Observable<DashboardData> {
    // TODO: Create backend endpoint GET /api/client/dashboard
    // Should aggregate:
    // - Current user name from Authentication context
    // - Active transfer (statut = EN_ATTENTE)
    // - Monthly stats (sum montantEnvoye WHERE creeLe >= startOfMonth)
    // - Recent 4 transfers ordered by creeLe DESC
    // - Unique beneficiaries count
    
    // Temporary: combine existing APIs
    return forkJoin({
      user: this.http.get<any>(`${this.apiBase}/utilisateurs/me`),
      transfers: this.getRecentTransfers(4),
      stats: this.getDashboardStats()
    }).pipe(
      map(({ user, transfers, stats }) => ({
        userName: user.prenom || 'Client',
        activeTransfer: transfers.find(t => t.status === 'pending' || t.status === 'to_withdraw') || null,
        stats,
        recentTransfers: transfers
      }))
    );

    // return of(MOCK_DASHBOARD_DATA).pipe(delay(300));
  }

  /**
   * Fetches the active in-progress transfer for the authenticated user.
   * Uses existing: GET /api/transferts/mes-transferts?clientId={id}
   * Filters for statut = EN_ATTENTE
   */
  getActiveTransfer(): Observable<Transfer | null> {
    // First get current user to get clientId
    return this.http.get<any>(`${this.apiBase}/utilisateurs/me`).pipe(
      switchMap(user => 
        this.http.get<any[]>(`${this.apiBase}/transferts/mes-transferts?clientId=${user.id}`)
      ),
      map(transfers => {
        const active = transfers.find(t => t.statut === 'EN_ATTENTE');
        return active ? this.mapTransfertToTransfer(active) : null;
      })
    );

    // return of(MOCK_ACTIVE_TRANSFER).pipe(delay(200));
  }

  /**
   * Fetches the last N transfers for the authenticated user.
   * Uses: GET /api/transferts/mes-transferts?clientId={id}
   * Takes first N results
   */
  getRecentTransfers(limit = 4): Observable<Transfer[]> {
    return this.http.get<any>(`${this.apiBase}/utilisateurs/me`).pipe(
      switchMap(user => 
        this.http.get<any[]>(`${this.apiBase}/transferts/mes-transferts?clientId=${user.id}`)
      ),
      map(transfers => 
        transfers.slice(0, limit).map(t => this.mapTransfertToTransfer(t))
      )
    );

    // return of(MOCK_RECENT_TRANSFERS.slice(0, limit)).pipe(delay(200));
  }

  /**
   * Fetches dashboard statistics (sent this month, beneficiaries, etc.).
   * 
   * Backend needs new endpoint:
   * GET /api/client/stats
   * Response: {
   *   sentThisMonth: BigDecimal,
   *   currency: String,
   *   changeVsLastMonth: BigDecimal,
   *   activeBeneficiariesCount: Integer,
   *   beneficiaries: List<BeneficiaireResponse>
   * }
   * 
   * Logic:
   * - Sum transfert.montantEnvoye WHERE expediteur.client.id = currentUser.id 
   *   AND transfert.creeLe >= first day of current month
   * - Compare to same sum for previous month
   * - Count distinct beneficiaires from user's transfers
   */
  getDashboardStats(): Observable<DashboardStats> {
    // TODO: Create backend endpoint GET /api/client/stats
    // For now return empty structure
    return this.http.get<any>(`${this.apiBase}/transferts/stats`);
    
    // return of(MOCK_STATS).pipe(delay(200));
  }

  /**
   * Reveals the full withdrawal code for a given transfer.
   * Uses: GET /api/transferts/{id}
   * Returns the codeRetrait field
   */
  revealWithdrawalCode(transferId: string): Observable<string> {
    return this.http.get<any>(`${this.apiBase}/transferts/${transferId}`).pipe(
      map(transfer => transfer.codeRetrait)
    );

    // return of('K7AB-12QA').pipe(delay(500));
  }

  /**
   * Maps backend TransfertResponse to frontend Transfer model
   */
  private mapTransfertToTransfer(t: any): Transfer {
    return {
      id: t.id?.toString() || '',
      reference: t.numeroReference || '',
      recipientName: t.nomBeneficiaire || '',
      amount: t.montantEnvoye || 0,
      currency: 'MAD', // TODO: get from t.deviseSource when available
      convertedAmount: t.montantRecu || 0,
      convertedCurrency: 'XOF', // TODO: get from t.deviseDestination
      destinationCountry: t.paysDestination || '',
      destinationFlag: this.getCountryFlag(t.paysDestination),
      status: this.mapStatut(t.statut),
      createdAt: new Date(t.creeLe),
      withdrawalCode: this.maskCode(t.codeRetrait),
      steps: this.generateSteps(t.statut, t.creeLe, t.payeLe)
    };
  }

  private mapStatut(statut: string): TransferStatus {
    const map: Record<string, TransferStatus> = {
      'EN_ATTENTE': 'to_withdraw',
      'PAYE': 'paid',
      'ANNULE': 'expired',
      'EXPIRE': 'expired',
      'BLOQUE': 'expired'
    };
    return map[statut] || 'pending';
  }

  private maskCode(code: string): string {
    if (!code || code.length < 4) return code;
    return code.substring(0, 2) + '••-••' + code.substring(code.length - 2);
  }

  private getCountryFlag(country: string): string {
    const flags: Record<string, string> = {
      'Sénégal': '🇸🇳',
      'Senegal': '🇸🇳',
      'France': '🇫🇷',
      'Côte d\'Ivoire': '🇨🇮',
      'Mali': '🇲🇱',
      'Maroc': '🇲🇦'
    };
    return flags[country] || '🌍';
  }

  private generateSteps(statut: string, creeLe: string, payeLe?: string): any[] {
    const created = new Date(creeLe);
    const paid = payeLe ? new Date(payeLe) : null;
    
    return [
      { 
        label: 'Créé', 
        time: created.toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' }), 
        completed: true, 
        current: false 
      },
      { 
        label: 'Validé', 
        time: created.toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' }), 
        completed: statut !== 'EN_ATTENTE', 
        current: statut === 'EN_ATTENTE' 
      },
      { 
        label: 'À retirer', 
        time: statut === 'PAYE' ? '' : 'en cours', 
        completed: statut === 'PAYE', 
        current: false 
      },
      { 
        label: 'Payé', 
        time: paid ? paid.toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' }) : '', 
        completed: statut === 'PAYE', 
        current: false 
      },
    ];
  }
}