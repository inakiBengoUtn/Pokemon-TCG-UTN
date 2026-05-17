import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DeckResponse, DeckSummaryResponse, DeckValidationResponse, SaveDeckRequest } from '../models/deck.model';

@Injectable({ providedIn: 'root' })
export class DeckApiService {
  private http = inject(HttpClient);
  private base = 'http://localhost:8080/api/decks';

  getDecks(): Observable<DeckSummaryResponse[]> {
    return this.http.get<DeckSummaryResponse[]>(this.base, { withCredentials: true });
  }

  getDeck(id: string): Observable<DeckResponse> {
    return this.http.get<DeckResponse>(`${this.base}/${id}`, { withCredentials: true });
  }

  createDeck(request: SaveDeckRequest): Observable<DeckResponse> {
    return this.http.post<DeckResponse>(this.base, request, { withCredentials: true });
  }

  updateDeck(id: string, request: SaveDeckRequest): Observable<DeckResponse> {
    return this.http.put<DeckResponse>(`${this.base}/${id}`, request, { withCredentials: true });
  }

  deleteDeck(id: string): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`, { withCredentials: true });
  }

  validateDeck(id: string): Observable<DeckValidationResponse> {
    return this.http.get<DeckValidationResponse>(`${this.base}/${id}/validate`, { withCredentials: true });
  }
}
