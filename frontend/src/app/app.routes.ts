import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'agent',
    loadComponent: () => import('./layouts/agent-layout/agent-layout.component').then(m => m.AgentLayoutComponent),
    children: [
      { path: 'dashboard', loadComponent: () => import('./features/agent/dashboard/dashboard.component').then(m => m.DashboardComponent) },
      { path: 'envoi', loadComponent: () => import('./features/agent/envoi/envoi.component').then(m => m.EnvoiComponent) },
      { path: 'paiement', loadComponent: () => import('./features/agent/paiement/paiement.component').then(m => m.PaiementComponent) },
      { path: 'caisse', loadComponent: () => import('./features/agent/caisse/caisse.component').then(m => m.CaisseComponent) },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
    ]
  },
  { path: '', redirectTo: 'agent/dashboard', pathMatch: 'full' },
  // gérer les pages introuvables 404 sans planter
  { path: '**', redirectTo: 'agent/dashboard' }
];
