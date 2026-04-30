import { catchError, switchMap, throwError, BehaviorSubject, filter, take } from 'rxjs';
import {
  HttpInterceptorFn,
  HttpRequest,
  HttpHandlerFn,
  HttpErrorResponse,
} from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../../pages/auth/auth.service';

// Variable para manejar múltiples peticiones 401 simultáneas
let isRefreshing = false;
const refreshTokenSubject = new BehaviorSubject<boolean | null>(null);

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);

  // Añadimos withCredentials para que las cookies HttpOnly se envíen siempre
  const authReq = req.clone({ withCredentials: true });

  return next(authReq).pipe(
    catchError((error) => {
      if (error instanceof HttpErrorResponse && error.status === 401) {
        return handle401Error(authReq, next, authService);
      }
      return throwError(() => error);
    }),
  );
};

const handle401Error = (
  request: HttpRequest<any>,
  next: HttpHandlerFn,
  authService: AuthService,
) => {
  if (!isRefreshing) {
    isRefreshing = true;
    refreshTokenSubject.next(null);

    // Llamamos al endpoint de refresh del backend
    return authService.refreshToken().pipe(
      switchMap(() => {
        isRefreshing = false;
        refreshTokenSubject.next(true);
        // Reintentamos la petición original
        return next(request);
      }),
      catchError((err) => {
        isRefreshing = false;
        authService.logout(); // Si falla el refresco, al login
        return throwError(() => err);
      }),
    );
  } else {
    // Si ya se está refrescando, esperamos a que termine
    return refreshTokenSubject.pipe(
      filter((result) => result !== null),
      take(1),
      switchMap(() => next(request)),
    );
  }
};
