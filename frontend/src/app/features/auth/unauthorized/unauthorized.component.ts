import { Component } from '@angular/core';

@Component({
  selector: 'app-unauthorized',
  standalone: true,
  template: `
    <div style="text-align:center;padding:4rem">
      <h1>403 — Accès refusé</h1>
      <p>Vous n'avez pas les droits pour accéder à cette page.</p>
      <a href="/auth/login">Retour à la connexion</a>
    </div>
  `
})
export class UnauthorizedComponent {}
