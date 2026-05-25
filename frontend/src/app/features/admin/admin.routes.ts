import { Routes } from '@angular/router';
import { AdminLayoutComponent } from '../../layouts/admin-layout/admin-layout.component';

export const adminRoutes: Routes = [
  {
    path: '',
    component: AdminLayoutComponent,
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        loadComponent: () => import('./dashboard/admin-dashboard.component').then(m => m.AdminDashboardComponent),
      },
      {
        path: 'devises',
        loadComponent: () => import('./devises/devises.component').then(m => m.DevisesComponent),
      },
      {
        path: 'frais',
        loadComponent: () => import('./frais/frais.component').then(m => m.FraisComponent),
      },
      {
        path: 'agences',
        loadComponent: () => import('./agences/agences.component').then(m => m.AgencesComponent),
      },
      {
        path: 'agences/:id',
        loadComponent: () => import('./agence-detail/agence-detail.component').then(m => m.AgenceDetailComponent),
      },
      {
        path: 'utilisateurs',
        loadComponent: () => import('./utilisateurs/utilisateurs.component').then(m => m.UtilisateursComponent),
      },
      {
        path: 'conformite',
        loadComponent: () => import('./conformite/conformite.component').then(m => m.ConformiteComponent),
      },
      {
        path: 'audit',
        loadComponent: () => import('./audit/audit.component').then(m => m.AuditComponent),
      },
      {
        path: 'rapports',
        loadComponent: () => import('./rapports/rapports.component').then(m => m.RapportsComponent),
      },
      {
        path: 'notifications',
        loadComponent: () => import('./notifications/notifications.component').then(m => m.NotificationsComponent),
      },
    ],
  },
];
