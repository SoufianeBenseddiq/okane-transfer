import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { UserResponse, CreateUserRequest, UpdateProfilRequest, AdminUpdateUserRequest } from '../models/user';
import { PieceIdentiteResponse, PieceIdentiteRequest } from '../models/piece-identite';
import { RoleUtilisateur } from '../models/enums';

@Injectable({ providedIn: 'root' })
export class UserService {

  private readonly base = `${environment.apiUrl}/api/utilisateurs`;

  constructor(private http: HttpClient) {}

  // ─── ADMIN: user management ────────────────────────────────────────────────

  create(request: CreateUserRequest): Observable<UserResponse> {
    return this.http.post<UserResponse>(this.base, request);
  }

  findAll(role?: RoleUtilisateur): Observable<UserResponse[]> {
    const params = role ? new HttpParams().set('role', role) : undefined;
    return this.http.get<UserResponse[]>(this.base, { params });
  }

  findById(id: number): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${this.base}/${id}`);
  }

  updateById(id: number, request: AdminUpdateUserRequest): Observable<UserResponse> {
    return this.http.put<UserResponse>(`${this.base}/${id}`, request);
  }

  deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }

  desactiver(id: number): Observable<void> {
    return this.http.put<void>(`${this.base}/${id}/desactiver`, null);
  }

  reactiver(id: number): Observable<void> {
    return this.http.put<void>(`${this.base}/${id}/reactiver`, null);
  }

  // ─── CLIENT: own profile ───────────────────────────────────────────────────

  getMe(): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${this.base}/me`);
  }

  updateMe(request: UpdateProfilRequest): Observable<UserResponse> {
    return this.http.put<UserResponse>(`${this.base}/me`, request);
  }

  deleteMe(): Observable<void> {
    return this.http.delete<void>(`${this.base}/me`);
  }

  // ─── Identity documents ────────────────────────────────────────────────────

  getPieces(clientId: number): Observable<PieceIdentiteResponse[]> {
    return this.http.get<PieceIdentiteResponse[]>(`${this.base}/${clientId}/pieces`);
  }

  addPiece(clientId: number, request: PieceIdentiteRequest): Observable<PieceIdentiteResponse> {
    return this.http.post<PieceIdentiteResponse>(`${this.base}/${clientId}/pieces`, request);
  }

  setPrincipale(clientId: number, pieceId: number): Observable<PieceIdentiteResponse> {
    return this.http.put<PieceIdentiteResponse>(
      `${this.base}/${clientId}/pieces/${pieceId}/principale`,
      null
    );
  }

  deletePiece(clientId: number, pieceId: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${clientId}/pieces/${pieceId}`);
  }
 searchClients(query: string): Observable<UserResponse[]> {
  return this.http.get<UserResponse[]>(`${this.base}/search/${encodeURIComponent(query)}`);
  }

  changePassword(currentPassword: string, newPassword: string): Observable<UserResponse> {
    return this.http.put<UserResponse>(`${this.base}/change-password`, {
      currentPassword,
      newPassword
    });
  }
}
