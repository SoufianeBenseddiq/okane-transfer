import { CanActivateFn, Router, ActivatedRouteSnapshot } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { RoleUtilisateur } from '../models/enums';

/**
 * Usage in routes:
 *   canActivate: [roleGuard],
 *   data: { roles: [RoleUtilisateur.ROLE_ADMIN] }
 */
export const roleGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.isLoggedIn) {
    return router.createUrlTree(['/auth/login']);
  }

  const allowedRoles: RoleUtilisateur[] = route.data?.['roles'] ?? [];
  if (allowedRoles.length === 0) {
    return true;
  }
 // const user = auth['_currentUser$'].value;
  const user = auth.currentUser;
  if (user && allowedRoles.includes(user.role as RoleUtilisateur)) {
    return true;
  }

  return router.createUrlTree(['/unauthorized']);
};
