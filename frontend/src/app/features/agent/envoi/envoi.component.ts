import { Component, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';

interface Client {
  id: number;
  nom: string;
  prenom: string;
  cin: string;
  telephone: string;
  pays: string;
  ville: string;
  nbTransferts: number;
  clientDepuis: string;
  kycOk: boolean;
  email: string;
}

interface Beneficiaire {
  nom: string;
  prenom: string;
  telephone: string;
  paysReception: string;
  ville: string;
  relation: string;
}

interface CalculFrais {
  montantEnvoye: number;
  deviseSource: string;
  taux: number;
  deviseDestination: string;
  frais: number;
  commissionAgence: number;
  montantRecu: number;
  delaiMin: number;
}

@Component({
  selector: 'app-envoi',
  standalone: true,
  imports: [CommonModule, FormsModule, TranslateModule],
  templateUrl: './envoi.component.html',
})
export class EnvoiComponent {

  etapeActive = signal<number>(1);
  etapes = [
    { num: 1, label: 'envoi.step1Label' },
    { num: 2, label: 'envoi.step2Label' },
    { num: 3, label: 'envoi.step3Label' },
    { num: 4, label: 'envoi.step4Label' },
  ];

  searchQuery = signal<string>('');
  clientsTrouves = signal<Client[]>([]);
  clientSelectionne = signal<Client | null>(null);
  rechercheEffectuee = signal<boolean>(false);

  nouveauClient = {
    nom: '', prenom: '', typePiece: 'CIN',
    numeroPiece: '', telephone: '', pays: 'Maroc',
  };

  typesPiece = ['CIN', 'Passeport', 'Titre de sejour', 'Carte de resident'];
  paysList = ['Maroc', 'France', 'Belgique', 'Espagne', 'Italie', 'Allemagne', 'Pays-Bas'];

  ofacOk = signal<boolean>(true);
  beneficiaire: Beneficiaire = {
    nom: '', prenom: '', telephone: '',
    paysReception: 'Senegal', ville: '', relation: '',
  };
  paysReceptionList = ['Senegal', 'Cote Ivoire', 'Mali', 'Guinee', 'Cameroun', 'Congo'];
  relationsList = ['Famille', 'Conjoint(e)', 'Parent', 'Enfant', 'Soeur', 'Frere', 'Ami(e)', 'Autre'];

  montantEnvoye = signal<number>(2000);
  corridorSelectionne = signal<string>('MAD -> XOF');
  modeReception = signal<string>('Cash au guichet');
  corridors = ['MAD -> XOF', 'MAD -> EUR', 'MAD -> GNF', 'MAD -> XAF'];
  modesReception = ['Cash au guichet', 'Mobile Money', 'Virement bancaire'];

  calcul = computed<CalculFrais>(() => ({
    montantEnvoye: this.montantEnvoye(),
    deviseSource: 'MAD',
    taux: 107.7,
    deviseDestination: 'XOF',
    frais: 35.00,
    commissionAgence: 14.00,
    montantRecu: Math.round((this.montantEnvoye() - 35) * 107.7),
    delaiMin: 5,
  }));

  expediteurNom = computed(() => {
    const c = this.clientSelectionne();
    return c ? c.prenom + ' ' + c.nom : null;
  });

  beneficiaireNom = computed(() => {
    const b = this.beneficiaire;
    return b.nom && b.prenom ? b.prenom + ' ' + b.nom : null;
  });

  corridorNom = computed(() => {
    return this.etapeActive() >= 3 ? this.corridorSelectionne() : null;
  });

  codeRetrait = signal<string>('K7X2-M9QA');
  referenceTransfert = signal<string>('TRF-2026-8094821');
  codeCopie = signal<boolean>(false);

  onRechercher(): void {
    const q = this.searchQuery().trim();
    if (!q) return;
    this.rechercheEffectuee.set(true);
    if (q.toUpperCase().includes('EE5847291') || q.toLowerCase().includes('alaoui')) {
      this.clientsTrouves.set([{
        id: 1, nom: 'Alaoui', prenom: 'Mohamed',
        cin: 'EE 5847291', telephone: '+212 6 12 34 56 78',
        pays: 'Maroc', ville: 'Casablanca',
        nbTransferts: 22, clientDepuis: '03/2023',
        kycOk: true, email: 'mohamed.alaoui@gmail.com',
      }]);
    } else {
      this.clientsTrouves.set([]);
    }
  }

  onSearchKeyDown(e: KeyboardEvent): void {
    if (e.key === 'Enter') this.onRechercher();
  }

  onSelectionnerClient(client: Client): void {
    this.clientSelectionne.set(client);
  }

  isEtapeComplete(num: number): boolean { return num < this.etapeActive(); }
  isEtapeActive(num: number): boolean { return num === this.etapeActive(); }
  etapeSuivante(): void { if (this.etapeActive() < 4) this.etapeActive.update(v => v + 1); }
  etapePrecedente(): void { if (this.etapeActive() > 1) this.etapeActive.update(v => v - 1); }

  copierCode(): void {
    navigator.clipboard.writeText(this.codeRetrait()).then(() => {
      this.codeCopie.set(true);
      setTimeout(() => this.codeCopie.set(false), 2000);
    });
  }

  nouveauTransfert(): void {
    this.etapeActive.set(1);
    this.clientSelectionne.set(null);
    this.rechercheEffectuee.set(false);
    this.clientsTrouves.set([]);
    this.searchQuery.set('');
    this.beneficiaire = { nom: '', prenom: '', telephone: '', paysReception: 'Senegal', ville: '', relation: '' };
    this.montantEnvoye.set(2000);
  }

  formatMontant(n: number): string {
    return new Intl.NumberFormat('fr-FR').format(n);
  }

  getExpirationDate(): string {
    const d = new Date();
    d.setDate(d.getDate() + 30);
    return d.toLocaleDateString('fr-FR');
  }
}