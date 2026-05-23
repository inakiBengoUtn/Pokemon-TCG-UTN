import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { environment } from '../../../../../environments'

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

  getDecks(): void {
    this.http.get<Deck[]>(environment.api.deck)
      .subscribe((res) => console.log(res));
  }

  // Estado privado con señales reactivas
  private _decks = signal<Deck[]>([]);

  private _selectedDeckId = signal<string | null>('1'); // Por defecto seleccionamos el primero

  // Exponer señales públicas de solo lectura
  decks = this._decks.asReadonly();
  selectedDeckId = this._selectedDeckId.asReadonly();

  /**
   * Carga los mazos simulando una petición HTTP.
   */
  async loadDecks() {
    this.http.get<Deck[]>(environment.api.deck)
      .subscribe((res) => {
        this._decks.set(res)
        console.log(res)
        this._selectedDeckId.set(res[0].id)
      });
  }

  /**
   * Selecciona un mazo activo enviando la información al backend (simulado).
   */
  async selectDeck(id: string): Promise<void> {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const deckExists = this._decks().some((d) => d.id === id);
        if (deckExists) {
          this._selectedDeckId.set(id);
          console.log(`[DeckService] Mazo activo seleccionado en el backend: ${id}`);
          resolve();
        } else {
          reject(new Error('Mazo no encontrado'));
        }
      }, 200);
    });
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
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const exists = this._decks().some((d) => d.id === id);
        if (exists) {
          this._decks.update((current) =>
            current.map((d) => (d.id === id ? { ...d, name } : d))
          );
          resolve();
        } else {
          reject(new Error('Mazo no encontrado'));
        }
      }, 200);
    });
  }

  /**
   * Elimina un mazo (simulado).
   */
  async deleteDeck(id: string): Promise<void> {
    return new Promise((resolve) => {
      setTimeout(() => {
        this._decks.update((current) => current.filter((d) => d.id !== id));
        // Si el mazo eliminado era el seleccionado, limpiamos la selección
        if (this._selectedDeckId() === id) {
          const remaining = this._decks();
          this._selectedDeckId.set(remaining.length > 0 ? remaining[0].id : null);
        }
        resolve();
      }, 200);
    });
  }
}
