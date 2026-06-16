import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface Agent {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  telephone: string;
  role: string;
  agenceId: number;
  actif: boolean;
  dateCreation?: string;
}

export interface AgentPerformance {
  agentId: number;
  nom: string;
  prenom: string;
  transfertsCount: number;
  montantTotal: number;
  commissionsGenerees: number;
  tauxReussite: number;
}

@Injectable({
  providedIn: 'root'
})
export class AgentsService {
  private readonly apiUrl = `${environment.apiUrl}/api/utilisateurs`;
  private readonly transfertsUrl = `${environment.apiUrl}/api/transferts`;
  private readonly caisseOperationsUrl = `${environment.apiUrl}/api/caisse-operations`;

  constructor(private http: HttpClient) {}

  // Lister tous les agents
  getAgents(): Observable<Agent[]> {
    return this.http.get<Agent[]>(this.apiUrl, {
      params: new HttpParams().set('role', 'ROLE_AGENT')
    });
  }

  // Récupérer les performances des agents basées sur les transferts
  getAgentsPerformance(): Observable<any[]> {
    return this.http.get<any[]>(this.transfertsUrl);
  }

  // Récupérer toutes les opérations de caisse (ENVOI/RETRAIT/...) pour tous les agents
  getCaisseOperations(): Observable<any[]> {
    return this.http.get<any[]>(`${this.caisseOperationsUrl}/all`);
  }

  // Récupérer un agent par ID
  getAgentById(id: number): Observable<Agent> {
    return this.http.get<Agent>(`${this.apiUrl}/${id}`);
  }

  // Créer un agent
  createAgent(data: Partial<Agent>): Observable<Agent> {
    return this.http.post<Agent>(this.apiUrl, data);
  }

  // Modifier un agent
  updateAgent(id: number, data: Partial<Agent>): Observable<Agent> {
    return this.http.put<Agent>(`${this.apiUrl}/${id}`, data);
  }

  // Désactiver un agent
  deactivateAgent(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/desactiver`, null);
  }

  // Réactiver un agent
  reactivateAgent(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/reactiver`, null);
  }

  // Récupérer le profil de l'agent connecté
  getProfile(): Observable<Agent> {
    return this.http.get<Agent>(`${this.apiUrl}/me`);
  }

  // Mettre à jour le profil de l'agent connecté
  updateProfile(data: Partial<Agent>): Observable<Agent> {
    return this.http.put<Agent>(`${this.apiUrl}/me`, data);
  }
}