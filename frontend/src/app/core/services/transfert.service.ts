import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { TransfertResponse, CreateTransfertRequest, UpdateTransfertRequest, PaiementRequest } from '../models/transfert';
import { BeneficiaireResponse, CreateBeneficiaireRequest, UpdateBeneficiaireRequest } from '../models/beneficiaire';
import { ExpediteurResponse, CreateExpediteurRequest, UpdateExpediteurRequest } from '../models/expediteur';

@Injectable({ providedIn: 'root' })
export class TransfertService {

  private readonly baseTransferts = `${environment.apiUrl}/api/transferts`;
  private readonly baseBeneficiaires = `${environment.apiUrl}/api/beneficiaires`;
  private readonly baseExpediteurs = `${environment.apiUrl}/api/expediteurs`;

  constructor(private http: HttpClient) {}

  // ─── Transferts ────────────────────────────────────────────────────────────

  getAll(): Observable<TransfertResponse[]> {
    return this.http.get<TransfertResponse[]>(this.baseTransferts);
  }

  getById(id: number): Observable<TransfertResponse> {
    return this.http.get<TransfertResponse>(`${this.baseTransferts}/${id}`);
  }

  create(request: CreateTransfertRequest): Observable<TransfertResponse> {
    return this.http.post<TransfertResponse>(this.baseTransferts, request);
  }

  update(id: number, request: UpdateTransfertRequest): Observable<TransfertResponse> {
    return this.http.put<TransfertResponse>(`${this.baseTransferts}/${id}`, request);
  }

  payer(request: PaiementRequest): Observable<TransfertResponse> {
    return this.http.post<TransfertResponse>(`${this.baseTransferts}/paiement`, request);
  }

  annuler(id: number): Observable<TransfertResponse> {
    return this.http.put<TransfertResponse>(`${this.baseTransferts}/${id}/annuler`, null);
  }

  getByCodeRetrait(codeRetrait: string): Observable<TransfertResponse> {
    return this.http.get<TransfertResponse>(`${this.baseTransferts}/code/${codeRetrait}`);
  }

  getMesTransferts(clientId: number): Observable<TransfertResponse[]> {
    return this.http.get<TransfertResponse[]>(`${this.baseTransferts}/mes-transferts`, {
      params: { clientId: clientId.toString() }
    });
  }

  // ─── Bénéficiaires ─────────────────────────────────────────────────────────

  getAllBeneficiaires(): Observable<BeneficiaireResponse[]> {
    return this.http.get<BeneficiaireResponse[]>(this.baseBeneficiaires);
  }

  getBeneficiaireById(id: number): Observable<BeneficiaireResponse> {
    return this.http.get<BeneficiaireResponse>(`${this.baseBeneficiaires}/${id}`);
  }

  createBeneficiaire(request: CreateBeneficiaireRequest): Observable<BeneficiaireResponse> {
    return this.http.post<BeneficiaireResponse>(this.baseBeneficiaires, request);
  }

  updateBeneficiaire(id: number, request: UpdateBeneficiaireRequest): Observable<BeneficiaireResponse> {
    return this.http.put<BeneficiaireResponse>(`${this.baseBeneficiaires}/${id}`, request);
  }

  deleteBeneficiaire(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseBeneficiaires}/${id}`);
  }

  // ─── Expéditeurs ───────────────────────────────────────────────────────────

  getAllExpediteurs(): Observable<ExpediteurResponse[]> {
    return this.http.get<ExpediteurResponse[]>(this.baseExpediteurs);
  }

  getExpediteurById(id: number): Observable<ExpediteurResponse> {
    return this.http.get<ExpediteurResponse>(`${this.baseExpediteurs}/${id}`);
  }

  createExpediteur(request: CreateExpediteurRequest): Observable<ExpediteurResponse> {
    return this.http.post<ExpediteurResponse>(this.baseExpediteurs, request);
  }

  updateExpediteur(id: number, request: UpdateExpediteurRequest): Observable<ExpediteurResponse> {
    return this.http.put<ExpediteurResponse>(`${this.baseExpediteurs}/${id}`, request);
  }

  deleteExpediteur(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseExpediteurs}/${id}`);
  }
}
