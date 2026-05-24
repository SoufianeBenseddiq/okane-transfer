import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TopbarComponent } from '../../../shared/components/topbar/topbar.component';
import { IconComponent } from '../../../shared/components/icon/icon.component';

// ⚠️ TODO (équipe Agence): ajouter GET /api/agences/{id} dans AgenceController
// et GET /api/agences/{id}/agents pour charger les données dynamiquement.

const BARS = [62, 78, 91, 84, 72, 58, 38];
const maxBar = Math.max(...BARS);

@Component({
  selector: 'app-agence-detail',
  standalone: true,
  host: { class: 'flex flex-col flex-1 min-w-0 overflow-hidden' },
  imports: [TopbarComponent, IconComponent],
  templateUrl: './agence-detail.component.html',
})
export class AgenceDetailComponent implements OnInit {
  agenceId = 0;

  readonly bars = BARS.map((v, i) => ({
    label: ['Lun','Mar','Mer','Jeu','Ven','Sam','Dim'][i],
    height: Math.round((v / maxBar) * 130),
  }));

  readonly agents = [
    { name: 'Nadia El Fassi',   trf: 28, mt: '84 200 MAD', solde: '84 200 MAD', active: true  },
    { name: 'Youssef Amrani',   trf: 19, mt: '62 000 MAD', solde: '62 000 MAD', active: true  },
    { name: 'Hassan Benzekri',  trf: 0,  mt: '—',          solde: '0 MAD',      active: false },
    { name: 'Fatima Chraibi',   trf: 22, mt: '71 500 MAD', solde: '71 500 MAD', active: true  },
    { name: 'Karim Tazi',       trf: 12, mt: '46 200 MAD', solde: '46 200 MAD', active: true  },
  ];

  readonly recent = [
    { ref: 'TRF-0094821', exp: 'Mohamed Alaoui', ben: 'Aminata Diallo', amt: '2 000 MAD', status: 'EN_ATTENTE', time: '09:14' },
    { ref: 'TRF-0094820', exp: 'Yasmine Ouali',  ben: 'Mariam Bah',     amt: '1 500 MAD', status: 'PAYE',       time: '09:01' },
    { ref: 'TRF-0094819', exp: 'Idriss Bouhaja', ben: 'Salif Camara',   amt: '4 200 MAD', status: 'PAYE',       time: '08:48' },
    { ref: 'TRF-0094818', exp: 'Latifa Saïdi',   ben: 'Aïcha Konaté',   amt: '800 MAD',   status: 'PAYE',       time: '08:32' },
    { ref: 'TRF-0094817', exp: 'Brahim Lahlou',  ben: 'Boubacar Sow',   amt: '6 100 MAD', status: 'BLOQUE',     time: '08:11' },
    { ref: 'TRF-0094816', exp: 'Najat Tazi',     ben: 'Khady Fall',     amt: '1 200 MAD', status: 'PAYE',       time: '07:55' },
  ];

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.agenceId = Number(this.route.snapshot.paramMap.get('id'));
  }

  statusBg(s: string): string {
    return s === 'EN_ATTENTE' ? 'rgba(24,144,255,0.12)' :
           s === 'PAYE'       ? 'rgba(0,196,140,0.12)'  :
           s === 'BLOQUE'     ? 'rgba(250,173,20,0.16)'  :
                                'rgba(140,143,163,0.16)';
  }
  statusColor(s: string): string {
    return s === 'EN_ATTENTE' ? '#1890FF' :
           s === 'PAYE'       ? '#00C48C'  :
           s === 'BLOQUE'     ? '#FAAD14'  :
                                '#6E7286';
  }
  statusLabel(s: string): string {
    return s === 'EN_ATTENTE' ? 'En attente' :
           s === 'PAYE'       ? 'Payé'       :
           s === 'BLOQUE'     ? 'Bloqué'     :
                                s;
  }

  initials(name: string): string {
    return name.split(' ').map(s => s[0]).filter(Boolean).join('').slice(0, 2).toUpperCase();
  }
}
