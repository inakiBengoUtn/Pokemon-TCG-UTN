import { Routes } from '@angular/router';
import { LobbyPage } from './pages/lobby/lobby-page.component';
import { GamePage } from './pages/game/game-page.component';
import { LobbyLayout } from './layouts/lobby-layout/lobby-layout.component';

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
  {
    path: '',
    component: LobbyLayout,
    children: [
      { path: '', component: LobbyPage },
      {
        path: 'decks',
        loadComponent: () => import('./pages/lobby/decks/list-decks/deck-list-page.component').then((m) => m.DecksListPage),
      },
      {
        path: 'decks/:id',
        loadComponent: () => import('./pages/lobby/decks/deck-edit/deck-edit-page.component').then((m) => m.DeckEditPage),
      }
    ]
  },
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
