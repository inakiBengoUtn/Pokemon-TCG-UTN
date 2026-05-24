import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { environment } from '../../../../../environments'
import { Observable } from 'rxjs';
import { ActivatedRoute } from '@angular/router';

export interface Deck {
  id: string;
  name: string;
  card_count: number;
  cover_image: string;
}

@Injectable({
  providedIn: 'root',
})
export class DeckService {
  private http = inject(HttpClient);
  private route = inject(ActivatedRoute)

  // Estado privado con señales reactivas
  private _decks = signal<Deck[]>([]);
  private _deckEdit = signal<Deck | null>(null);
  private _selectedDeckId = signal<string | null>('1'); // Por defecto seleccionamos el primero

  // Exponer señales públicas de solo lectura
  decks = this._decks.asReadonly();
  deckEdit = this._deckEdit.asReadonly();
  selectedDeckId = this._selectedDeckId.asReadonly();

  /**
   * Carga los mazos simulando una petición HTTP.
   */
  async loadDecks() {
    this.http.get<Deck[]>(environment.api.deck.get)
      .subscribe((res) => {
        this._decks.set(res)
        this._selectedDeckId.set(res[0].id)
      });
  }

  /**
   * Obtiene el Deck actual basado en el ID de la URL activa.
   * Se actualiza automáticamente cada vez que el ID cambia.
   */
  getCurrentDeck(id: string) {
    this.http.get<Deck>(`${environment.api.deck.getById}/${id}`)
      .subscribe((res) => {
        this._deckEdit.set(res)
        console.log(res)
      })
  }

  /**
   * Selecciona un mazo activo enviando la información al backend (simulado).
   */
  async selectDeck(id: string): Promise<void> {
  }

  /**
   * Crea un nuevo mazo (simulado).
   */
  async createDeck(name: string): Promise<void> {
  }

  /**
   * Edita el nombre de un mazo existente (simulado).
   */
  async updateDeck(id: string, name: string): Promise<void> {
  }

  /**
   * Elimina un mazo (simulado).
   */
  async deleteDeck(id: string): Promise<void> {
  }
}
