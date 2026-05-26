import { Routes } from '@angular/router';
import { RoleUtilisateur } from '../../core/models/enums';

export const managerRoutes: Routes = [
{
  path: 'plafond-journalier',
  loadComponent: () =>
    import('./plafond-journalier/ plafond-journalier.component')
      .then(m => m.PlafondJournalierComponent),
  data: { roles: [RoleUtilisateur.ROLE_MANAGER] }
},
{
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full'
}






];
import { ManagerLayoutComponent } from '../../layouts/manager-layout/manager-layout.component';

export const managerRoutes: Routes = [

  {
    path: '',
    component: ManagerLayoutComponent,
    children: [

      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },

      {
        path: 'dashboard',
        loadComponent: () =>
          import('./dashboard/dashboard.component')
            .then(m => m.DashboardComponent)
      },

      {
        path: 'agents',
        loadComponent: () =>
          import('./agents/agents.component')
            .then(m => m.AgentsComponent)
      },

      {
        path: 'caisse',
        loadComponent: () =>
          import('./caisse/caisse.component')
            .then(m => m.CaisseComponent)
      },

      {
        path: 'rapports-agence',
        loadComponent: () =>
          import('./rapports-agence/rapports-agence.component')
            .then(m => m.RapportsAgenceComponent)
      },

      {
        path: 'plafond',
        loadComponent: () =>
          import('./plafond/plafond.component')
            .then(m => m.PlafondComponent)
      }

    ]
  }

];
