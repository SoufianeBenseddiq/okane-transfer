import { Component, HostListener, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { forkJoin } from 'rxjs';
import { DeviseService } from '../../../core/services/devise.service';
import { DeviseResponse } from '../../../core/models/devise/devise-response.model';
import { CorridorResponse } from '../../../core/models/devise/corridor-response.model';
import { TopbarComponent } from '../../../shared/components/topbar/topbar.component';
import { IconComponent } from '../../../shared/components/icon/icon.component';
import { TranslateService, TranslatePipe } from '@ngx-translate/core';

// ⚠️ TODO (équipe Devise): ajouter DELETE /api/admin/devises/{id}
// ⚠️ TODO (équipe Corridor): ajouter PUT /api/admin/corridors/{id} et DELETE /api/admin/corridors/{id}

type PanelMode = 'devise-create' | 'devise-edit' | 'corridor-create' | null;

@Component({
  selector: 'app-devises',
  standalone: true,
  host: { class: 'flex flex-col flex-1 min-w-0 overflow-hidden' },
  imports: [TopbarComponent, IconComponent, DatePipe, ReactiveFormsModule, TranslatePipe],
  templateUrl: './devises.component.html',
})
export class DevisesComponent implements OnInit {
  devises:   DeviseResponse[]   = [];
  corridors: CorridorResponse[] = [];
  loading  = true;
  saving   = false;
  error: string | null = null;

  activeTab  = 'all';
  panelMode: PanelMode = null;
  isMobile   = window.innerWidth < 768;

  @HostListener('window:resize')
  onResize(): void { this.isMobile = window.innerWidth < 768; }
  editingDevise: DeviseResponse | null = null;

  readonly sourceTauxOptions = ['API', 'MANUEL'];

  // ⚠️ TODO (équipe Devise): remplacer par GET /api/admin/devises/historique
  readonly history = [
    { date: '22/05 · 14:08', dev: 'USD', old: '0.91',   neu: '0.92',   up: true,  src: 'API'    },
    { date: '22/05 · 11:42', dev: 'XOF', old: '654.10', neu: '655.96', up: true,  src: 'API'    },
    { date: '22/05 · 09:15', dev: 'GBP', old: '0.83',   neu: '0.84',   up: true,  src: 'API'    },
    { date: '21/05 · 17:30', dev: 'GNF', old: '9 380',  neu: '9 412',  up: true,  src: 'MANUEL' },
    { date: '21/05 · 14:00', dev: 'MAD', old: '10.88',  neu: '10.85',  up: false, src: 'API'    },
    { date: '21/05 · 09:00', dev: 'MRU', old: '43.40',  neu: '43.21',  up: false, src: 'API'    },
  ];

  deviseForm = this.fb.group({
    code:        ['', [Validators.required, Validators.minLength(3), Validators.maxLength(3)]],
    nom:         ['', Validators.required],
    symbole:     ['', Validators.required],
    tauxVersEuro:[ null as number | null, [Validators.required, Validators.min(0.000001)]],
    sourceTaux:  ['MANUEL'],
  });

  corridorForm = this.fb.group({
    deviseSourceId:      [null as number | null, Validators.required],
    deviseDestinationId: [null as number | null, Validators.required],
  });

  constructor(
    private deviseService: DeviseService,
    private fb: FormBuilder,
    private translate: TranslateService,
  ) {}

  ngOnInit(): void {
    forkJoin({
      devises:   this.deviseService.getAllDevises(),
      corridors: this.deviseService.getAllCorridors(),
    }).subscribe({
      next: ({ devises, corridors }) => {
        this.devises   = devises;
        this.corridors = corridors;
        this.loading   = false;
      },
      error: () => { this.loading = false; },
    });
  }

  get filteredDevises(): DeviseResponse[] {
    if (this.activeTab === 'api') return this.devises.filter(d => d.sourceTaux === 'API');
    if (this.activeTab === 'man') return this.devises.filter(d => d.sourceTaux !== 'API');
    return this.devises;
  }

  get activeCount():   number { return this.devises.filter(d =>  d.active).length; }
  get corridorCount(): number { return this.corridors.filter(c => c.actif).length; }

  get pageSubtitle(): string {
    if (this.loading) return this.translate.instant('devises.subtitle.loading');
    return `${this.activeCount} ${this.translate.instant('devises.subtitle.active')} · ${this.corridorCount} ${this.translate.instant('devises.subtitle.corridors')}`;
  }

  toggleDevise(d: DeviseResponse): void {
    const obs = d.active
      ? this.deviseService.desactiverDevise(d.id)
      : this.deviseService.activerDevise(d.id);
    obs.subscribe({ next: () => { d.active = !d.active; } });
  }

  toggleCorridor(c: CorridorResponse): void {
    const obs = c.actif
      ? this.deviseService.desactiverCorridor(c.id)
      : this.deviseService.activerCorridor(c.id);
    obs.subscribe({ next: () => { c.actif = !c.actif; } });
  }

  // ── Devise panel ────────────────────────────────────────────────────────────

  openCreateDevise(): void {
    this.panelMode = 'devise-create';
    this.editingDevise = null;
    this.deviseForm.reset({ sourceTaux: 'MANUEL' });
    this.error = null;
  }

  openEditDevise(d: DeviseResponse): void {
    this.panelMode = 'devise-edit';
    this.editingDevise = d;
    this.deviseForm.patchValue({
      code: d.code, nom: d.nom, symbole: d.symbole,
      tauxVersEuro: d.tauxVersEuro, sourceTaux: d.sourceTaux,
    });
    this.deviseForm.get('code')!.disable();
    this.error = null;
  }

  submitDevise(): void {
    this.deviseForm.markAllAsTouched();
    if (this.deviseForm.invalid) return;
    this.saving = true;
    this.error  = null;

    const raw = this.deviseForm.getRawValue();

    if (this.panelMode === 'devise-create') {
      this.deviseService.createDevise(raw as any).subscribe({
        next: d => { this.devises.unshift(d); this.saving = false; this.closePanel(); },
        error: () => { this.error = this.translate.instant('devises.error.create'); this.saving = false; },
      });
    } else {
      this.deviseService.updateDevise(this.editingDevise!.id, raw as any).subscribe({
        next: updated => {
          const idx = this.devises.findIndex(d => d.id === this.editingDevise!.id);
          if (idx !== -1) this.devises[idx] = updated;
          this.saving = false; this.closePanel();
        },
        error: () => { this.error = this.translate.instant('devises.error.update'); this.saving = false; },
      });
    }
  }

  // ── Corridor panel ──────────────────────────────────────────────────────────

  openCreateCorridor(): void {
    this.panelMode = 'corridor-create';
    this.corridorForm.reset();
    this.error = null;
  }

  submitCorridor(): void {
    this.corridorForm.markAllAsTouched();
    if (this.corridorForm.invalid) return;
    this.saving = true;
    this.error  = null;

    this.deviseService.createCorridor(this.corridorForm.value as any).subscribe({
      next: c => { this.corridors.push(c); this.saving = false; this.closePanel(); },
      error: () => { this.error = this.translate.instant('devises.error.createCorridor'); this.saving = false; },
    });
  }

  closePanel(): void {
    this.panelMode = null;
    this.editingDevise = null;
    this.deviseForm.get('code')!.enable();
  }

  get showPanel(): boolean { return this.panelMode !== null; }
}
