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
