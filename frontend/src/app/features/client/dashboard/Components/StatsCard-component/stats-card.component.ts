import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-stats-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './ stats-card.component.html',
  styleUrls: ['./stats-card.component.scss'],
})
export class StatsCardComponent {
  @Input() sentThisMonth: number = 0;
  @Input() currency: string = 'MAD';
  @Input() changeVsLastMonth: number = 0;

  get isPositive(): boolean {
    return this.changeVsLastMonth >= 0;
  }

  get changeLabel(): string {
    const sign = this.isPositive ? '+' : '-';
    const abs = Math.abs(this.changeVsLastMonth);
    return `${sign} ${abs.toLocaleString('fr-MA')} ${this.currency} vs mois dernier`;
  }
}