import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Transfer } from '../../dashboard.models';
import { TransferItemCardComponent } from '../TransferCard-component/transfer-item-card.component';

@Component({
  selector: 'app-recent-transfers',
  standalone: true,
  imports: [CommonModule, TransferItemCardComponent],
  templateUrl: './ recent-transfers.component.html',
  styleUrls: ['./recent-transfers.component.scss'],
})
export class RecentTransfersComponent {
  @Input() transfers: Transfer[] = [];
  @Output() viewAllClicked = new EventEmitter<void>();
}