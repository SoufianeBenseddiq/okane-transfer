import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { DatePipe, DecimalPipe } from '@angular/common';
import { TranslateService, TranslatePipe } from '@ngx-translate/core';
import { AmlService } from '../../../core/services/aml.service';
import { DeclarationResponse, RegleAML } from '../../../core/models/aml';
import { TopbarComponent } from '../../../shared/components/topbar/topbar.component';
import { IconComponent } from '../../../shared/components/icon/icon.component';

type Tab = 'declarations' | 'regles';
type ModalMode = 'create' | 'edit';

@Component({
  selector: 'app-conformite',
  standalone: true,
  host: { class: 'flex flex-col flex-1 min-w-0 overflow-hidden' },
  imports: [TopbarComponent, IconComponent, ReactiveFormsModule, DatePipe, DecimalPipe, TranslatePipe],
  templateUrl: './conformite.component.html',
})
export class ConformiteComponent implements OnInit {
  activeTab: Tab = 'declarations';

  declarations: DeclarationResponse[] = [];
  regles: RegleAML[] = [];
  loading = true;
  treating: number | null = null;
  deleting: number | null = null;
  saving = false;
  error: string | null = null;

  showModal = false;
  modalMode: ModalMode = 'create';
  editingId: number | null = null;

  form = this.fb.group({
    nom:                 ['', Validators.required],
    description:         ['', Validators.required],
    seuilMontant:        [null as number | null],
    seuilNbTransactions: [null as number | null],
    fenetreTempsMinutes: [null as number | null],
    active:              [true, Validators.required],
  });

  constructor(
    private aml: AmlService,
    private fb: FormBuilder,
    private translate: TranslateService,
  ) {}

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading = true;
    this.aml.getAllDeclarations().subscribe({
      next: data => { this.declarations = data; this.loadRegles(); },
      error: () => { this.loading = false; },
    });
  }

  loadRegles(): void {
    this.aml.getAllRegles().subscribe({
      next: data => { this.regles = data; this.loading = false; },
      error: () => { this.loading = false; },
    });
  }

  get pendingCount(): number { return this.declarations.filter(d => !d.traitee).length; }
  get activeReglesCount(): number { return this.regles.filter(r => r.active).length; }

  get pageSubtitle(): string {
    if (this.activeTab === 'declarations') {
      return `${this.pendingCount} ${this.translate.instant('conformite.subtitle.declarations')}`;
    }
    return `${this.activeReglesCount} ${this.translate.instant('conformite.subtitle.regles')}`;
  }

  traiter(d: DeclarationResponse): void {
    this.treating = d.id;
    this.aml.updateDeclaration(d.id, { traitee: true }).subscribe({
      next: updated => {
        const idx = this.declarations.findIndex(x => x.id === d.id);
        if (idx !== -1) this.declarations[idx] = updated;
        this.treating = null;
      },
      error: () => { this.treating = null; },
    });
  }

  openCreate(): void {
    this.modalMode = 'create';
    this.editingId = null;
    this.form.reset({ active: true });
    this.error = null;
    this.showModal = true;
  }

  openEdit(r: RegleAML): void {
    this.modalMode = 'edit';
    this.editingId = r.id ?? null;
    this.form.patchValue({
      nom: r.nom,
      description: r.description,
      seuilMontant: r.seuilMontant,
      seuilNbTransactions: r.seuilNbTransactions,
      fenetreTempsMinutes: r.fenetreTempsMinutes,
      active: r.active,
    });
    this.error = null;
    this.showModal = true;
  }

  closeModal(): void { this.showModal = false; }

  submit(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid) return;
    this.saving = true;
    this.error = null;
    const val = this.form.value as RegleAML;

    if (this.modalMode === 'create') {
      this.aml.createRegle(val).subscribe({
        next: r => { this.regles.unshift(r); this.saving = false; this.showModal = false; },
        error: () => { this.error = this.translate.instant('conformite.regles.error.create'); this.saving = false; },
      });
    } else {
      this.aml.updateRegle(this.editingId!, val).subscribe({
        next: r => {
          const idx = this.regles.findIndex(x => x.id === this.editingId);
          if (idx !== -1) this.regles[idx] = r;
          this.saving = false;
          this.showModal = false;
        },
        error: () => { this.error = this.translate.instant('conformite.regles.error.update'); this.saving = false; },
      });
    }
  }

  deleteRegle(r: RegleAML): void {
    if (!confirm(this.translate.instant('conformite.regles.deleteConfirm'))) return;
    this.deleting = r.id ?? null;
    this.aml.deleteRegle(r.id!).subscribe({
      next: () => { this.regles = this.regles.filter(x => x.id !== r.id); this.deleting = null; },
      error: () => { this.deleting = null; },
    });
  }
}
