import { Routes } from '@angular/router';
import { LobbyPage } from './pages/lobby/lobby-page';
import { GamePage } from './pages/game/game-page.component';

export const routes: Routes = [
  { path: '', component: LobbyPage },
  {
    path: 'game',
    loadComponent: () =>
      import('./layouts/game-layout/game-layout.component').then((m) => m.GameLayout), // Mejora el rendimiento
    children: [
      {
        path: '',
        component: GamePage,
      },
    ],
  },
];
