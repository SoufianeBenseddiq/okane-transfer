import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Transfer, TransferStatus } from '../../dashboard.models';

@Component({
  selector: 'app-transfer-item-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './transfer-item-card.component.html',
  styleUrls: ['./transfer-item-card.component.scss'],
})
export class TransferItemCardComponent {
  @Input() transfer!: Transfer;

  constructor(private router: Router) {}

  goToDetail(): void {
    if (this.transfer?.id) {
      this.router.navigate(['/client/transfert', this.transfer.id]);
    }
  }

  get statusLabel(): string {
    const labels: Record<TransferStatus, string> = {
      pending: 'En attente',
      validated: 'Validé',
      to_withdraw: 'À retirer',
      paid: 'Payé',
      expired: 'Expiré',
    };
    return labels[this.transfer?.status] ?? '';
  }

  get statusClass(): string {
    switch (this.transfer?.status) {
      case 'pending':
      case 'to_withdraw': return 'status-pending';
      case 'validated': return 'status-validated';
      case 'paid': return 'status-paid';
      case 'expired': return 'status-expired';
      default: return '';
    }
  }

  get formattedDate(): string {
    const d = this.transfer?.createdAt;
    if (!d) return '';
    const today = new Date();
    const isToday =
      d.getDate() === today.getDate() &&
      d.getMonth() === today.getMonth() &&
      d.getFullYear() === today.getFullYear();

    const time = d.toTimeString().slice(0, 5);
    const day = isToday
      ? "Aujourd'hui"
      : `${d.getDate().toString().padStart(2, '0')} ${d.toLocaleString('fr-FR', { month: 'short' })}`;
    return `${day} · ${time}`;
  }

  get convertedDisplay(): string {
    const t = this.transfer;
    if (!t) return '';
    return `– ${t.convertedAmount.toLocaleString('fr-MA')} ${t.convertedCurrency}`;
  }
}