import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-welcome-banner',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './welcome-banner.component.html',
  styleUrls: ['./welcome-banner.component.scss'],
})
export class WelcomeBannerComponent {
  @Input() userName: string = '';
  @Output() sendMoneyClicked = new EventEmitter<void>();
  @Output() historyClicked = new EventEmitter<void>();
}