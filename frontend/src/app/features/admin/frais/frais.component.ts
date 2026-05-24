import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { FormsModule } from '@angular/forms';
import { DeviseService } from '../../../core/services/devise.service';
import { TranslationService } from '../../../core/services/translation.service';
import { TranslatePipe } from '../../../shared/pipes/translate.pipe';
import { CorridorResponse } from '../../../core/models/devise/corridor-response.model';
import { GrilleTarifaireRequest } from '../../../core/models/devise/grille-tarifaire-request.model';
import { GrilleTarifaireResponse } from '../../../core/models/devise/grille-tarifaire-response.model';
import { FraisResult } from '../../../core/models/devise/frais-result.model';
import { TopbarComponent } from '../../../shared/components/topbar/topbar.component';
import { IconComponent } from '../../../shared/components/icon/icon.component';

@Component({
  selector: 'app-frais',
  standalone: true,
  host: { class: 'flex flex-col flex-1 min-w-0 overflow-hidden' },
  imports: [TopbarComponent, IconComponent, ReactiveFormsModule, FormsModule, TranslatePipe],
  templateUrl: './frais.component.html',
})
export class FraisComponent implements OnInit {
  corridors: CorridorResponse[] = [];
  activeCorridorId: number | null = null;
  loadingCorridors = true;

  grilles: GrilleTarifaireResponse[] = [];
  loadingGrilles = false;

  simulatorAmount = 3000;
  fraisResult: FraisResult | null = null;
  calculating = false;

  showModal = false;
  modalMode: 'create' | 'edit' = 'create';
  editingGrilleId: number | null = null;
  saving = false;
  deleting: number | null = null;
  error: string | null = null;
  form!: FormGroup;

  isMobile = window.innerWidth < 768;

  constructor(
    private deviseService: DeviseService,
    private fb: FormBuilder,
    public ts: TranslationService,
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      montantMin:       [null, [Validators.required, Validators.min(0)]],
      montantMax:       [null, [Validators.required, Validators.min(0)]],
      fraisFixe:        [null, [Validators.required, Validators.min(0)]],
      fraisPourcentage: [0,    [Validators.required, Validators.min(0)]],
      partAgence:       [null, [Validators.required, Validators.min(0)]],
      partCentrale:     [{ value: null, disabled: true }],
    }, { validators: this.partCentraleValidator });

    this.form.get('fraisFixe')!.valueChanges.subscribe(() => this.computePartCentrale());
    this.form.get('partAgence')!.valueChanges.subscribe(() => this.computePartCentrale());

    this.deviseService.getAllCorridors().subscribe({
      next: corridors => {
        this.corridors = corridors;
        if (corridors.length) {
          this.activeCorridorId = corridors[0].id;
          this.loadGrilles(corridors[0].id);
          this.simuler();
        }
        this.loadingCorridors = false;
      },
      error: () => { this.loadingCorridors = false; },
    });
  }

  get activeCorridor(): CorridorResponse | undefined {
    return this.corridors.find(c => c.id === this.activeCorridorId);
  }

  get pageSubtitle(): string {
    if (this.loadingCorridors) return this.ts.t('frais.loading');
    return `${this.grilles.length} ${this.ts.t('frais.subtitle')}`;
  }

  selectCorridor(id: number): void {
    this.activeCorridorId = id;
    this.fraisResult = null;
    this.showModal = false;
    this.grilles = [];
    this.loadGrilles(id);
    this.simuler();
  }

  loadGrilles(corridorId: number): void {
    this.loadingGrilles = true;
    this.deviseService.getGrillesByCorridor(corridorId).subscribe({
      next: grilles => { this.grilles = grilles; this.loadingGrilles = false; },
      error: () => { this.grilles = []; this.loadingGrilles = false; },
    });
  }

  private partCentraleValidator(group: AbstractControl): ValidationErrors | null {
    const centrale = +(group.get('partCentrale')?.value ?? 0);
    return centrale < 0 ? { partNegative: true } : null;
  }

  private computePartCentrale(): void {
    const fixe   = +(this.form.get('fraisFixe')?.value  ?? 0);
    const agence = +(this.form.get('partAgence')?.value ?? 0);
    this.form.get('partCentrale')!.setValue(+(fixe - agence).toFixed(2), { emitEvent: false });
  }

  get partCentraleNegative(): boolean {
    return this.form.errors?.['partNegative'] === true;
  }

  openCreate(): void {
    this.modalMode = 'create';
    this.editingGrilleId = null;
    this.form.reset({ fraisPourcentage: 0, partCentrale: null });
    this.error = null;
    this.showModal = true;
  }

  openEdit(g: GrilleTarifaireResponse): void {
    this.modalMode = 'edit';
    this.editingGrilleId = g.id;
    this.form.setValue({
      montantMin:       g.montantMin,
      montantMax:       g.montantMax,
      fraisFixe:        g.fraisFixe,
      fraisPourcentage: g.fraisPourcentage,
      partAgence:       g.partAgence,
      partCentrale:     g.partCentrale,
    });
    this.error = null;
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
    this.error = null;
  }

  submit(): void {
    if (this.form.invalid || !this.activeCorridorId) return;
    this.saving = true;
    this.error = null;

    const v = this.form.getRawValue();
    const request: GrilleTarifaireRequest = {
      corridorId:       this.activeCorridorId,
      montantMin:       +v.montantMin,
      montantMax:       +v.montantMax,
      fraisFixe:        +v.fraisFixe,
      fraisPourcentage: +v.fraisPourcentage,
      partAgence:       +v.partAgence,
      partCentrale:     +v.partCentrale,
    };

    if (this.modalMode === 'create') {
      this.deviseService.createGrille(request).subscribe({
        next: () => {
          this.loadGrilles(this.activeCorridorId!);
          this.saving = false;
          this.closeModal();
        },
        error: () => {
          this.error = this.ts.t('frais.error.create');
          this.saving = false;
        },
      });
    } else {
      this.deviseService.updateGrille(this.editingGrilleId!, request).subscribe({
        next: () => {
          this.loadGrilles(this.activeCorridorId!);
          this.saving = false;
          this.closeModal();
        },
        error: () => {
          this.error = this.ts.t('frais.error.update');
          this.saving = false;
        },
      });
    }
  }

  deleteGrille(g: GrilleTarifaireResponse): void {
    if (this.deleting !== null) return;
    if (!confirm(this.ts.t('frais.deleteConfirm'))) return;
    this.deleting = g.id;
    this.deviseService.deleteGrille(g.id).subscribe({
      next: () => {
        this.grilles = this.grilles.filter(x => x.id !== g.id);
        this.deleting = null;
      },
      error: () => { this.deleting = null; },
    });
  }

  simuler(): void {
    if (!this.activeCorridorId || !this.simulatorAmount || this.simulatorAmount <= 0) return;
    this.calculating = true;
    this.deviseService.calculerFrais(this.activeCorridorId, this.simulatorAmount).subscribe({
      next: r  => { this.fraisResult = r; this.calculating = false; },
      error: () => { this.calculating = false; },
    });
  }
}
