import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, catchError, map, forkJoin, tap, throwError } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface StatCard {
  label: string;
  value: string;
  subtext?: string;
  trend?: string;
  trendDown?: boolean;
  progress?: number;
  status?: 'normal' | 'warning' | 'critical';
  icon?: string;
}

export interface Transfer {
  id: string;
  amount: number;
  sender: string;
  receiver: string;
  remark: string;
  agent?: string;
  status: 'pending' | 'approved' | 'rejected';
}

export interface AgentPerformance {
  agentId: number;
  nom: string;
  prenom: string;
  transfertsCount: number;
  montantTotal: number;
  commissionsGenerees: number;
  tauxReussite: number;
  count: number;
}

// ─── Interfaces calquées exactement sur les réponses Postman ─────────────────

interface Transfert {
  id: number;
  codeRetrait: string;
  numeroReference: string;
  montantEnvoye: number;
  montantRecu: number;
  frais: number;
  statut: 'EN_ATTENTE' | 'PAYE' | 'BLOQUE' | 'ANNULE' | string;
  creeLe: string;
  agentId: number;
  agenceEnvoiId: number;
  agenceRetraitId: number;
}

interface DeclarationSoupcon {
  id: number;
  transfert?: { id: number; montantEnvoye: number };
  regle?: { id: number; nom: string };
  motif: string;
  montantTotal: number;
  genereLe: string;
  traitee: boolean;
}

interface RegleAML {
  id: number;
  nom: string;
  description: string;
  seuilMontant: number;
  active: boolean;
}

interface Utilisateur {
  id: number;
  login: string;
  prenom: string;
  nom: string;
  role: string;
  actif: boolean;
}

interface Agence {
  id: number;
  nom: string;
  plafondJournalier: number;
}

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private readonly apiUrl = `${environment.apiUrl}/api`;

  constructor(private http: HttpClient) {}

  // ─── Stats cards ──────────────────────────────────────────────────────────

  getDashboardStats(): Observable<StatCard[]> {
    return forkJoin({
      transferts:   this.http.get<Transfert[]>(`${this.apiUrl}/transferts`).pipe(catchError(() => of([]))),
      declarations: this.http.get<DeclarationSoupcon[]>(`${this.apiUrl}/aml/declarations`).pipe(catchError(() => of([]))),
      regles:       this.http.get<RegleAML[]>(`${this.apiUrl}/aml/regles`).pipe(catchError(() => of([]))),
      utilisateurs: this.http.get<Utilisateur[]>(`${this.apiUrl}/utilisateurs`).pipe(catchError(() => of([]))),
      agences:      this.http.get<Agence[]>(`${this.apiUrl}/agences/actives`).pipe(catchError(() => of([]))),
    }).pipe(
      map(({ transferts, declarations, regles, utilisateurs, agences }) => {

        const now       = new Date();
        const debutMois = new Date(now.getFullYear(), now.getMonth(), 1);

        // ── Transferts ce mois ──
        const transfertsMois      = transferts.filter(t => new Date(t.creeLe) >= debutMois);
        const transfertsPayes     = transfertsMois.filter(t => t.statut === 'PAYE');
        const transfertsEnAttente = transfertsMois.filter(t => t.statut === 'EN_ATTENTE');
        const transfertsBloques   = transfertsMois.filter(t => t.statut === 'BLOQUE');

        const caMois    = transfertsPayes.reduce((s, t) => s + (t.montantEnvoye || 0), 0);
        const fraisMois = transfertsPayes.reduce((s, t) => s + (t.frais || 0), 0);

        const tauxSucces = transfertsMois.length > 0
          ? Math.round((transfertsPayes.length / transfertsMois.length) * 100)
          : 0;

        // ── Déclarations AML ──
        const declNonTraitees = declarations.filter(d => !d.traitee);
        const tauxTraitement  = declarations.length > 0
          ? Math.round(((declarations.length - declNonTraitees.length) / declarations.length) * 100)
          : 100;

        // ── Utilisateurs ──
        const agents  = utilisateurs.filter(u => u.role === 'ROLE_AGENT');
        const clients = utilisateurs.filter(u => u.role === 'ROLE_CLIENT');

        // ── Règles AML ──
        const reglesActives = regles.filter(r => r.active).length;

        return [
          {
            label: 'CA CE MOIS',
            value: this.formatMontant(caMois) + ' MAD',
            subtext: `${transfertsPayes.length} transferts payés`,
            trend: `Taux succès : ${tauxSucces}%`,
            trendDown: tauxSucces < 70,
            status: tauxSucces < 50 ? 'critical' : tauxSucces < 70 ? 'warning' : 'normal',
            icon: 'ti-cash'
          },
          {
            label: 'COMMISSIONS CE MOIS',
            value: this.formatMontant(fraisMois) + ' MAD',
            subtext: transfertsPayes.length > 0
              ? `≈ ${Math.round(fraisMois / transfertsPayes.length)} MAD / transfert`
              : 'Aucun transfert payé',
            status: 'normal',
            icon: 'ti-coin'
          },
          {
            label: 'TRANSFERTS EN ATTENTE',
            value: `${transfertsEnAttente.length}`,
            subtext: `${transfertsBloques.length} bloqué(s) · ${transfertsMois.length} ce mois`,
            status: transfertsBloques.length > 0 ? 'critical'
                  : transfertsEnAttente.length > 20 ? 'warning'
                  : 'normal',
            icon: 'ti-clock'
          },
          {
            label: 'ALERTES AML EN ATTENTE',
            value: `${declNonTraitees.length}`,
            subtext: `${tauxTraitement}% des déclarations traitées`,
            progress: tauxTraitement,
            status: declNonTraitees.length > 10 ? 'critical'
                  : declNonTraitees.length > 3  ? 'warning'
                  : 'normal',
            icon: 'ti-shield-exclamation'
          },
          {
            label: 'RÈGLES AML ACTIVES',
            value: `${reglesActives}`,
            subtext: `/ ${regles.length} règles configurées`,
            progress: regles.length > 0 ? Math.round((reglesActives / regles.length) * 100) : 0,
            status: reglesActives === 0 ? 'critical' : 'normal',
            icon: 'ti-adjustments'
          },
          {
            label: 'AGENTS & AGENCES',
            value: `${agents.length} agents`,
            subtext: `${agences.length} agences · ${clients.length} clients`,
            status: 'normal',
            icon: 'ti-users'
          }
        ] as StatCard[];
      })
    );
  }

  // ─── Validations AML en attente ───────────────────────────────────────────

  getPendingValidations(): Observable<Transfer[]> {
    return this.http.get<DeclarationSoupcon[]>(`${this.apiUrl}/aml/declarations`).pipe(
      map(declarations =>
        declarations
          .filter(d => !d.traitee)
          .slice(0, 5)
          .map(d => ({
            id: `TRF-${String(d.transfert?.id || d.id).padStart(7, '0')}`,
            amount: d.montantTotal || 0,
            sender: `Déclaration #${d.id}`,
            receiver: d.regle?.nom || `Règle #${d.regle?.id ?? '?'}`,
            remark: d.motif || 'Validation requise',
            status: 'pending' as const
          }))
      ),
      catchError(() => of([]))
    );
  }

  // ─── Performance agents ───────────────────────────────────────────────────

  getAgentPerformance(): Observable<AgentPerformance[]> {
    // Récupérer les transferts et les agents en parallèle
    return forkJoin({
      transferts: this.http.get<Transfert[]>(`${this.apiUrl}/transferts`).pipe(catchError(() => of([]))),
      agents: this.http.get<Utilisateur[]>(`${this.apiUrl}/utilisateurs?role=ROLE_AGENT`).pipe(catchError(() => of([])))
    }).pipe(
      map(({ transferts, agents }) => {
        // Calculer les stats par agent
        const statsParAgent = new Map<number, any>();

        if (transferts && transferts.length > 0) {
          transferts.forEach(t => {
            const agentId = t.agentId;
            
            if (agentId) {
              if (!statsParAgent.has(agentId)) {
                statsParAgent.set(agentId, {
                  transferts: 0,
                  frais: 0,
                  payees: 0,
                  total: 0
                });
              }
              const stats = statsParAgent.get(agentId)!;
              stats.total++;
              
              if (t.statut === 'PAYE') {
                stats.transferts++;
                stats.frais += t.frais || 0;
                stats.payees++;
              }
            }
          });
        }

        // Retourner les agents avec leurs stats
        return agents.slice(0, 6).map(a => {
          const stats = statsParAgent.get(a.id);
          return {
            agentId: a.id,
            nom: a.nom,
            prenom: a.prenom,
            transfertsCount: stats?.transferts || 0,
            montantTotal: 0,
            commissionsGenerees: stats?.frais || 0,
            tauxReussite: stats?.total ? Math.round((stats.payees / stats.total) * 100) : 0,
            count: stats?.transferts || 0
          };
        });
      }),
      catchError(() => of([]))
    );
  }

  // ─── Utilitaires ──────────────────────────────────────────────────────────

  /**
   * Approuver une validation AML
   * Marque la déclaration comme traitée dans la BDD
   */
  approveValidation(declarationId: number): Observable<any> {
    return this.http.put(
      `${this.apiUrl}/aml/declarations/${declarationId}`,
      { traitee: true }
    ).pipe(
      tap(() => console.log(`✅ Validation ${declarationId} approuvée`)),
      catchError(err => {
        console.error(`❌ Erreur approbation ${declarationId}:`, err);
        return throwError(() => err);
      })
    );
  }

  /**
   * Rejeter une validation AML
   * Marque la déclaration comme rejetée dans la BDD
   */
  rejectValidation(declarationId: number): Observable<any> {
    return this.http.put(
      `${this.apiUrl}/aml/declarations/${declarationId}`,
      { traitee: true }
    ).pipe(
      tap(() => console.log(`✅ Validation ${declarationId} rejetée`)),
      catchError(err => {
        console.error(`❌ Erreur rejet ${declarationId}:`, err);
        return throwError(() => err);
      })
    );
  }

  private formatMontant(montant: number): string {
    if (montant >= 1_000_000) return (montant / 1_000_000).toFixed(2) + 'M';
    if (montant >= 1_000)     return (montant / 1_000).toFixed(1) + 'K';
    return montant.toLocaleString('fr-MA');
  }
}