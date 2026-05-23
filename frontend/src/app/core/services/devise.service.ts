import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { DeviseResponse } from '../models/devise/devise-response.model';
import { DeviseRequest } from '../models/devise/devise-request.model';
import { CorridorResponse } from '../models/devise/corridor-response.model';
import { CorridorRequest } from '../models/devise/corridor-request.model';
import { GrilleTarifaireRequest } from '../models/devise/grille-tarifaire-request.model';
import { FraisResult } from '../models/devise/frais-result.model';

@Injectable({ providedIn: 'root' })
export class DeviseService {

  private readonly baseDevises = `${environment.apiUrl}/api/admin/devises`;
  private readonly baseCorridors = `${environment.apiUrl}/api/admin/corridors`;

  constructor(private http: HttpClient) {}

  // ─── Devises ───────────────────────────────────────────────────────────────

  getAllDevises(): Observable<DeviseResponse[]> {
    return this.http.get<DeviseResponse[]>(this.baseDevises);
  }

  getDeviseById(id: number): Observable<DeviseResponse> {
    return this.http.get<DeviseResponse>(`${this.baseDevises}/${id}`);
  }

  getDeviseByCode(code: string): Observable<DeviseResponse> {
    return this.http.get<DeviseResponse>(`${this.baseDevises}/code/${code}`);
  }

  createDevise(request: DeviseRequest): Observable<DeviseResponse> {
    return this.http.post<DeviseResponse>(this.baseDevises, request);
  }

  updateDevise(id: number, request: DeviseRequest): Observable<DeviseResponse> {
    return this.http.put<DeviseResponse>(`${this.baseDevises}/${id}`, request);
  }

  activerDevise(id: number): Observable<void> {
    return this.http.patch<void>(`${this.baseDevises}/${id}/activer`, null);
  }

  desactiverDevise(id: number): Observable<void> {
    return this.http.patch<void>(`${this.baseDevises}/${id}/desactiver`, null);
  }

  // ─── Corridors ─────────────────────────────────────────────────────────────

  getAllCorridors(): Observable<CorridorResponse[]> {
    return this.http.get<CorridorResponse[]>(this.baseCorridors);
  }

  getCorridorById(id: number): Observable<CorridorResponse> {
    return this.http.get<CorridorResponse>(`${this.baseCorridors}/${id}`);
  }

  createCorridor(request: CorridorRequest): Observable<CorridorResponse> {
    return this.http.post<CorridorResponse>(this.baseCorridors, request);
  }

  activerCorridor(id: number): Observable<void> {
    return this.http.patch<void>(`${this.baseCorridors}/${id}/activer`, null);
  }

  desactiverCorridor(id: number): Observable<void> {
    return this.http.patch<void>(`${this.baseCorridors}/${id}/desactiver`, null);
  }

  calculerFrais(corridorId: number, montant: number): Observable<FraisResult> {
    const params = new HttpParams().set('montant', montant.toString());
    return this.http.get<FraisResult>(`${this.baseCorridors}/${corridorId}/frais`, { params });
  }

  createGrille(request: GrilleTarifaireRequest): Observable<void> {
    return this.http.post<void>(`${this.baseCorridors}/grilles`, request);
  }
}
