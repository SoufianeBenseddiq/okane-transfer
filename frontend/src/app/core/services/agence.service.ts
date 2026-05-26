import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AgenceResponse, AgenceRequest } from '../models/agence';

@Injectable({ providedIn: 'root' })
export class AgenceService {

  private readonly base = `${environment.apiUrl}/api/agences`;

  constructor(private http: HttpClient) {}

  findAll(): Observable<AgenceResponse[]> {
    return this.http.get<AgenceResponse[]>(`${this.base}/all`);
  }

  findActives(): Observable<AgenceResponse[]> {
    return this.http.get<AgenceResponse[]>(`${this.base}/actives`);
  }

  findByNom(nom: string): Observable<AgenceResponse> {
    return this.http.get<AgenceResponse>(`${this.base}/nom/${nom}`);
  }

  findByAdresse(adresse: string): Observable<AgenceResponse> {
    return this.http.get<AgenceResponse>(`${this.base}/adresse/${adresse}`);
  }

  findByResponsable(email: string): Observable<AgenceResponse> {
    return this.http.get<AgenceResponse>(`${this.base}/responsable/${email}`);
  }

  create(request: AgenceRequest): Observable<AgenceResponse> {
    return this.http.post<AgenceResponse>(`${this.base}/add-one`, request);
  }

  update(id: number, request: AgenceRequest): Observable<AgenceResponse> {
    return this.http.put<AgenceResponse>(`${this.base}/id/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/id/${id}`);
  }

  demanderRevisionPlafond(agenceId: number, body:any): Observable<AgenceResponse> {
    return this.http.post<AgenceResponse>(`${this.base}/id/${agenceId}/revision-plafond`, body);
  }
}
