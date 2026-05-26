import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface RapportAgence {
  agenceId: number;
  agenceName: string;
  transfertsCount: number;
  montantTotal: number;
  commissionsGenerees: number;
  fraisTotal: number;
  tauxReussite: number;
  periodeDebut: string;
  periodeFin: string;
}

export interface Agence {
  id: number;
  nom: string;
  adresse: string;
  pays: string;
  plafondJournalier: number;
  responsableEmail?: string;
  actif: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class RapportsAgenceService {
  private readonly apiUrl = `${environment.apiUrl}/api/agences`;

  constructor(private http: HttpClient) {}

  // Chercher agence par nom
  getAgenceByNom(nom: string): Observable<Agence> {
    return this.http.get<Agence>(`${this.apiUrl}/nom/${nom}`);
  }

  // Chercher agence par adresse
  getAgenceByAdresse(adresse: string): Observable<Agence> {
    return this.http.get<Agence>(`${this.apiUrl}/adresse/${adresse}`);
  }

  // Chercher agence par responsable
  getAgenceByResponsable(email: string): Observable<Agence> {
    return this.http.get<Agence>(`${this.apiUrl}/responsable/${email}`);
  }

  // Lister les agences actives
  getAgencesActives(): Observable<Agence[]> {
    return this.http.get<Agence[]>(`${this.apiUrl}/actives`);
  }

  // Lister toutes les agences
  getAllAgences(): Observable<Agence[]> {
    return this.http.get<Agence[]>(`${this.apiUrl}/all`);
  }

  // Créer une agence
  createAgence(data: Partial<Agence>): Observable<Agence> {
    return this.http.post<Agence>(`${this.apiUrl}/add-one`, data);
  }

  // Modifier une agence
  updateAgence(id: number, data: Partial<Agence>): Observable<Agence> {
    return this.http.put<Agence>(`${this.apiUrl}/id/${id}`, data);
  }

  // Supprimer une agence
  deleteAgence(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/id/${id}`);
  }

  // Générer rapport d'agence (mock pour l'instant)
  getRapportAgence(agenceId: number, debut: string, fin: string): Observable<RapportAgence> {
    return this.http.get<RapportAgence>(`${this.apiUrl}/${agenceId}/rapport`, {
      params: { debut, fin }
    });
  }
}
