import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe, DecimalPipe } from '@angular/common';
import { TransfertResponse } from '../../../core/models/transfert';

@Component({
  selector: 'app-recu-paiement',
  standalone: true,
  imports: [CommonModule, DatePipe, DecimalPipe],
  templateUrl: './recu-paiement.component.html',
  styleUrls: ['./recu-paiement.component.scss'],
})
export class RecuPaiementComponent implements OnInit {

  transfer!: TransfertResponse;
  typePiece  = '';
  numeroPiece = '';
  printDate  = new Date();

  ngOnInit(): void {
    // Lire les données depuis sessionStorage
    const raw = sessionStorage.getItem('recu_transfer');
    if (raw) {
      this.transfer    = JSON.parse(raw);
      this.typePiece   = sessionStorage.getItem('recu_typePiece')   ?? '';
      this.numeroPiece = sessionStorage.getItem('recu_numeroPiece') ?? '';

      // Nettoyer après lecture
      sessionStorage.removeItem('recu_transfer');
      sessionStorage.removeItem('recu_typePiece');
      sessionStorage.removeItem('recu_numeroPiece');
    }

  }

  close(): void {
    window.close();
  }

  downloadPdf(): void {
    window.print(); // Le navigateur propose "Enregistrer en PDF" dans la boîte d'impression
  }
}
