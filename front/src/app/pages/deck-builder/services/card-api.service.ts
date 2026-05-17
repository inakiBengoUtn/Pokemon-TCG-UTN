import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CardResponse } from '../models/card.model';

@Injectable({ providedIn: 'root' })
export class CardApiService {
  private http = inject(HttpClient);
  private base = 'http://localhost:8080/api/cards';

  search(setId = 'xy1', name?: string, supertype?: string): Observable<CardResponse[]> {
    let params = new HttpParams().set('setId', setId);
    if (name) params = params.set('name', name);
    if (supertype) params = params.set('supertype', supertype);
    return this.http.get<CardResponse[]>(this.base, { params, withCredentials: true });
  }

  syncSet(setId = 'xy1', force = false): Observable<{ synced: number; message: string }> {
    const params = new HttpParams().set('setId', setId).set('force', force);
    return this.http.post<{ synced: number; message: string }>(
      `${this.base}/sync`, null, { params, withCredentials: true }
    );
  }

  getCacheStatus(setId = 'xy1'): Observable<{ cached: boolean }> {
    const params = new HttpParams().set('setId', setId);
    return this.http.get<{ cached: boolean }>(`${this.base}/status`, { params, withCredentials: true });
  }
}
