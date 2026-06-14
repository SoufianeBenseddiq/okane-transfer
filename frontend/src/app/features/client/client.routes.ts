import { Routes } from '@angular/router';

export const clientRoutes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./navbar/navbar.component').then(m => m.NavbarComponent),
    children: [
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./dashboard/dashboard.component').then(m => m.DashboardComponent)
      },
      {
        path: 'historique',
        loadComponent: () =>
          import('./historique/historique.component').then(m => m.HistoriqueComponent)
      },
      {
        path: 'transfert/:id',
        loadComponent: () =>
          import('./historique/transfer-detail/transfer-detail.component').then(m => m.TransferDetailComponent)
      },
      {
        path: 'suivi/:reference',
        loadComponent: () =>
          import('./suivi/suivi.component').then(m => m.SuiviComponent)
      },
      {
        path: 'profil',
        loadComponent: () =>
          import('./profil/profil.component').then(m => m.ProfilComponent)
      }
    ]
  }
];
