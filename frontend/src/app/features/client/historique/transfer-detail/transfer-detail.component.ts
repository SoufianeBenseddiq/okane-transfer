import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';
import { ActivatedRoute, Router } from '@angular/router';
import { LucideAngularModule } from 'lucide-angular';
import {
  ArrowLeft, Check, Clock, Printer, Share2,
  Eye, EyeOff, Copy, MapPin, Phone, Calendar, Info, X,
  CheckCircle2, AlertCircle, XCircle, Lock, ShieldCheck
} from 'lucide-angular';
import jsPDF from 'jspdf';

import { TransfertService } from '../../../../core/services/transfert.service';
import { TransfertResponse } from '../../../../core/models/transfert/transfert-response.model';

@Component({
  selector: 'app-transfer-detail',
  standalone: true,
  imports: [
    CommonModule,
    TranslateModule,
    LucideAngularModule,
  ],
  templateUrl: './transfer-detail.component.html',
  styleUrls: ['./transfer-detail.component.scss']
})
export class TransferDetailComponent implements OnInit {
  @Input() transfert!: TransfertResponse;
  @Output() closeDetail = new EventEmitter<void>();

  loading = false;
  error: string | null = null;
  transfertId: string | null = null;
  codeVisible = false;

  /**
   * Jeu d'icônes Lucide exposé au template.
   * On lie ces références via `[img]="icons.XXX"` sur <lucide-angular>.
   */
  readonly icons = {
    ArrowLeft, Check, Clock, Printer, Share2,
    Eye, EyeOff, Copy, MapPin, Phone, Calendar, Info, X,
    CheckCircle2, AlertCircle, XCircle, Lock, ShieldCheck
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private transfertService: TransfertService
  ) {}

  ngOnInit(): void {
    this.transfertId = this.route.snapshot.paramMap.get('id');
    if (this.transfertId && !this.transfert) {
      this.loadTransfert();
    }
  }

  /** Charge le transfert via son ID */
  loadTransfert(): void {
    if (!this.transfertId) return;

    this.loading = true;
    this.error = null;
    this.transfertService.getById(Number(this.transfertId)).subscribe({
      next: (data: TransfertResponse) => {
        this.transfert = data;
        this.loading = false;
      },
      error: (err: any) => {
        console.error('Erreur lors du chargement du transfert', err);
        this.error = 'Erreur : Transfert non trouvé';
        this.loading = false;
      }
    });
  }

  /**
   * Retourne la classe CSS correspondant au statut du transfert.
   * Ces classes pilotent les couleurs du badge dans le template.
   */
  getStatutClass(): string {
    switch (this.transfert?.statut) {
      case 'EN_ATTENTE': return 'pending';
      case 'PAYE':       return 'paid';
      case 'ANNULE':     return 'cancelled';
      case 'EXPIRE':     return 'expired';
      case 'BLOQUE':     return 'blocked';
      default:           return 'default';
    }
  }

  /**
   * Retourne l'icône Lucide adaptée au statut courant.
   * Utilisée dans la carte "STATUT DU TRANSFERT".
   */
  getStatutIcon(): any {
    switch (this.transfert?.statut) {
      case 'PAYE':       return this.icons.CheckCircle2;
      case 'EN_ATTENTE': return this.icons.Clock;
      case 'ANNULE':     return this.icons.XCircle;
      case 'EXPIRE':     return this.icons.AlertCircle;
      case 'BLOQUE':     return this.icons.Lock;
      default:           return this.icons.Info;
    }
  }

  /** Clé i18n pour le label du statut */
  getStatutLabel(): string {
    return `enums.statut-transfert.${this.transfert?.statut}`;
  }

  /**
   * Construit la timeline de progression du transfert
   * (inspirée du dashboard : Créé → Validé → À retirer → Payé).
   * `done` = étape franchie, `active` = étape courante.
   */
  getSteps(): { key: string; label: string; done: boolean; active: boolean }[] {
    const order = ['CREE', 'VALIDE', 'A_RETIRER', 'PAYE'];
    const labels: Record<string, string> = {
      CREE: 'Créé',
      VALIDE: 'Validé',
      A_RETIRER: 'À retirer',
      PAYE: 'Payé',
    };

    // Statut courant projeté sur la timeline
    let currentIndex: number;
    switch (this.transfert?.statut) {
      case 'PAYE':       currentIndex = 3; break;
      case 'EN_ATTENTE': currentIndex = 2; break; // en cours de retrait
      case 'ANNULE':
      case 'EXPIRE':
      case 'BLOQUE':     currentIndex = 1; break; // validé puis interrompu
      default:           currentIndex = 0; break;
    }

    return order.map((key, i) => ({
      key,
      label: labels[key],
      done: i < currentIndex || (i === 3 && currentIndex === 3),
      active: i === currentIndex,
    }));
  }

  /**
   * Masque le code de retrait en remplaçant les caractères centraux
   * par des points, en gardant les 2 premiers et les 2 derniers.
   * Ex : "K7ABCQA" → "K7•• - ••QA"
   */
  maskCode(code: string): string {
    if (!code) return '';
    if (code.length <= 4) return '••••';
    const start = code.slice(0, 2);
    const end   = code.slice(-2);
    return `${start}•• - ••${end}`;
  }

  /** Bascule la visibilité du code de retrait */
  toggleCodeVisibility(): void {
    this.codeVisible = !this.codeVisible;
  }

  /** Copie le code de retrait dans le presse-papiers */
  copyToClipboard(text: string): void {
    navigator.clipboard.writeText(text).then(() => {
      // TODO: afficher un toast de confirmation
      console.log('Code copié !');
    });
  }

  /**
   * Génère et télécharge un reçu PDF professionnel du transfert.
   * Utilise jsPDF (aucun template HTML imprimé) pour un rendu propre et constant.
   */
  onPrint(): void {
    if (!this.transfert) return;

    const t = this.transfert;
    const doc = new jsPDF('p', 'mm', 'a4');
    const pageW = doc.internal.pageSize.getWidth();
    const margin = 16;
    const contentW = pageW - margin * 2;

    // Palette
    const navy: [number, number, number] = [15, 23, 41];
    const navyLight: [number, number, number] = [28, 41, 69];
    const amber: [number, number, number] = [249, 168, 37];
    const grayText: [number, number, number] = [100, 116, 139];
    const dark: [number, number, number] = [15, 23, 41];
    const line: [number, number, number] = [226, 232, 240];

    const fmt = (n: any) =>
      Number(n ?? 0).toLocaleString('fr-FR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    const dateStr = (d: any) => (d ? new Date(d).toLocaleDateString('fr-FR') : '—');

    // ---------- EN-TÊTE (bandeau navy) ----------
    doc.setFillColor(...navy);
    doc.rect(0, 0, pageW, 42, 'F');

    doc.setTextColor(...amber);
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(20);
    doc.text('OkaneTransfer', margin, 20);

    doc.setTextColor(255, 255, 255);
    doc.setFont('helvetica', 'normal');
    doc.setFontSize(10);
    doc.text('Reçu de transfert d\'argent', margin, 28);

    // Référence + statut (à droite)
    doc.setFontSize(9);
    doc.setTextColor(180, 190, 205);
    doc.text('RÉFÉRENCE', pageW - margin, 16, { align: 'right' });
    doc.setTextColor(255, 255, 255);
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(13);
    doc.text(`${t.numeroReference ?? '—'}`, pageW - margin, 23, { align: 'right' });

    doc.setFont('helvetica', 'normal');
    doc.setFontSize(9);
    doc.setTextColor(...amber);
    doc.text(`Statut : ${t.statut ?? '—'}`, pageW - margin, 31, { align: 'right' });

    doc.setTextColor(180, 190, 205);
    doc.setFontSize(8);
    doc.text(`Émis le ${dateStr(new Date())}`, pageW - margin, 37, { align: 'right' });

    let y = 56;

    // ---------- Helper : titre de section ----------
    const sectionTitle = (label: string) => {
      doc.setFont('helvetica', 'bold');
      doc.setFontSize(9);
      doc.setTextColor(...grayText);
      doc.text(label.toUpperCase(), margin, y);
      doc.setDrawColor(...line);
      doc.setLineWidth(0.3);
      doc.line(margin, y + 2, pageW - margin, y + 2);
      y += 9;
    };

    // ---------- Helper : ligne clé/valeur ----------
    const row = (label: string, value: string, opts: { bold?: boolean; x?: number; w?: number } = {}) => {
      const x = opts.x ?? margin;
      const w = opts.w ?? contentW;
      doc.setFont('helvetica', 'normal');
      doc.setFontSize(9.5);
      doc.setTextColor(...grayText);
      doc.text(label, x, y);
      doc.setFont('helvetica', opts.bold ? 'bold' : 'normal');
      doc.setTextColor(...dark);
      doc.text(value, x + w, y, { align: 'right' });
      y += 7;
    };

    // ---------- EXPÉDITEUR & BÉNÉFICIAIRE (2 colonnes) ----------
    sectionTitle('Parties');
    const colGap = 8;
    const colW = (contentW - colGap) / 2;
    const startY = y;

    const partyBlock = (
      title: string,
      accent: [number, number, number],
      x: number,
      lines: { label: string; value: string }[]
    ) => {
      let cy = startY;
      doc.setFillColor(...accent);
      doc.rect(x, cy - 4, 2.5, 7, 'F');
      doc.setFont('helvetica', 'bold');
      doc.setFontSize(10);
      doc.setTextColor(...dark);
      doc.text(title, x + 5, cy + 1);
      cy += 9;
      lines.forEach((l) => {
        doc.setFont('helvetica', 'normal');
        doc.setFontSize(8.5);
        doc.setTextColor(...grayText);
        doc.text(l.label, x, cy);
        cy += 4.5;
        doc.setFont('helvetica', 'bold');
        doc.setFontSize(9.5);
        doc.setTextColor(...dark);
        doc.text(l.value || '—', x, cy);
        cy += 7;
      });
      return cy;
    };

    const senderEnd = partyBlock('EXPÉDITEUR', navy, margin, [
      { label: 'Nom', value: t.nomExpediteur },
      { label: 'Localisation', value: `${t.villeExpediteur ?? ''}, ${t.paysExpediteur ?? ''}` },
      { label: 'Téléphone', value: t.telephoneExpediteur },
    ]);
    const beneEnd = partyBlock('BÉNÉFICIAIRE', amber, margin + colW + colGap, [
      { label: 'Nom', value: t.nomBeneficiaire },
      { label: 'Localisation', value: `${t.villeBeneficiaire ?? ''}, ${t.paysBeneficiaire ?? ''}` },
      { label: 'Téléphone', value: t.telephoneBeneficiaire },
    ]);

    y = Math.max(senderEnd, beneEnd) + 6;

    // ---------- DÉTAILS FINANCIERS ----------
    sectionTitle('Détails financiers');
    row('Montant envoyé', `${fmt(t.montantEnvoye)} MAD`);
    row('Frais OkaneTransfer', `- ${fmt(t.frais)} MAD`);
    row('Taux du jour appliqué', `1 MAD = ${fmt(t.tauxChange)} ${t.deviseReception ?? ''}`);

    // Total mis en évidence
    y += 1;
    doc.setFillColor(247, 249, 252);
    doc.roundedRect(margin, y - 5, contentW, 12, 2, 2, 'F');
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(10.5);
    doc.setTextColor(...dark);
    doc.text(`${t.nomBeneficiaire ?? 'Le bénéficiaire'} reçoit`, margin + 4, y + 2);
    doc.setTextColor(...navyLight);
    doc.text(`${fmt(t.montantRecu)} ${t.deviseReception ?? ''}`, pageW - margin - 4, y + 2, { align: 'right' });
    y += 18;

    // ---------- CODE DE RETRAIT (boîte navy) ----------
    sectionTitle('Code de retrait');
    doc.setFillColor(...navy);
    doc.roundedRect(margin, y - 4, contentW, 18, 2, 2, 'F');
    doc.setFont('courier', 'bold');
    doc.setFontSize(18);
    doc.setTextColor(255, 255, 255);
    doc.text(`${t.codeRetrait ?? '—'}`, margin + 6, y + 7);
    doc.setFont('helvetica', 'normal');
    doc.setFontSize(7.5);
    doc.setTextColor(...amber);
    doc.text('À communiquer uniquement au bénéficiaire', pageW - margin - 6, y + 7, { align: 'right' });
    y += 24;

    // ---------- INFORMATIONS ----------
    sectionTitle('Informations');
    row('Moyen de retrait', 'Cash au guichet · réseau OkaneTransfer');
    row('Date d\'expiration', dateStr(t.expireLe));

    // ---------- PIED DE PAGE ----------
    const footY = doc.internal.pageSize.getHeight() - 16;
    doc.setDrawColor(...line);
    doc.setLineWidth(0.3);
    doc.line(margin, footY - 6, pageW - margin, footY - 6);
    doc.setFont('helvetica', 'normal');
    doc.setFontSize(7.5);
    doc.setTextColor(...grayText);
    doc.text(
      'Ce document est un reçu généré automatiquement par OkaneTransfer. Conservez-le pour vos archives.',
      pageW / 2,
      footY,
      { align: 'center' }
    );

    // Téléchargement
    doc.save(`recu-${t.numeroReference ?? 'transfert'}.pdf`);
  }

  /** Ferme / retour à la liste */
  onClose(): void {
    if (this.transfertId) {
      this.router.navigate(['/client/historique']);
    } else {
      this.closeDetail.emit();
    }
  }
}
