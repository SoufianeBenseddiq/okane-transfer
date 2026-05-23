import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LoginRequest, RefreshTokenRequest, TokenResponse } from '../models/auth';
import { UserResponse } from '../models/user';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly base = `${environment.apiUrl}/api/auth`;

  private _token$ = new BehaviorSubject<string | null>(this.storedToken);
  private _currentUser$ = new BehaviorSubject<UserResponse | null>(this.storedUser);

  readonly token$ = this._token$.asObservable();
  readonly currentUser$ = this._currentUser$.asObservable();

  constructor(private http: HttpClient) {}

  // ─── Auth endpoints ────────────────────────────────────────────────────────

  login(request: LoginRequest): Observable<TokenResponse> {
    return this.http.post<TokenResponse>(`${this.base}/login`, request).pipe(
      tap(res => {
        if (res.accessToken) {
          this.saveToken(res.accessToken, res.refreshToken);
        }
      })
    );
  }

  refresh(request: RefreshTokenRequest): Observable<TokenResponse> {
    return this.http.post<TokenResponse>(`${this.base}/refresh`, request).pipe(
      tap(res => {
        if (res.accessToken) {
          this.saveToken(res.accessToken, res.refreshToken);
        }
      })
    );
  }

  logout(): Observable<void> {
    return this.http.post<void>(`${this.base}/logout`, {}).pipe(
      tap(() => this.clearSession())
    );
  }

  // ─── Session helpers ───────────────────────────────────────────────────────

  get token(): string | null {
    return this._token$.value;
  }

  get refreshToken(): string | null {
    return localStorage.getItem('refreshToken');
  }

  get isLoggedIn(): boolean {
    return !!this._token$.value;
  }

  get currentUser(): UserResponse | null {
    return this._currentUser$.value;
  }

  setCurrentUser(user: UserResponse): void {
    this._currentUser$.next(user);
    localStorage.setItem('currentUser', JSON.stringify(user));
  }

  clearSession(): void {
    this._token$.next(null);
    this._currentUser$.next(null);
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('currentUser');
  }

  private saveToken(access: string | null, refresh: string | null): void {
    if (access) {
      localStorage.setItem('accessToken', access);
      this._token$.next(access);
    }
    if (refresh) {
      localStorage.setItem('refreshToken', refresh);
    }
  }

  private get storedToken(): string | null {
    return localStorage.getItem('accessToken');
  }

  private get storedUser(): UserResponse | null {
    const raw = localStorage.getItem('currentUser');
    return raw ? JSON.parse(raw) : null;
  }
}
