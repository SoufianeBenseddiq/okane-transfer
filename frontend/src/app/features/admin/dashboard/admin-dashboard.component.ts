import { Component } from '@angular/core';
import { TopbarComponent } from '../../../shared/components/topbar/topbar.component';
import { IconComponent } from '../../../shared/components/icon/icon.component';

interface BarItem { label: string; value: number; height: number; }

const BAR_DATA = [
  { label: 'MAR→SEN', value: 184 },
  { label: 'MAR→FRA', value: 142 },
  { label: 'MAR→CIV', value: 108 },
  { label: 'MAR→BEL', value: 96  },
  { label: 'FRA→MAR', value: 88  },
  { label: 'MAR→MLI', value: 72  },
  { label: 'MAR→BEN', value: 54  },
];

const max = Math.max(...BAR_DATA.map(d => d.value));

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  host: { class: 'flex flex-col flex-1 min-w-0 overflow-hidden' },
  imports: [TopbarComponent, IconComponent],
  templateUrl: './admin-dashboard.component.html',
})
export class AdminDashboardComponent {
  readonly bars: BarItem[] = BAR_DATA.map(d => ({ ...d, height: Math.round((d.value / max) * 120) }));

  readonly alerts = [
    { date: '22/05 · 14:32', client: 'A. Diallo',      montant: '14 800 MAD', regle: 'Cumul > 30k / 7j',             pending: true  },
    { date: '22/05 · 11:18', client: 'M. El Idrissi',  montant: '9 200 MAD',  regle: 'Structuring (4 op. < 5k)',      pending: true  },
    { date: '22/05 · 09:04', client: 'K. Touré',       montant: '22 500 MAD', regle: 'Bénéf. liste OFAC',             pending: true  },
    { date: '21/05 · 17:55', client: 'F. Belkacem',    montant: '6 700 MAD',  regle: 'Pays à risque',                 pending: false },
    { date: '21/05 · 16:22', client: 'O. Sané',        montant: '11 300 MAD', regle: 'Velocity 3 op. / 2h',           pending: false },
  ];
}
