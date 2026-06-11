import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { ToastService } from '../services/toast.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const auth  = inject(AuthService);
  const router = inject(Router);
  const toast  = inject(ToastService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {

      if (error.status === 401) {
        const refreshToken = auth.refreshToken;
        if (refreshToken) {
          return auth.refresh({ refreshToken }).pipe(
            switchMap(tokenRes => {
              const retried = req.clone({
                setHeaders: { Authorization: `Bearer ${tokenRes.accessToken}` }
              });
              return next(retried);
            }),
            catchError(refreshError => {
              auth.clearSession();
              router.navigate(['/auth/login']);
              return throwError(() => refreshError);
            })
          );
        }
        auth.clearSession();
        router.navigate(['/auth/login']);
        return throwError(() => error);
      }

      if (error.status === 403 && !req.url.includes('/api/auth')) {
        // Stay on current page — show a toast instead of redirecting
        const msg = error?.error?.message ?? 'Vous n\'avez pas les droits pour effectuer cette action.';
        toast.show(msg, 'error');
        return throwError(() => error);
      }

      return throwError(() => error);
    })
  );
};
