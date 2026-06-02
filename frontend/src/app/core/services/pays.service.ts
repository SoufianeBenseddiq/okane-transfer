import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PaysResponse } from '../models/pays/pays-response.model';
import { PaysRequest } from '../models/pays/pays-request.model';

@Injectable({ providedIn: 'root' })
export class PaysService {
  private readonly base      = `${environment.apiUrl}/api/pays`;
  private readonly adminBase = `${environment.apiUrl}/api/admin/pays`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<PaysResponse[]> {
    return this.http.get<PaysResponse[]>(this.base);
  }

  getById(id: number): Observable<PaysResponse> {
    return this.http.get<PaysResponse>(`${this.base}/${id}`);
  }

  createPays(request: PaysRequest): Observable<PaysResponse> {
    return this.http.post<PaysResponse>(this.adminBase, request);
  }

  updatePays(id: number, request: PaysRequest): Observable<PaysResponse> {
    return this.http.put<PaysResponse>(`${this.adminBase}/${id}`, request);
  }
}
