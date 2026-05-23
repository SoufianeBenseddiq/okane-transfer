import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { DeclarationResponse } from '../models/aml/declaration-response.model';
import { AuditResponse } from '../models/aml/audit-response.model';
import { RegleAML } from '../models/aml/regle-aml.model';

@Injectable({ providedIn: 'root' })
export class AmlService {

  private readonly baseDeclarations = `${environment.apiUrl}/api/aml/declarations`;
  private readonly baseAudit = `${environment.apiUrl}/api/aml/audit`;
  private readonly baseRegles = `${environment.apiUrl}/api/aml/regles`;

  constructor(private http: HttpClient) {}

  // ─── Déclarations de soupçon ───────────────────────────────────────────────

  getAllDeclarations(): Observable<DeclarationResponse[]> {
    return this.http.get<DeclarationResponse[]>(this.baseDeclarations);
  }

  getDeclarationById(id: number): Observable<DeclarationResponse> {
    return this.http.get<DeclarationResponse>(`${this.baseDeclarations}/${id}`);
  }

  updateDeclaration(id: number, declaration: Partial<DeclarationResponse>): Observable<DeclarationResponse> {
    return this.http.put<DeclarationResponse>(`${this.baseDeclarations}/${id}`, declaration);
  }

  deleteDeclaration(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseDeclarations}/${id}`);
  }

  // ─── Journal d'audit ───────────────────────────────────────────────────────

  getAllAuditLogs(): Observable<AuditResponse[]> {
    return this.http.get<AuditResponse[]>(this.baseAudit);
  }

  getAuditById(id: number): Observable<AuditResponse> {
    return this.http.get<AuditResponse>(`${this.baseAudit}/${id}`);
  }

  // ─── Règles AML ────────────────────────────────────────────────────────────

  getAllRegles(): Observable<RegleAML[]> {
    return this.http.get<RegleAML[]>(this.baseRegles);
  }

  getRegleById(id: number): Observable<RegleAML> {
    return this.http.get<RegleAML>(`${this.baseRegles}/${id}`);
  }

  createRegle(regle: RegleAML): Observable<RegleAML> {
    return this.http.post<RegleAML>(this.baseRegles, regle);
  }

  updateRegle(id: number, regle: RegleAML): Observable<RegleAML> {
    return this.http.put<RegleAML>(`${this.baseRegles}/${id}`, regle);
  }

  deleteRegle(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseRegles}/${id}`);
  }
}
