import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';

interface Transfer {
  ref: string;
  senderName: string;
  senderLocation: string;
  benefName: string;
  benefLocation: string;
  benefPhone: string;
  sentDate: string;
  agency: string;
  expiryDate: string;
  amount: number;
  currency: string;
  madAmount: string;
  rate: number;
}

@Component({
  selector: 'app-paiement',
  standalone: true,
  imports: [CommonModule, FormsModule, TranslateModule],
  templateUrl: './paiement.component.html',
})
export class PaiementComponent {

  searchMode: 'code' | 'phone' = 'code';
  searchValue = '';
  isValidFormat = false;
  isLoading = false;
  transfer: Transfer | null = null;

  idType = 'CIN';
  idNumber = '';

  // Mock transfer — remplacer par un vrai appel API
  private mockTransfer: Transfer = {
    ref: 'TRF-2026-0894821',
    senderName: 'Mohamed Alaoui',
    senderLocation: 'Casablanca, Maroc',
    benefName: 'Aminata Diallo',
    benefLocation: 'Dakar',
    benefPhone: '+221 77 412 65 09',
    sentDate: '22 / 05 / 2026 · 09:14',
    agency: 'Agence Casablanca Maârif',
    expiryDate: '22 / 06 / 2026',
    amount: 215400,
    currency: 'XOF',
    madAmount: '2 000 MAD envoyés',
    rate: 107.7,
  };

  setSearchMode(mode: 'code' | 'phone'): void {
    this.searchMode = mode;
    this.searchValue = '';
    this.isValidFormat = false;
    this.transfer = null;
  }

  onInputChange(): void {
    const v = this.searchValue.trim().toUpperCase();
    if (this.searchMode === 'code') {
      // format XXXX-XXXX (8 alphanum + tiret)
      this.isValidFormat = /^[A-Z0-9]{4}-[A-Z0-9]{4}$/.test(v);
    } else {
      // format téléphone international simple
      this.isValidFormat = /^\+?[\d\s]{8,15}$/.test(v);
    }
  }

  search(): void {
    if (!this.isValidFormat) return;
    this.isLoading = true;
    this.transfer = null;

    // Simuler appel API
    setTimeout(() => {
      this.transfer = this.mockTransfer;
      this.isLoading = false;
    }, 800);
  }

  cancel(): void {
    this.transfer = null;
    this.searchValue = '';
    this.isValidFormat = false;
    this.idNumber = '';
  }

  printSlip(): void {
    window.print();
  }

  confirmPayment(): void {
    if (!this.idNumber || !this.transfer) return;
    // TODO: appel API confirmation
    alert(`Paiement confirmé — ${this.transfer.ref}`);
    this.cancel();
  }
}
