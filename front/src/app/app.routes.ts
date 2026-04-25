import { Routes } from '@angular/router';
import { LobbyPage } from './pages/lobby/lobby-page.component';
import { GamePage } from './pages/game/game-page.component';

// loadComponent mejora el rendimiento
export const routes: Routes = [
  { path: '', component: LobbyPage },
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
