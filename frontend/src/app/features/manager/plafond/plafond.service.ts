import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface PlafondInfo {
  agenceId: number;
  agenceName: string;
  plafondJournalier: number;
  montantUtilise: number;
  montantRestant: number;
  pourcentageUtilise: number;
  date: string;
}

export interface TransfertSummary {
  id: number;
  montant: number;
  nomBeneficiaire: string;
  dateCreation: string;
  status: string;
}

@Injectable({
  providedIn: 'root'
})
export class PlafondService {
  private readonly transfertUrl = `${environment.apiUrl}/api/transferts`;
  private readonly agenceUrl = `${environment.apiUrl}/api/agences`;

  constructor(private http: HttpClient) {}

  // Récupérer les infos de plafond d'une agence
  getPlafondAgence(agenceId: number): Observable<PlafondInfo> {
    return this.http.get<PlafondInfo>(`${this.agenceUrl}/id/${agenceId}/plafond`);
  }

  // Récupérer le plafond du jour
  getPlafondDuJour(agenceId: number): Observable<PlafondInfo> {
    const today = new Date().toISOString().split('T')[0];
    return this.http.get<PlafondInfo>(`${this.agenceUrl}/id/${agenceId}/plafond`, {
      params: new HttpParams().set('date', today)
    });
  }

  // Récupérer les transferts du jour (pour calculer plafond utilisé)
  getTransfertsJour(agenceId?: number): Observable<TransfertSummary[]> {
    const today = new Date().toISOString().split('T')[0];
    let params = new HttpParams().set('date', today);
    
    if (agenceId) {
      params = params.set('agenceId', agenceId.toString());
    }

    return this.http.get<TransfertSummary[]>(`${this.transfertUrl}/jour`, { params });
  }

  // Récupérer les transferts du mois
  getTransfertsMois(agenceId?: number): Observable<TransfertSummary[]> {
    const debut = new Date(new Date().getFullYear(), new Date().getMonth(), 1)
      .toISOString().split('T')[0];
    const fin = new Date().toISOString().split('T')[0];

    let params = new HttpParams()
      .set('debut', debut)
      .set('fin', fin);
    
    if (agenceId) {
      params = params.set('agenceId', agenceId.toString());
    }

    return this.http.get<TransfertSummary[]>(`${this.transfertUrl}/periode`, { params });
  }

  // Vérifier si un montant respecte le plafond
  verifierPlafond(agenceId: number, montant: number): Observable<{
    plafondSuffisant: boolean;
    montantRestant: number;
  }> {
    return this.http.post<any>(`${this.agenceUrl}/id/${agenceId}/verifier-plafond`, {
      montant
    });
  }

  // Modifier le plafond journalier d'une agence
  updatePlafondJournalier(agenceId: number, nouveauPlafond: number): Observable<any> {
    return this.http.put(`${this.agenceUrl}/id/${agenceId}/plafond`, {
      plafondJournalier: nouveauPlafond
    });
  }

  // Récupérer l'historique des plafonds
  getHistoriquePlafond(agenceId: number, debut?: string, fin?: string): Observable<PlafondInfo[]> {
    let params = new HttpParams();
    if (debut) params = params.set('debut', debut);
    if (fin) params = params.set('fin', fin);

    return this.http.get<PlafondInfo[]>(`${this.agenceUrl}/id/${agenceId}/plafond/historique`, { params });
  }
}
