import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface DeclarationSoupcon {
  id: number;
  nomClient: string;
  nomBeneficiaire: string;
  montant: number;
  motif: string;
  status: string;
  dateDeclaration: string;
}

export interface JournalAudit {
  id: number;
  action: string;
  utilisateur: string;
  description: string;
  dateAction: string;
}

export interface RegleAML {
  id: number;
  code: string;
  nom: string;
  description: string;
  seuil?: number;
  actif: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class AmlService {
  private readonly apiUrl = `${environment.apiUrl}/api/aml`;

  constructor(private http: HttpClient) {}

  // ─── Declarations de Soupçon ───────────────────────────────────────────────

  // Lister les déclarations
  getDeclarations(): Observable<DeclarationSoupcon[]> {
    return this.http.get<DeclarationSoupcon[]>(`${this.apiUrl}/declarations`);
  }

  // Récupérer une déclaration
  getDeclarationById(id: number): Observable<DeclarationSoupcon> {
    return this.http.get<DeclarationSoupcon>(`${this.apiUrl}/declarations/${id}`);
  }

  // Créer une déclaration
  createDeclaration(data: Partial<DeclarationSoupcon>): Observable<DeclarationSoupcon> {
    return this.http.post<DeclarationSoupcon>(`${this.apiUrl}/declarations`, data);
  }

  // Modifier une déclaration
  updateDeclaration(id: number, data: Partial<DeclarationSoupcon>): Observable<DeclarationSoupcon> {
    return this.http.put<DeclarationSoupcon>(`${this.apiUrl}/declarations/${id}`, data);
  }

  // Supprimer une déclaration
  deleteDeclaration(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/declarations/${id}`);
  }

  // ─── Journal Audit ──────────────────────────────────────────────────────────

  // Lister les audits
  getAudits(): Observable<JournalAudit[]> {
    return this.http.get<JournalAudit[]>(`${this.apiUrl}/audit`);
  }

  // Récupérer un audit
  getAuditById(id: number): Observable<JournalAudit> {
    return this.http.get<JournalAudit>(`${this.apiUrl}/audit/${id}`);
  }

  // Créer un audit
  createAudit(data: Partial<JournalAudit>): Observable<JournalAudit> {
    return this.http.post<JournalAudit>(`${this.apiUrl}/audit`, data);
  }

  // Modifier un audit
  updateAudit(id: number, data: Partial<JournalAudit>): Observable<JournalAudit> {
    return this.http.put<JournalAudit>(`${this.apiUrl}/audit/${id}`, data);
  }

  // Supprimer un audit
  deleteAudit(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/audit/${id}`);
  }

  // ─── Regles AML ─────────────────────────────────────────────────────────────

  // Lister les règles
  getRegles(): Observable<RegleAML[]> {
    return this.http.get<RegleAML[]>(`${this.apiUrl}/regles`);
  }

  // Récupérer une règle
  getRegleById(id: number): Observable<RegleAML> {
    return this.http.get<RegleAML>(`${this.apiUrl}/regles/${id}`);
  }

  // Créer une règle
  createRegle(data: Partial<RegleAML>): Observable<RegleAML> {
    return this.http.post<RegleAML>(`${this.apiUrl}/regles`, data);
  }

  // Modifier une règle
  updateRegle(id: number, data: Partial<RegleAML>): Observable<RegleAML> {
    return this.http.put<RegleAML>(`${this.apiUrl}/regles/${id}`, data);
  }

  // Supprimer une règle
  deleteRegle(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/regles/${id}`);
  }

  // ─── Actions Validations ────────────────────────────────────────────────────

  // Approuver une validation
  approveDeclaration(id: number): Observable<DeclarationSoupcon> {
    return this.http.put<DeclarationSoupcon>(`${this.apiUrl}/declarations/${id}`, {
      status: 'APPROVED'
    });
  }

  // Rejeter une validation
  rejectDeclaration(id: number): Observable<DeclarationSoupcon> {
    return this.http.put<DeclarationSoupcon>(`${this.apiUrl}/declarations/${id}`, {
      status: 'REJECTED'
    });
  }
}
