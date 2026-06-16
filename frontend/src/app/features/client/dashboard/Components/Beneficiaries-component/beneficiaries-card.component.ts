import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Beneficiary } from '../../dashboard.models';

@Component({
  selector: 'app-beneficiaries-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './beneficiaries-card.component.html',
  styleUrls: ['./beneficiaries-card.component.scss'],
})
export class BeneficiariesCardComponent {
  @Input() count: number = 0;
  @Input() beneficiaries: Beneficiary[] = [];
  @Output() manageBeneficiaries = new EventEmitter<void>();

  /** Avatar background colors cycling through a palette */
  readonly avatarColors = ['#f59e0b', '#3b82f6', '#10b981', '#8b5cf6', '#ef4444'];

  getAvatarColor(index: number): string {
    return this.avatarColors[index % this.avatarColors.length];
  }
}