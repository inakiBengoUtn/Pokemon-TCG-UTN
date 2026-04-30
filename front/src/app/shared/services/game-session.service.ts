import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class GameSessionService {
  private http = inject(HttpClient);
  private readonly STORAGE_KEY = 'current_game_id';
  private readonly API_URL = 'https://tu-api.com/games';

  // Usamos un signal para que tus componentes reaccionen al estado de la partida
  currentGameId = signal<string | null>(localStorage.getItem(this.STORAGE_KEY));

  /**
   * Verifica si existe una sesión previa y si sigue activa en el servidor.
   */
  async checkActiveSession(): Promise<string | null> {
    const savedId = this.currentGameId();

    if (!savedId) return null;

    try {
      // Petición al servidor para validar el estado
      const response = await firstValueFrom(
        this.http.get<{ active: boolean }>(`${this.API_URL}/${savedId}/status`),
      );

      if (response.active) {
        return savedId;
      } else {
        this.clearSession();
        return null;
      }
    } catch (error) {
      // Si el servidor da error (ej. 404), asumimos que la partida no existe
      this.clearSession();
      return null;
    }
  }

  saveSession(gameId: string): void {
    localStorage.setItem(this.STORAGE_KEY, gameId);
    this.currentGameId.set(gameId);
  }

  clearSession(): void {
    localStorage.removeItem(this.STORAGE_KEY);
    this.currentGameId.set(null);
  }
}
