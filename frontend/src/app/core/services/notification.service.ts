import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { NotificationResponse, CreateNotificationRequest } from '../models/notification';

@Injectable({ providedIn: 'root' })
export class NotificationService {

  private readonly base = `${environment.apiUrl}/api/notifications`;

  constructor(private http: HttpClient) {}

  // ── Current user ────────────────────────────────────────────────────────────
  getMesNotifications(): Observable<NotificationResponse[]> {
    return this.http.get<NotificationResponse[]>(`${this.base}/me`);
  }

  getMyUnreadCount(): Observable<number> {
    return this.http.get<number>(`${this.base}/me/unread/count`);
  }

  markAllMyAsRead(): Observable<void> {
    return this.http.put<void>(`${this.base}/me/read-all`, null);
  }

  markAsRead(id: number): Observable<void> {
    return this.http.put<void>(`${this.base}/${id}/read`, null);
  }

  // ── Admin CRUD ───────────────────────────────────────────────────────────────
  getAll(): Observable<NotificationResponse[]> {
    return this.http.get<NotificationResponse[]>(this.base);
  }

  getUnreadCount(): Observable<number> {
    return this.http.get<number>(`${this.base}/unread/count`);
  }

  getByDestinataire(id: number): Observable<NotificationResponse[]> {
    return this.http.get<NotificationResponse[]>(`${this.base}/destinataire/${id}`);
  }

  create(request: CreateNotificationRequest): Observable<NotificationResponse> {
    return this.http.post<NotificationResponse>(this.base, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
