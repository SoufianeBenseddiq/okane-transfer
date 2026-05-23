import {Routes} from "@angular/router";

export const routes: Routes = [
  {
    path: 'agent',
    loadComponent: () => import('./layouts/agent-layout/agent-layout.component').then(m => m.AgentLayoutComponent),
    children: [
      { path: 'dashboard', loadComponent: () => import('./features/agent/dashboard/dashboard.component').then(m => m.DashboardComponent) },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
    ]
  },
  // ── AJOUTEZ CETTE LIGNE ICI POUR CORRIGER LA RACINE ────────────────
  { path: '', redirectTo: 'agent/dashboard', pathMatch: 'full' },
  // Optionnel : gérer les pages introuvables 404 sans planter
  { path: '**', redirectTo: 'agent/dashboard' }
];
