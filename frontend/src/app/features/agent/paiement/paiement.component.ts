import { Component } from '@angular/core';
import { CommonModule, DatePipe, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';
import { TransfertService } from '../../../core/services/transfert.service';
import { AuthService } from '../../../core/services/auth.service';
import { TransfertResponse } from '../../../core/models/transfert';
import {TypePiece} from "../../../core/models/enums";

@Component({
  selector: 'app-paiement',
  standalone: true,
  imports: [CommonModule, FormsModule, TranslateModule, DatePipe, DecimalPipe],
  templateUrl: './paiement.component.html',
})
export class PaiementComponent {

  searchMode: 'code' | 'phone' = 'code';
  searchValue  = '';
  isValidFormat = false;
  isLoading     = false;
  errorMessage: string | null = null;

  transfer: TransfertResponse | null = null;

  idType: TypePiece = TypePiece.CIN;
  idNumber = '';

  typesPiece = Object.values(TypePiece);

  constructor(
    private transfertService: TransfertService,
    private authService: AuthService,
  ) {}

  // ── Onglets ────────────────────────────────────────────────────────────────

  setSearchMode(mode: 'code' | 'phone'): void {
    this.searchMode   = mode;
    this.searchValue  = '';
    this.isValidFormat = false;
    this.transfer      = null;
    this.errorMessage  = null;
  }

  // ── Validation du format de saisie ────────────────────────────────────────

  onInputChange(): void {
    const v = this.searchValue.trim().toUpperCase();
    if (this.searchMode === 'code') {
      // format XXXX-XXXX (8 alphanumériques + tiret)
      this.isValidFormat = /^[A-Z0-9]{4}-[A-Z0-9]{4}$/.test(v);
    } else {
      // numéro international : commence par + optionnel, chiffres et espaces, 8-15 chiffres
      this.isValidFormat = /^\+?[\d\s]{8,15}$/.test(v);
    }
  }

  // ── Recherche ──────────────────────────────────────────────────────────────

  search(): void {
    if (!this.isValidFormat) return;

    this.isLoading    = true;
    this.transfer      = null;
    this.errorMessage  = null;

    const value = this.searchValue.trim();

    const appel$ = this.searchMode === 'code'
      ? this.transfertService.getByCodeRetrait(value.toUpperCase())
      : this.transfertService.getByTelephoneBeneficiaire(value);

    appel$.subscribe({
      next: (data) => {
        this.transfer  = data;
        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading    = false;
        this.errorMessage = err.status === 404
          ? 'payment.notFound'       // clé i18n
          : 'payment.errorGeneric';
      },
    });
  }

  // ── Actions ────────────────────────────────────────────────────────────────

  cancel(): void {
    this.transfer      = null;
    this.searchValue   = '';
    this.isValidFormat = false;
    this.idNumber      = '';
    this.errorMessage  = null;
  }

  printSlip(): void {
    window.print();
  }


  // confirmPayment(): void {
  //   if (!this.idNumber || !this.transfer) return;
  //
  //   this.isLoading = true;
  //
  //   this.transfertService.payer({
  //     codeRetrait:             this.transfer.codeRetrait,
  //     agenceRetraitId:         this.agenceRetraitId,
  //     typePieceBeneficiaire:   this.idType as TypePiece,
  //     numeroPieceBeneficiaire: this.idNumber,
  //   }).subscribe({
  //     next: (transfertPaye) => {
  //       this.isLoading = false;
  //       this.transfer  = transfertPaye;
  //       alert(`Paiement confirmé — ${transfertPaye.numeroReference}`);
  //     },
  //     error: (err) => {
  //       this.isLoading    = false;
  //       this.errorMessage = err.status === 409
  //         ? 'payment.alreadyPaid'
  //         : 'payment.errorGeneric';
  //     },
  //   });
  // }
  // Dans paiement.component.ts
// Remplace confirmPayment() par cette version

  confirmPayment(): void {
    if (!this.idNumber || !this.transfer) return;

    this.isLoading = true;

    const currentUser = this.authService.currentUser;

    this.transfertService.payer({
      codeRetrait:             this.transfer.codeRetrait,
      agentId:                 currentUser?.id,
      agenceRetraitId:         currentUser?.agenceId ?? 1,
      typePieceBeneficiaire:   this.idType as TypePiece,
      numeroPieceBeneficiaire: this.idNumber,
    }).subscribe({
      next: (transfertPaye) => {
        this.isLoading = false;
        this.transfer  = transfertPaye;

        // Ouvrir le reçu dans une nouvelle fenêtre
        this.ouvrirRecu(transfertPaye);
      },
      error: (err) => {
        this.isLoading = false;
        const msg: string = err?.error?.message ?? '';
        if (err.status === 409) {
          this.errorMessage = 'payment.alreadyPaid';
        } else if (msg.toLowerCase().includes('caisse') && msg.toLowerCase().includes('ouvert')) {
          this.errorMessage = 'payment.caisseNonOuverte';
        } else if (msg.toLowerCase().includes('insuffisant') || msg.toLowerCase().includes('solde')) {
          this.errorMessage = 'payment.soldeInsuffisant';
        } else {
          this.errorMessage = 'payment.errorGeneric';
        }
      },
    });
  }

// Ajoute cette méthode dans la classe
  public ouvrirRecu(transfer: TransfertResponse): void {
    // Sérialise le transfert en query params via sessionStorage
    sessionStorage.setItem('recu_transfer',   JSON.stringify(transfer));
    sessionStorage.setItem('recu_typePiece',  this.idType);
    sessionStorage.setItem('recu_numeroPiece', this.idNumber);

    // Ouvre une nouvelle fenêtre sur la route du reçu
    window.open('/recu-paiement', '_blank', 'width=800,height=900');
  }

  get statutStyle(): { label: string; classes: string } {
    const styles: Record<string, { label: string; classes: string }> = {
      EN_ATTENTE: {
        label: 'En attente',
        classes: 'text-amber-400 bg-[#2a2010] border-amber-500/30'
      },
      PAYE: {
        label: 'Payé',
        classes: 'text-emerald-400 bg-emerald-500/10 border-emerald-500/30'
      },
      ANNULE: {
        label: 'Annulé',
        classes: 'text-red-400 bg-red-500/10 border-red-500/30'
      },
      EXPIRE: {
        label: 'Expiré',
        classes: 'text-gray-400 bg-gray-500/10 border-gray-500/30'
      },
      BLOQUE: {
        label: 'Bloqué',
        classes: 'text-purple-400 bg-purple-500/10 border-purple-500/30'
      },
    };
    return styles[this.transfer?.statut ?? ''] ?? styles['EN_ATTENTE'];
  }


  // ── Helpers template ───────────────────────────────────────────────────────

  /** Affiche "2 000 MAD envoyés" à partir de montantEnvoye */
  get madAmountLabel(): string {
    if (!this.transfer) return '';
    const formatted = new Intl.NumberFormat('fr-MA').format(this.transfer.montantEnvoye);
    return `${formatted} MAD envoyés`;
  }
}
// import { Component } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { FormsModule } from '@angular/forms';
// import { TranslateModule } from '@ngx-translate/core';
//
// interface Transfer {
//   ref: string;
//   senderName: string;
//   senderLocation: string;
//   benefName: string;
//   benefLocation: string;
//   benefPhone: string;
//   sentDate: string;
//   agency: string;
//   expiryDate: string;
//   amount: number;
//   currency: string;
//   madAmount: string;
//   rate: number;
// }
//
// @Component({
//   selector: 'app-paiement',
//   standalone: true,
//   imports: [CommonModule, FormsModule, TranslateModule],
//   templateUrl: './paiement.component.html',
// })
// export class PaiementComponent {
//
//   searchMode: 'code' | 'phone' = 'code';
//   searchValue = '';
//   isValidFormat = false;
//   isLoading = false;
//   transfer: Transfer | null = null;
//
//   idType = 'CIN';
//   idNumber = '';
//
//   // Mock transfer — remplacer par un vrai appel API
//   private mockTransfer: Transfer = {
//     ref: 'TRF-2026-0894821',
//     senderName: 'Mohamed Alaoui',
//     senderLocation: 'Casablanca, Maroc',
//     benefName: 'Aminata Diallo',
//     benefLocation: 'Dakar',
//     benefPhone: '+221 77 412 65 09',
//     sentDate: '22 / 05 / 2026 · 09:14',
//     agency: 'Agence Casablanca Maârif',
//     expiryDate: '22 / 06 / 2026',
//     amount: 215400,
//     currency: 'XOF',
//     madAmount: '2 000 MAD envoyés',
//     rate: 107.7,
//   };
//
//   setSearchMode(mode: 'code' | 'phone'): void {
//     this.searchMode = mode;
//     this.searchValue = '';
//     this.isValidFormat = false;
//     this.transfer = null;
//   }
//
//   onInputChange(): void {
//     const v = this.searchValue.trim().toUpperCase();
//     if (this.searchMode === 'code') {
//       // format XXXX-XXXX (8 alphanum + tiret)
//       this.isValidFormat = /^[A-Z0-9]{4}-[A-Z0-9]{4}$/.test(v);
//     } else {
//       // format téléphone international simple
//       this.isValidFormat = /^\+?[\d\s]{8,15}$/.test(v);
//     }
//   }
//
//   search(): void {
//     if (!this.isValidFormat) return;
//     this.isLoading = true;
//     this.transfer = null;
//
//     // Simuler appel API
//     setTimeout(() => {
//       this.transfer = this.mockTransfer;
//       this.isLoading = false;
//     }, 800);
//   }
//
//   cancel(): void {
//     this.transfer = null;
//     this.searchValue = '';
//     this.isValidFormat = false;
//     this.idNumber = '';
//   }
//
//   printSlip(): void {
//     window.print();
//   }
//
//   confirmPayment(): void {
//     if (!this.idNumber || !this.transfer) return;
//     // TODO: appel API confirmation
//     alert(`Paiement confirmé — ${this.transfer.ref}`);
//     this.cancel();
//   }
// }
