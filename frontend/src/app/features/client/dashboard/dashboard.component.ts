import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './dashboard.component.html',
  styles: [`
    .card { background: #fff; border-radius: 12px; border: 1px solid #eee; }
    .dark-card { background: #1a1f36; border-radius: 16px; color: white; }
    .badge { border-radius: 20px; font-size: 12px; padding: 3px 10px; font-weight: 500; }
    .badge-pending { background: #e8f4fd; color: #2196F3; }
    .badge-paid { background: #e8f8f0; color: #4CAF50; }
    .badge-expired { background: #fef2f2; color: #ef4444; }
    .btn-dark { background: #1a1f36; color: white; border: none; border-radius: 10px; padding: 10px 20px; font-weight: 600; cursor: pointer; display:flex; align-items:center; gap:8px; }
    .btn-orange { background: #F5A623; color: white; border: none; border-radius: 10px; padding: 10px 20px; font-weight: 600; cursor: pointer; display:flex; align-items:center; gap:8px; }
    .step-line { height: 3px; flex: 1; background: #F5A623; }
    .step-line-inactive { height: 3px; flex: 1; background: #444; }
    .step-dot { width: 22px; height: 22px; border-radius: 50%; display:flex; align-items:center; justify-content:center; font-size:11px; }
    .step-dot-done { background: #F5A623; }
    .step-dot-active { background: #F5A623; border: 3px solid #fff; box-shadow: 0 0 0 2px #F5A623; }
    .step-dot-inactive { background: #444; }
    .avatar { width:32px; height:32px; border-radius:50%; display:flex; align-items:center; justify-content:center; color:white; font-size:12px; font-weight:700; border:2px solid white; margin-left:-8px; }
    .transfer-card { background:#fff; border-radius:12px; border:1px solid #eee; padding:16px; flex:1; min-width:0; }
    .okanebot { position:fixed; bottom:24px; right:24px; background:#1a1f36; color:white; border-radius:50px; padding:12px 18px; display:flex; align-items:center; gap:10px; cursor:pointer; box-shadow: 0 4px 20px rgba(0,0,0,0.2); }
  `]
})
export class DashboardComponent {

  transferRecent = [
    { pays: '🇸🇳', paysNom: 'Sénégal',       beneficiaire: 'Aminata Diallo',  montant: '2 000 MAD', converti: '215 400 XOF', date: "Aujourd'hui · 09:14", ref: '094821', statut: 'pending' },
    { pays: '🇨🇮', paysNom: "Côte d'Ivoire", beneficiaire: 'Kadiatou Touré',  montant: '1 500 MAD', converti: '161 600 XOF', date: '18 mai · 14:22',      ref: '094215', statut: 'paid'    },
    { pays: '🇸🇳', paysNom: 'Sénégal',       beneficiaire: 'Omar Sané',       montant: '900 MAD',   converti: '96 900 XOF',  date: '11 mai · 11:05',      ref: '093888', statut: 'paid'    },
    { pays: '🇫🇷', paysNom: 'France',         beneficiaire: 'Fatima Belkacem', montant: '2 000 MAD', converti: '184 €',       date: '02 mai · 17:48',      ref: '093102', statut: 'expired' },
  ];

  beneficiaires = [
    { initiales: 'AD', color: '#F5A623' },
    { initiales: 'KT', color: '#1a1f36' },
    { initiales: 'OS', color: '#4CAF50' },
    { initiales: 'FB', color: '#9c27b0' },
  ];

  codeVisible = false;

  toggleCode() {
    this.codeVisible = !this.codeVisible;
  }

  getStatutLabel(statut: string): string {
    return statut === 'pending' ? 'En attente' : statut === 'paid' ? 'Payé' : 'Expiré';
  }

  getStatutClass(statut: string): string {
    return statut === 'pending' ? 'badge-pending' : statut === 'paid' ? 'badge-paid' : 'badge-expired';
  }
}
