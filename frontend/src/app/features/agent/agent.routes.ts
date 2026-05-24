import { Routes } from '@angular/router';

export const agentRoutes: Routes = [
  {
    path: '',
    loadComponent: () => import('../../layouts/agent-layout/agent-layout.component')
      .then(m => m.AgentLayoutComponent),
    children: [
      {
        path: 'dashboard',
        loadComponent: () => import('./dashboard/dashboard.component').then(m => m.DashboardComponent)
      },
      {
        path: 'envoi',
        loadComponent: () => import('./envoi/envoi.component').then(m => m.EnvoiComponent)
      },
      {
        path: 'paiement',
        loadComponent: () => import('./paiement/paiement.component').then(m => m.PaiementComponent)
      },
      {
        path: 'caisse',
        loadComponent: () => import('./caisse/caisse.component').then(m => m.CaisseComponent)
      },
      {
        path: 'historique',
        loadComponent: () => import('./historique/historique.component').then(m => m.HistoriqueComponent)
      },
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      }
    ]
  }
];
