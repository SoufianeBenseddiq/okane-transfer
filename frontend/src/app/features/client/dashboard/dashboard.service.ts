import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { delay } from 'rxjs/operators';
import {
  DashboardData,
  Transfer,
  DashboardStats,
  Beneficiary,
} from './dashboard.models';

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

@Injectable({
  providedIn: 'root',
})
export class DashboardService {
  private apiBase = '/api/v1';

  constructor(private http: HttpClient) {}

  /**
   * Fetches the full dashboard data for the authenticated user.
   * Replace the mock return with the real HTTP call when the API is ready.
   */
  getDashboardData(): Observable<DashboardData> {
    // --- REAL CALL (uncomment when API is ready) ---
    // return this.http.get<DashboardData>(`${this.apiBase}/dashboard`);

    return of(MOCK_DASHBOARD_DATA).pipe(delay(300));
  }

  /**
   * Fetches the active in-progress transfer for the authenticated user.
   */
  getActiveTransfer(): Observable<Transfer | null> {
    // return this.http.get<Transfer | null>(`${this.apiBase}/transfers/active`);
    return of(MOCK_ACTIVE_TRANSFER).pipe(delay(200));
  }

  /**
   * Fetches the last N transfers for the authenticated user.
   */
  getRecentTransfers(limit = 4): Observable<Transfer[]> {
    // return this.http.get<Transfer[]>(`${this.apiBase}/transfers/recent?limit=${limit}`);
    return of(MOCK_RECENT_TRANSFERS.slice(0, limit)).pipe(delay(200));
  }

  /**
   * Fetches dashboard statistics (sent this month, beneficiaries, etc.).
   */
  getDashboardStats(): Observable<DashboardStats> {
    // return this.http.get<DashboardStats>(`${this.apiBase}/dashboard/stats`);
    return of(MOCK_STATS).pipe(delay(200));
  }

  /**
   * Reveals the full withdrawal code for a given transfer.
   */
  revealWithdrawalCode(transferId: string): Observable<string> {
    // return this.http.get<string>(`${this.apiBase}/transfers/${transferId}/withdrawal-code`);
    return of('K7AB-12QA').pipe(delay(500));
  }
}