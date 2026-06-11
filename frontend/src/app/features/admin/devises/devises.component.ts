import { Component, HostListener, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { forkJoin } from 'rxjs';
import { DeviseService } from '../../../core/services/devise.service';
import { PaysService } from '../../../core/services/pays.service';
import { DeviseResponse } from '../../../core/models/devise/devise-response.model';
import { CorridorResponse } from '../../../core/models/devise/corridor-response.model';
import { PaysResponse } from '../../../core/models/pays/pays-response.model';
import { TopbarComponent } from '../../../shared/components/topbar/topbar.component';
import { IconComponent } from '../../../shared/components/icon/icon.component';
import { TranslateService, TranslatePipe } from '@ngx-translate/core';

type MainTab  = 'corridors' | 'pays-devises';
type PanelMode = 'devise-create' | 'devise-edit' | 'corridor-create' | 'pays-create' | 'pays-edit' | null;

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
  pays:      PaysResponse[]     = [];
  loading  = true;
  saving   = false;
  error: string | null = null;

  mainTab:   MainTab   = 'corridors';
  panelMode: PanelMode = null;
  deviseTab  = 'all';
  isMobile   = window.innerWidth < 768;

  editingDevise: DeviseResponse | null = null;
  editingPays:   PaysResponse   | null = null;
  inlineDevise   = false; // toggle to create a new devise inline in the pays form

  @HostListener('window:resize')
  onResize(): void { this.isMobile = window.innerWidth < 768; }

  readonly sourceTauxOptions = ['API', 'MANUEL'];

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

  paysForm = this.fb.group({
    nom:          ['', Validators.required],
    codeIso:      ['', [Validators.required, Validators.minLength(2), Validators.maxLength(3)]],
    indicatifTel: ['', Validators.required],
    formatTel:    [''],
    longueurTel:  [null as number | null],
    deviseId:     [null as number | null, Validators.required],
    // Inline devise creation fields (used when inlineDevise = true)
    deviseCode:     [''],
    deviseNom:      [''],
    deviseSymbole:  [''],
    deviseTaux:     [null as number | null],
    deviseSource:   ['MANUEL'],
  });

  corridorForm = this.fb.group({
    paysSourceId:      [null as number | null, Validators.required],
    paysDestinationId: [null as number | null, Validators.required],
  });

  constructor(
    private deviseService: DeviseService,
    private paysService:   PaysService,
    private fb: FormBuilder,
    private translate: TranslateService,
  ) {}

  ngOnInit(): void {
    forkJoin({
      devises:   this.deviseService.getAllDevises(),
      corridors: this.deviseService.getAllCorridors(),
      pays:      this.paysService.getAll(),
    }).subscribe({
      next: ({ devises, corridors, pays }) => {
        this.devises   = devises;
        this.corridors = corridors.filter(c => c.paysSource && c.paysDestination);
        this.pays      = pays;
        this.loading   = false;
      },
      error: () => { this.loading = false; },
    });
  }

  // ── Computed ─────────────────────────────────────────────────────────────────

  get filteredDevises(): DeviseResponse[] {
    if (this.deviseTab === 'api') return this.devises.filter(d => d.sourceTaux === 'API');
    if (this.deviseTab === 'man') return this.devises.filter(d => d.sourceTaux !== 'API');
    return this.devises;
  }

  get activeCount():   number { return this.devises.filter(d =>  d.active).length; }
  get corridorCount(): number { return this.corridors.length; }
  get paysCount():     number { return this.pays.length; }

  get pageSubtitle(): string {
    if (this.loading) return this.translate.instant('devises.subtitle.loading');
    return `${this.activeCount} ${this.translate.instant('devises.subtitle.active')} · ${this.corridorCount} ${this.translate.instant('devises.subtitle.corridors')} · ${this.paysCount} pays`;
  }

  get showPanel(): boolean { return this.panelMode !== null; }

  corridorLabel(c: CorridorResponse): string {
    return `${c.paysSource?.nom ?? c.deviseSource} → ${c.paysDestination?.nom ?? c.deviseDestination}`;
  }

  corridorDevises(c: CorridorResponse): string {
    return `${c.deviseSource ?? '?'} → ${c.deviseDestination ?? '?'}`;
  }

  getFlagEmoji(codeIso?: string | null): string {
    if (!codeIso || codeIso.length !== 2) return '🏳️';
    return codeIso.toUpperCase().split('').map(c => String.fromCodePoint(0x1F1E6 + c.charCodeAt(0) - 65)).join('');
  }

  // ── Devise actions ────────────────────────────────────────────────────────────

  toggleDevise(d: DeviseResponse): void {
    const obs = d.active
      ? this.deviseService.desactiverDevise(d.id)
      : this.deviseService.activerDevise(d.id);
    obs.subscribe({ next: () => { d.active = !d.active; } });
  }

  openCreateDevise(): void {
    this.panelMode = 'devise-create';
    this.editingDevise = null;
    this.deviseForm.reset({ sourceTaux: 'MANUEL' });
    this.deviseForm.get('code')!.enable();
    this.error = null;
  }

  openEditDevise(d: DeviseResponse): void {
    this.panelMode = 'devise-edit';
    this.editingDevise = d;
    this.deviseForm.patchValue({ code: d.code, nom: d.nom, symbole: d.symbole, tauxVersEuro: d.tauxVersEuro, sourceTaux: d.sourceTaux });
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

  // ── Pays actions ──────────────────────────────────────────────────────────────

  openCreatePays(): void {
    this.panelMode = 'pays-create';
    this.editingPays = null;
    this.inlineDevise = false;
    this.paysForm.reset({ deviseSource: 'MANUEL' });
    this.error = null;
  }

  openEditPays(p: PaysResponse): void {
    this.panelMode = 'pays-edit';
    this.editingPays = p;
    this.inlineDevise = false;
    this.paysForm.patchValue({
      nom: p.nom, codeIso: p.codeIso,
      indicatifTel: p.indicatifTel, formatTel: p.formatTel ?? '',
      longueurTel: p.longueurTel,
      deviseId: this.devises.find(d => d.code === p.deviseCode)?.id ?? null,
    });
    this.error = null;
  }

  submitPays(): void {
    this.error = null;
    if (this.inlineDevise) {
      // Create the new devise first, then use its id
      const dv = this.paysForm.getRawValue();
      if (!dv.deviseCode?.trim() || !dv.deviseNom?.trim() || !dv.deviseSymbole?.trim() || !dv.deviseTaux) {
        this.error = 'Veuillez remplir tous les champs de la nouvelle devise.';
        return;
      }
      this.saving = true;
      this.deviseService.createDevise({
        code: dv.deviseCode!.toUpperCase().trim(),
        nom: dv.deviseNom!.trim(),
        symbole: dv.deviseSymbole!.trim(),
        tauxVersEuro: dv.deviseTaux,
        sourceTaux: dv.deviseSource ?? 'MANUEL',
      } as any).subscribe({
        next: newDevise => {
          this.devises.unshift(newDevise);
          this.paysForm.patchValue({ deviseId: newDevise.id });
          this.inlineDevise = false;
          this.doSubmitPays();
        },
        error: () => { this.error = 'Erreur lors de la création de la devise.'; this.saving = false; },
      });
    } else {
      this.paysForm.markAllAsTouched();
      if (this.paysForm.get('nom')?.invalid || this.paysForm.get('codeIso')?.invalid ||
          this.paysForm.get('indicatifTel')?.invalid || !this.paysForm.value.deviseId) {
        this.error = 'Veuillez remplir tous les champs obligatoires.';
        return;
      }
      this.saving = true;
      this.doSubmitPays();
    }
  }

  private doSubmitPays(): void {
    const v = this.paysForm.getRawValue();
    const request = {
      nom: v.nom!, codeIso: v.codeIso!, indicatifTel: v.indicatifTel!,
      formatTel: v.formatTel || undefined, longueurTel: v.longueurTel || undefined,
      deviseId: v.deviseId!,
    };

    if (this.panelMode === 'pays-create') {
      this.paysService.createPays(request as any).subscribe({
        next: p => { this.pays.push(p); this.saving = false; this.closePanel(); },
        error: (e) => {
          this.error = e?.error?.message ?? 'Erreur lors de la création du pays.';
          this.saving = false;
        },
      });
    } else {
      this.paysService.updatePays(this.editingPays!.id, request as any).subscribe({
        next: updated => {
          const idx = this.pays.findIndex(p => p.id === this.editingPays!.id);
          if (idx !== -1) this.pays[idx] = updated;
          this.saving = false; this.closePanel();
        },
        error: (e) => {
          this.error = e?.error?.message ?? 'Erreur lors de la mise à jour du pays.';
          this.saving = false;
        },
      });
    }
  }

  // ── Corridor actions ──────────────────────────────────────────────────────────

  toggleCorridor(c: CorridorResponse): void {
    const obs = c.actif
      ? this.deviseService.desactiverCorridor(c.id)
      : this.deviseService.activerCorridor(c.id);
    obs.subscribe({ next: () => { c.actif = !c.actif; } });
  }

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

  // ── Shared ────────────────────────────────────────────────────────────────────

  closePanel(): void {
    this.panelMode     = null;
    this.editingDevise = null;
    this.editingPays   = null;
    this.inlineDevise  = false;
    this.deviseForm.get('code')!.enable();
    this.error = null;
  }

  switchMainTab(tab: MainTab): void {
    this.mainTab = tab;
    this.closePanel();
  }
}
