import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-cloture-caisse',
  standalone: true,
  imports: [CommonModule, FormsModule, TranslateModule],
  templateUrl: './cloture-caisse.component.html',
})
export class ClotureCaisseComponent {

  today = new Date();

  stats = {
    envois:      8,
    retraits:    4,
    entrants:    '62 400',
    sortants:    '28 200',
    theoretique: 84200,
  };

  realBalance: number | null = null;
  gap: number | null = null;
  note = '';

  constructor(private router: Router) {}

  computeGap(): void {
    if (this.realBalance === null || this.realBalance === undefined || isNaN(Number(this.realBalance))) {
      this.gap = null;
      return;
    }
    this.gap = Number(this.realBalance) - this.stats.theoretique;
  }

  get canValidate(): boolean {
    if (this.gap === null) return false;
    if (this.gap !== 0 && !this.note.trim()) return false;
    return true;
  }

  cancel(): void {
    this.router.navigate(['/agent/caisse']);
  }

  validate(): void {
    if (!this.canValidate) return;
    // TODO : appel API clôture
    this.router.navigate(['/agent/dashboard']);
  }
}
