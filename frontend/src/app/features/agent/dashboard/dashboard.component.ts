import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';

interface Operation {
  time: string;
  type: 'ENVOI' | 'RETRAIT';
  ref: string;
  client: string;
  amount: string;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, TranslateModule],
  templateUrl: './dashboard.component.html',
})
export class DashboardComponent implements OnInit {

  stats = {
    transfersCount: 12,
    sends: 5,
    withdrawals: 7,
    totalAmount: '46 200 MAD',
    commissions: '980 MAD',
  };

  operations: Operation[] = [
    { time: '15:42', type: 'RETRAIT', ref: 'TRF-0094788', client: 'Aminata Diallo',  amount: '- 215 400 XOF' },
    { time: '15:18', type: 'ENVOI',   ref: 'TRF-0094828', client: 'Yasmine Ouali',   amount: '+ 1 500 MAD'   },
    { time: '14:55', type: 'ENVOI',   ref: 'TRF-0094819', client: 'Idriss Bouhaja',  amount: '+ 4 200 MAD'   },
    { time: '14:32', type: 'RETRAIT', ref: 'TRF-0094602', client: 'Aïcha Konaté',    amount: '- 85 220 XOF'  },
    { time: '13:48', type: 'ENVOI',   ref: 'TRF-0094818', client: 'Latifa Saïdi',    amount: '+ 800 MAD'     },
    { time: '13:22', type: 'ENVOI',   ref: 'TRF-0094817', client: 'Brahim Lahlou',   amount: '+ 6 100 MAD'   },
    { time: '12:55', type: 'RETRAIT', ref: 'TRF-0094521', client: 'Khady Fall',      amount: '- 129 240 XOF' },
    { time: '12:14', type: 'ENVOI',   ref: 'TRF-0094816', client: 'Najat Tazi',      amount: '+ 1 200 MAD'   },
  ];

  ngOnInit(): void {}
}
