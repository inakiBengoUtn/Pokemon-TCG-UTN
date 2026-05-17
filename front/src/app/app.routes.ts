import { Routes } from '@angular/router';
import { LobbyPage } from './pages/lobby/lobby-page.component';
import { GamePage } from './pages/game/game-page.component';

// loadComponent mejora el rendimiento
export const routes: Routes = [
  // Authentication
  {
    path: 'auth',
    loadComponent: () =>
      import('./layouts/auth-layout/auth-layout.component').then((m) => m.AuthLayout),
    children: [
      {
        path: '',
        loadComponent: () =>
          import('./pages/auth/login/login-page.component').then((m) => m.LoginPage),
      },
      {
        path: 'register',
        loadComponent: () =>
          import('./pages/auth/register/register-page.component').then((m) => m.RegisterPage),
      },
    ],
  },
  // Lobby
  { path: '', component: LobbyPage },
  // match
  {
    path: 'match',
    loadComponent: () =>
      import('./layouts/match-layout/match-layout.component').then((m) => m.MatchLayout),
    children: [
      {
        path: '',
        loadComponent: () => import('./pages/match/match-page.component').then((m) => m.MatchPage),
      },
    ],
  },
  // deck builder
  {
    path: 'deck-builder',
    loadComponent: () =>
      import('./layouts/game-layout/game-layout.component').then((m) => m.GameLayout),
    children: [
      {
        path: '',
        loadComponent: () =>
          import('./pages/deck-builder/deck-builder-page.component').then(
            (m) => m.DeckBuilderPageComponent
          ),
      },
    ],
  },
  // game
  {
    path: 'game',
    loadComponent: () =>
      import('./layouts/game-layout/game-layout.component').then((m) => m.GameLayout),
    children: [
      {
        path: '',
        component: GamePage,
      },
    ],
  },
];
