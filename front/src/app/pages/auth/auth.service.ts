import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { signal } from '@angular/core';
import { LoginFormData } from './login/login-page.component';
import { environment } from '../../../environments';
import { Observable, tap } from 'rxjs';
import { RegisterFormData } from './register/register-page.component';

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
  private http = inject(HttpClient);
  isLoggedIn = signal(false);

  loginUser(credentials: LoginFormData): Observable<LoginResponse> {
    // environment its an object, so we need to access the login url.
    return this.http.post<LoginResponse>(environment.api.auth.login, credentials);
  }

  registerUser(credentials: RegisterFormData): Observable<RegisterResponse> {
    return this.http.post<RegisterResponse>(environment.api.auth.register, credentials).pipe(
      tap((response) => {
        if (response.access_token && response.refresh_token) {
          this.saveSession(response);
        }
      }),
    );
  }

  private saveSession(tokens: { access_token: string; refresh_token: string }): void {
    localStorage.setItem('access_token', tokens.access_token);
    localStorage.setItem('refresh_token', tokens.refresh_token);
    this.isLoggedIn.set(true);
  }
}
