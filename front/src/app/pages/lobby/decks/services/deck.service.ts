import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { environment } from '../../../../../environments'

export interface Deck {
  id: string;
  name: string;
  cardCount: number;
  coverCardImage: string;
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
  private _decks = signal<Deck[]>([
    {
      id: '1',
      name: 'Charizard Pyroclast',
      cardCount: 60,
      coverCardImage: 'https://images.pokemontcg.io/sv3/125_hires.png',
    },
    {
      id: '2',
      name: 'Blastoise Torrential',
      cardCount: 60,
      coverCardImage: 'https://images.pokemontcg.io/sv3/120_hires.png',
    },
    {
      id: '3',
      name: 'Venusaur Overgrowth',
      cardCount: 60,
      coverCardImage: 'https://images.pokemontcg.io/sv3/3_hires.png',
    },
  ]);

  private _selectedDeckId = signal<string | null>('1'); // Por defecto seleccionamos el primero

  // Exponer señales públicas de solo lectura
  decks = this._decks.asReadonly();
  selectedDeckId = this._selectedDeckId.asReadonly();

  /**
   * Carga los mazos simulando una petición HTTP.
   */
  async loadDecks(): Promise<void> {
    return new Promise((resolve) => {
      // Simula latencia de red de 300ms
      setTimeout(() => {
        resolve();
      }, 300);
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
    return new Promise((resolve) => {
      setTimeout(() => {
        // Imágenes de portada aleatorias para nuevos mazos
        const covers = [
          'https://images.pokemontcg.io/sv3/125_hires.png',
          'https://images.pokemontcg.io/sv3/120_hires.png',
          'https://images.pokemontcg.io/sv3/3_hires.png',
        ];
        const randomCover = covers[Math.floor(Math.random() * covers.length)];

        const newDeck: Deck = {
          id: Math.random().toString(36).substring(2, 9),
          name: name || 'Nuevo Mazo',
          cardCount: 60,
          coverCardImage: randomCover,
        };

        this._decks.update((current) => [...current, newDeck]);
        resolve();
      }, 300);
    });
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
