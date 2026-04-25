import { Component, inject, signal } from '@angular/core';
import { form, FormField, minLength, required, validate } from '@angular/forms/signals';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../auth.service';

export interface RegisterFormData {
  username: string;
  password: string;
}

@Component({
  selector: 'register-page',
  templateUrl: './register-page.component.html',
  styleUrl: './register-page.component.css',
  imports: [
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    FormField,
    MatButtonModule,
    RouterLink,
  ],
})
export class RegisterPage {
  private authService = inject(AuthService);
  private router = inject(Router);
  hidePassword = signal(true);
  registerModel = signal<RegisterFormData>({
    username: '',
    password: '',
  });
  isLoading = signal(false);
  usernameAlreadyExists = signal(false);

  // Method to toggle password visibility
  clickTooglePassword(event: MouseEvent) {
    event.preventDefault();
    this.hidePassword.set(!this.hidePassword());
    event.stopPropagation();
  }

  registerForm = form(this.registerModel, (path) => {
    required(path.username, { message: 'El usuario es requerido.' });
    required(path.password, { message: 'La contraseña es requerida.' });
    minLength(path.password, 6, { message: 'La contraseña debe tener al menos 6 caracteres.' });
    validate(path.username, () =>
      this.usernameAlreadyExists()
        ? {
            message: 'El usuario ya existe, elije otro nombre.',
            kind: 'error',
          }
        : undefined,
    );
  });

  onSubmit(event: Event) {
    this.isLoading.set(true);
    event.preventDefault();

    const credentials = this.registerModel();
    this.authService.registerUser(credentials).subscribe({
      // store the tokens in the local storage.
      next: () => {
        this.router.navigate(['']);
      },
      // if the login fails, show an error message.
      error: (error) => {
        if (error.status === 400) {
          const { code } = error.error || 'UNKNOWN_ERROR';
          if (code === 'USER_TAKEN') {
            this.usernameAlreadyExists.set(true);
          }
        }
      },
    });

    this.isLoading.set(false);
  }

  // Method to reset form
  onReset() {
    this.registerModel.set({
      username: '',
      password: '',
    });
    this.registerForm().reset();
  }
}
