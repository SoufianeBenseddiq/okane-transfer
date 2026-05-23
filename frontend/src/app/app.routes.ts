import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';
import { RoleUtilisateur } from './core/models/enums';

export const routes: Routes = [
  // ─── Auth ──────────────────────────────────────────────────────────────────
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.routes').then(m => m.authRoutes)
  },

  // ─── Admin ─────────────────────────────────────────────────────────────────
  {
    path: 'admin',
    canActivate: [authGuard, roleGuard],
    data: { roles: [RoleUtilisateur.ROLE_ADMIN] },
    loadChildren: () => import('./features/admin/admin.routes').then(m => m.adminRoutes)
  },

  // ─── Manager ───────────────────────────────────────────────────────────────
  {
    path: 'manager',
    canActivate: [authGuard, roleGuard],
    data: { roles: [RoleUtilisateur.ROLE_MANAGER] },
    loadChildren: () => import('./features/manager/manager.routes').then(m => m.managerRoutes)
  },

  // ─── Agent ─────────────────────────────────────────────────────────────────
  {
    path: 'agent',
    canActivate: [authGuard, roleGuard],
    data: { roles: [RoleUtilisateur.ROLE_AGENT] },
    loadChildren: () => import('./features/agent/agent.routes').then(m => m.agentRoutes)
  },

  // ─── Client ────────────────────────────────────────────────────────────────
  {
    path: 'client',
    canActivate: [authGuard, roleGuard],
    data: { roles: [RoleUtilisateur.ROLE_CLIENT] },
    loadChildren: () => import('./features/client/client.routes').then(m => m.clientRoutes)
  },

  // ─── Default redirects ─────────────────────────────────────────────────────
  { path: 'unauthorized', loadComponent: () => import('./features/auth/unauthorized/unauthorized.component').then(m => m.UnauthorizedComponent) },
  { path: '', redirectTo: 'auth/login', pathMatch: 'full' },
  { path: '**', redirectTo: 'auth/login' }
];
