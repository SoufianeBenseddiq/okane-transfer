import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';

interface Movement {
  time: string;
  type: 'envoi' | 'retrait';
  ref: string;
  client: string;
  amount: string;
}

@Component({
  selector: 'app-caisse',
  standalone: true,
  imports: [CommonModule, TranslateModule],
  templateUrl: './caisse.component.html',
})
export class CaisseComponent {

  cashStats = {
    envoiOps: 8,
    retraitOps: 4,
  };

  movements: Movement[] = [
    { time: '15:42', type: 'retrait', ref: 'TRF-0094788', client: 'Aminata Diallo',  amount: '-2 000'  },
    { time: '15:18', type: 'envoi',   ref: 'TRF-0094828', client: 'Yasmine Ouali',   amount: '+1 500'  },
    { time: '14:55', type: 'envoi',   ref: 'TRF-0094819', client: 'Idriss Bouhaja',  amount: '+4 200'  },
    { time: '14:32', type: 'retrait', ref: 'TRF-0094602', client: 'Aïcha Konaté',    amount: '-800'    },
    { time: '13:48', type: 'envoi',   ref: 'TRF-0094818', client: 'Latifa Saïdi',    amount: '+800'    },
    { time: '13:22', type: 'envoi',   ref: 'TRF-0094817', client: 'Brahim Lahlou',   amount: '+6 100'  },
    { time: '12:55', type: 'retrait', ref: 'TRF-0094521', client: 'Khady Fall',      amount: '-1 200'  },
    { time: '12:14', type: 'envoi',   ref: 'TRF-0094816', client: 'Najat Tazi',      amount: '+1 200'  },
    { time: '11:42', type: 'envoi',   ref: 'TRF-0094815', client: 'Ahmed Idrissi',   amount: '+3 500'  },
  ];
}
