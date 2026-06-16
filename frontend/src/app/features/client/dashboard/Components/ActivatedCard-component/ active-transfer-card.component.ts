import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Transfer } from '../../dashboard.models';
import { DashboardService } from '../../dashboard.service';

@Component({
  selector: 'app-active-transfer-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './ active-transfer-card.component.html',
  styleUrls: ['./active-transfer-card.component.scss'],
})
export class ActiveTransferCardComponent implements OnInit {
  @Input() transfer!: Transfer;

  codeRevealed = false;
  revealedCode = '';
  isRevealing = false;

  constructor(private dashboardService: DashboardService, private router: Router) {}

  goToDetail(): void {
    if (this.transfer?.id) {
      this.router.navigate(['/client/transfert', this.transfer.id]);
    }
  }

  ngOnInit(): void {}

  get statusLabel(): string {
    switch (this.transfer?.status) {
      case 'pending': return 'En attente';
      case 'validated': return 'Validé';
      case 'to_withdraw': return 'À retirer';
      case 'paid': return 'Payé';
      case 'expired': return 'Expiré';
      default: return '';
    }
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

  get progressPercentage(): number {
    const steps = this.transfer?.steps ?? [];
    const completedOrCurrent = steps.filter(s => s.completed || s.current).length;
    return Math.round((completedOrCurrent / steps.length) * 100);
  }

  onRevealCode(): void {
    if (this.codeRevealed || this.isRevealing) return;
    this.isRevealing = true;
    this.dashboardService.revealWithdrawalCode(this.transfer.id).subscribe({
      next: (code) => {
        this.revealedCode = code;
        this.codeRevealed = true;
        this.isRevealing = false;
      },
      error: () => {
        this.isRevealing = false;
      },
    });
  }

  get displayCode(): string {
    return this.codeRevealed ? this.revealedCode : (this.transfer?.withdrawalCode ?? '');
  }
}