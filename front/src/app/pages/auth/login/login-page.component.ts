import { Component, inject, signal } from '@angular/core';
import {
  form,
  required,
  FormField,
  email,
  minLength,
  submit,
  validate,
} from '@angular/forms/signals';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '../auth.service';
import { Router, RouterLink } from '@angular/router';

export interface LoginFormData {
  username: string;
  password: string;
}

@Component({
  selector: 'login-page',
  templateUrl: './login-page.component.html',
  styleUrl: './login-page.component.css',
  imports: [
    MatFormFieldModule,
    MatInputModule,
    FormField,
    MatIconModule,
    MatButtonModule,
    RouterLink,
  ],
})
export class LoginPage {
  authService = inject(AuthService);
  router = inject(Router);
  hidePassword = signal(true);
  loginModel = signal<LoginFormData>({
    username: '',
    password: '',
  });
  invalidCredentials = signal(false);

  // Method to toggle password visibility
  clickTooglePassword(event: MouseEvent) {
    event.preventDefault();
    this.hidePassword.set(!this.hidePassword());
    event.stopPropagation();
  }

  loginForm = form(this.loginModel, (path) => {
    required(path.username, { message: 'El nombre de usuario es requerido' });
    required(path.password, { message: 'La contraseña es requerida' });
    minLength(path.password, 6, { message: 'La contraseña debe tener al menos 6 caracteres' });
    validate(path.password, () => {
      if (this.invalidCredentials()) {
        return { message: 'Credenciales invalidas', kind: 'error' };
      }
      return undefined;
    });
    validate(path.username, () => {
      if (this.invalidCredentials()) {
        return { message: 'Credenciales invalidas', kind: 'error' };
      }
      return undefined;
    });
  });

  // Method to handle form submission
  onSubmit(event: Event) {
    event.preventDefault();

    if (!this.loginForm().valid()) return;

    const credentials = this.loginModel();
    this.authService.loginUser(credentials).subscribe({
      // store the tokens in the local storage.
      next: () => {
        this.router.navigate(['']);
      },
      // if the login fails, show an error message.
      error: (error) => {
        if (error.status === 400) {
          const { code } = error.error || 'UNKNOWN_ERROR';
          if (code === 'BAD_CREDENTIALS') {
            this.invalidCredentials.set(true);
          }
        }
      },
    });
  }

  // Method to reset form
  onReset() {
    this.loginModel.set({
      username: '',
      password: '',
    });
    this.loginForm().reset();
  }
}
