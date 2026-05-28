import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface CaisseOperation {
  id: number;
  agentEmail: string;
  type: string;
  montant: number;
  dateOperation: string;
  description?: string;
}

export interface ClotureResponse {
  id: number;
  agentEmail: string;
  date: string;
  soldeSaisi: number;
  soldeSouhait?: number;
  ecart?: number;
  status?: string;
}

@Injectable({
  providedIn: 'root'
})
export class CaisseService {
  private readonly apiUrl = `${environment.apiUrl}/api/caisse-operations`;
  private readonly cloturePath = `${environment.apiUrl}/api/clotures-caisse`;

  constructor(private http: HttpClient) {}

  // ─── Operations Caisse ──────────────────────────────────────────────────────

  // Récupérer les opérations d'un agent
  getOperationsAgent(email: string): Observable<CaisseOperation[]> {
    return this.http.get<CaisseOperation[]>(`${this.apiUrl}/agent/${email}`);
  }

  // Récupérer le solde d'un agent
  getSoldeAgent(email: string): Observable<{ solde: number }> {
    return this.http.get<{ solde: number }>(`${this.apiUrl}/agent/${email}/solde`);
  }

  // Historique des opérations d'un agent
  getHistoriqueAgent(email: string): Observable<CaisseOperation[]> {
    return this.http.get<CaisseOperation[]>(`${this.apiUrl}/${email}/operations`);
  }

  // Solde théorique d'un agent
  getSoldeTheorique(agentEmail: string, debut: string, fin: string): Observable<{ solde: number }> {
    return this.http.get<{ solde: number }>(`${this.apiUrl}/${agentEmail}/${debut}/${fin}/solde`);
  }

  // Lister toutes les opérations
  getAllOperations(): Observable<CaisseOperation[]> {
    return this.http.get<CaisseOperation[]>(`${this.apiUrl}/all`);
  }

  // Ouvrir une caisse
  ouvrirCaisse(agentEmail: string, montantInitial: number): Observable<any> {
    const params = new HttpParams()
      .set('agentEmail', agentEmail)
      .set('montantInitial', montantInitial.toString());
    return this.http.post(`${this.apiUrl}/ouvrir`, {}, { params });
  }

  // Supprimer une opération
  deleteOperation(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/id/${id}`);
  }

  // ─── Clotures Caisse ───────────────────────────────────────────────────────

  // Cloture par agent et date
  getClotureByDate(email: string, date: string): Observable<ClotureResponse> {
    return this.http.get<ClotureResponse>(`${this.cloturePath}/agent/${email}/date/${date}`);
  }

  // Clotures avec écart
  getCloturksAvecEcart(): Observable<ClotureResponse[]> {
    return this.http.get<ClotureResponse[]>(`${this.cloturePath}/ecarts`);
  }

  // Écarts d'un agent
  getEcartsAgent(email: string): Observable<ClotureResponse[]> {
    return this.http.get<ClotureResponse[]>(`${this.cloturePath}/agent/${email}/ecarts`);
  }

  // Lister toutes les clotures
  getAllClotures(): Observable<ClotureResponse[]> {
    return this.http.get<ClotureResponse[]>(`${this.cloturePath}/all`);
  }

  // Rapport de cloture
  getRapportCloture(email: string, date: string): Observable<any> {
    return this.http.get(`${this.cloturePath}/${email}/rapport`, {
      params: new HttpParams().set('date', date)
    });
  }

  // Créer une cloture
  createCloture(data: Partial<ClotureResponse>): Observable<ClotureResponse> {
    return this.http.post<ClotureResponse>(`${this.cloturePath}/add-one`, data);
  }

  // Cloturer la caisse d'un agent
  cloturerCaisse(email: string): Observable<ClotureResponse> {
    return this.http.post<ClotureResponse>(`${this.cloturePath}/${email}/cloturer`, {});
  }

  // Modifier une cloture
  updateCloture(data: Partial<ClotureResponse>): Observable<ClotureResponse> {
    return this.http.put<ClotureResponse>(`${this.cloturePath}/update`, data);
  }

  // Supprimer une cloture
  deleteCloture(id: number): Observable<any> {
    return this.http.delete(`${this.cloturePath}/id/${id}`);
  }
}
