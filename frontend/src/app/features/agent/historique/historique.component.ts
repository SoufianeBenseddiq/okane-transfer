import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';

interface Operation {
  time: string;
  date: string;
  type: 'ENVOI' | 'RETRAIT';
  ref: string;
  client: string;
  destination: string;
  amount: string;
}

@Component({
  selector: 'app-historique',
  standalone: true,
  imports: [CommonModule, FormsModule, TranslateModule],
  templateUrl: './historique.component.html',
})
export class HistoriqueComponent implements OnInit {

  searchQuery = '';
  filterType = 'all';
  filterDate = 'today';
  currentPage = 1;

  operations: Operation[] = [
    { time: '15:42', date: '23/05/2026', type: 'RETRAIT', ref: 'TRF-0094788', client: 'Aminata Diallo',  destination: 'Dakar, Sénégal',      amount: '2 000 MAD'   },
    { time: '15:18', date: '23/05/2026', type: 'ENVOI',   ref: 'TRF-0094828', client: 'Yasmine Ouali',   destination: 'Tunis, Tunisie',       amount: '1 500 MAD'   },
    { time: '14:55', date: '23/05/2026', type: 'ENVOI',   ref: 'TRF-0094819', client: 'Idriss Bouhaja',  destination: 'Paris, France',        amount: '4 200 MAD'   },
    { time: '14:32', date: '23/05/2026', type: 'RETRAIT', ref: 'TRF-0094602', client: 'Aïcha Konaté',    destination: 'Abidjan, Côte d\'Ivoire', amount: '800 MAD'  },
    { time: '13:48', date: '23/05/2026', type: 'ENVOI',   ref: 'TRF-0094818', client: 'Latifa Saïdi',    destination: 'Lyon, France',         amount: '800 MAD'     },
    { time: '13:22', date: '23/05/2026', type: 'ENVOI',   ref: 'TRF-0094817', client: 'Brahim Lahlou',   destination: 'Montréal, Canada',     amount: '6 100 MAD'   },
    { time: '12:55', date: '23/05/2026', type: 'RETRAIT', ref: 'TRF-0094521', client: 'Khady Fall',      destination: 'Dakar, Sénégal',       amount: '1 200 MAD'   },
    { time: '12:14', date: '23/05/2026', type: 'ENVOI',   ref: 'TRF-0094816', client: 'Najat Tazi',      destination: 'Madrid, Espagne',      amount: '1 200 MAD'   },
    { time: '11:42', date: '23/05/2026', type: 'ENVOI',   ref: 'TRF-0094815', client: 'Ahmed Idrissi',   destination: 'Bruxelles, Belgique',  amount: '3 500 MAD'   },
    { time: '11:05', date: '23/05/2026', type: 'RETRAIT', ref: 'TRF-0094510', client: 'Mariam Traoré',   destination: 'Bamako, Mali',         amount: '950 MAD'     },
    { time: '10:30', date: '23/05/2026', type: 'ENVOI',   ref: 'TRF-0094809', client: 'Omar Benjelloun', destination: 'Amsterdam, Pays-Bas',  amount: '2 800 MAD'   },
    { time: '09:14', date: '23/05/2026', type: 'ENVOI',   ref: 'TRF-0094801', client: 'Mohamed Alaoui',  destination: 'Dakar, Sénégal',       amount: '2 000 MAD'   },
  ];

  filtered: Operation[] = [];
  pages: number[] = [1, 2, 3];

  get envoiCount(): number {
    return this.filtered.filter(o => o.type === 'ENVOI').length;
  }

  get retraitCount(): number {
    return this.filtered.filter(o => o.type === 'RETRAIT').length;
  }

  ngOnInit(): void {
    this.applyFilters();
  }

  applyFilters(): void {
    let result = [...this.operations];

    if (this.filterType !== 'all') {
      result = result.filter(o => o.type === this.filterType);
    }

    if (this.searchQuery.trim()) {
      const q = this.searchQuery.toLowerCase();
      result = result.filter(o =>
        o.client.toLowerCase().includes(q) ||
        o.ref.toLowerCase().includes(q) ||
        o.destination.toLowerCase().includes(q)
      );
    }

    this.filtered = result;
  }
}
