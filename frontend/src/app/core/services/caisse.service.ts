import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CaisseOperationResponse } from '../models/caisse/caisse-operation-response.model';
import { ClotureCaisseResponse } from '../models/caisse/cloture-caisse-response.model';
import { ClotureCaisseRequest } from '../models/caisse/cloture-caisse-request.model';

@Injectable({ providedIn: 'root' })
export class CaisseService {

  private readonly baseOps = `${environment.apiUrl}/api/caisse-operations`;
  private readonly baseClotures = `${environment.apiUrl}/api/clotures-caisse`;

  constructor(private http: HttpClient) {}

  // ─── Opérations ────────────────────────────────────────────────────────────

  findAllOperations(): Observable<CaisseOperationResponse[]> {
    return this.http.get<CaisseOperationResponse[]>(`${this.baseOps}/all`);
  }

  findByAgent(email: string): Observable<CaisseOperationResponse[]> {
    return this.http.get<CaisseOperationResponse[]>(`${this.baseOps}/agent/${email}`);
  }

  consulterSolde(email: string): Observable<number> {
    return this.http.get<number>(`${this.baseOps}/agent/${email}/solde`);
  }

  historiqueOperations(email: string): Observable<CaisseOperationResponse[]> {
    return this.http.get<CaisseOperationResponse[]>(`${this.baseOps}/${email}/operations`);
  }

  ouvrirCaisse(agentEmail: string, montantInitial: number): Observable<CaisseOperationResponse> {
    const params = new HttpParams()
      .set('agentEmail', agentEmail)
      .set('montantInitial', montantInitial.toString());
    return this.http.post<CaisseOperationResponse>(`${this.baseOps}/ouvrir`, null, { params });
  }

  deleteOperation(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseOps}/id/${id}`);
  }

  // ─── Clôtures ──────────────────────────────────────────────────────────────

  findAllClotures(): Observable<ClotureCaisseResponse[]> {
    return this.http.get<ClotureCaisseResponse[]>(`${this.baseClotures}/all`);
  }

  findCloture(email: string, date: string): Observable<ClotureCaisseResponse> {
    return this.http.get<ClotureCaisseResponse>(`${this.baseClotures}/agent/${email}/date/${date}`);
  }

  findEcarts(): Observable<ClotureCaisseResponse[]> {
    return this.http.get<ClotureCaisseResponse[]>(`${this.baseClotures}/ecarts`);
  }

  findEcartsAgent(email: string): Observable<ClotureCaisseResponse[]> {
    return this.http.get<ClotureCaisseResponse[]>(`${this.baseClotures}/agent/${email}/ecarts`);
  }

  saveCloture(request: ClotureCaisseRequest): Observable<ClotureCaisseResponse> {
    return this.http.post<ClotureCaisseResponse>(`${this.baseClotures}/add-one`, request);
  }

  updateCloture(request: ClotureCaisseRequest): Observable<ClotureCaisseResponse> {
    return this.http.put<ClotureCaisseResponse>(`${this.baseClotures}/update`, request);
  }

  cloturerCaisse(email: string, request: ClotureCaisseRequest): Observable<ClotureCaisseResponse> {
    return this.http.post<ClotureCaisseResponse>(`${this.baseClotures}/${email}/cloturer`, request);
  }

  rapportCloture(email: string, date: string): Observable<ClotureCaisseResponse> {
    return this.http.get<ClotureCaisseResponse>(`${this.baseClotures}/${email}/rapport`, {
      params: { date }
    });
  }

  deleteCloture(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseClotures}/id/${id}`);
  }

  findCloturesByAgence(agenceId: number,limit:number): Observable<ClotureCaisseResponse[]> {
    return this.http.get<ClotureCaisseResponse[]>(`${this.baseClotures}/agence/${agenceId}`, {
      params: { limit: limit.toString() }
    });
  }
}
