import { Routes } from '@angular/router';

export const managerRoutes: Routes = [
  { 
    path: 'rapports', 
    loadComponent: () => import('./rapports-agence/ rapport-agence.component').then(m => m.RapportAgenceComponent) 
  },
  
];
