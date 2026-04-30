import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { signal } from '@angular/core';
import { LoginFormData } from './login/login-page.component';
import { environment } from '../../../environments';
import { Observable, tap } from 'rxjs';
import { RegisterFormData } from './register/register-page.component';
import { Router } from '@angular/router';

interface LoginResponse {
  access_token: string;
  refresh_token: string;
}

interface RegisterResponse {
  access_token: string;
  refresh_token: string;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private router = inject(Router);
  private http = inject(HttpClient);

  loginUser(credentials: LoginFormData): Observable<LoginResponse> {
    // environment its an object, so we need to access the login url.
    return this.http.post<LoginResponse>(environment.api.auth.login, credentials, {
      withCredentials: true,
    });
  }

  registerUser(credentials: RegisterFormData): Observable<RegisterResponse> {
    return this.http.post<RegisterResponse>(environment.api.auth.register, credentials, {
      withCredentials: true,
    });
  }

  refreshToken() {
    // El backend leerá la cookie 'refreshToken' automáticamente
    return this.http.post(environment.api.auth.refresh, {}, { withCredentials: true });
  }

  logout() {
    this.router.navigate(['/auth/login']);
  }
}
