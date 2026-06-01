import { Component, HostListener, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { FormsModule } from '@angular/forms';
import { AgenceService } from '../../../core/services/agence.service';
import { AgenceResponse, AgenceRequest } from '../../../core/models/agence';
import { UserService } from '../../../core/services/user.service';
import { UserResponse } from '../../../core/models/user';
import { RoleUtilisateur } from '../../../core/models/enums';
import { TopbarComponent } from '../../../shared/components/topbar/topbar.component';
import { IconComponent } from '../../../shared/components/icon/icon.component';
import { TranslateService, TranslatePipe } from '@ngx-translate/core';
import { ConfirmDialogComponent, ConfirmDialogConfig } from '../../../shared/components/confirm-dialog/confirm-dialog.component';

type FilterTab  = 'all' | 'active' | 'inactive';
type ModalMode  = 'create' | 'edit';

@Component({
  selector: 'app-agences',
  standalone: true,
  host: { class: 'flex flex-col flex-1 min-w-0 overflow-hidden' },
  imports: [TopbarComponent, IconComponent, FormsModule, ReactiveFormsModule, TranslatePipe, ConfirmDialogComponent],
  templateUrl: './agences.component.html',
})
export class AgencesComponent implements OnInit {
  agences:  AgenceResponse[] = [];
  managers: UserResponse[]   = [];
  loading  = true;
  saving   = false;
  deleting: number | null = null;
  toggling: number | null = null;
  error: string | null = null;

  showModal = false;
  modalMode: ModalMode = 'create';
  editingId: number | null = null;
  detailAgence: AgenceResponse | null = null;
  centrales: AgenceResponse[] = [];

  confirmDialog: (ConfirmDialogConfig & { action: () => void }) | null = null;

  search    = '';
  activeTab: FilterTab = 'all';
  isMobile  = window.innerWidth < 768;

  readonly filterTabs: Array<{ id: FilterTab; key: string }> = [
    { id: 'all',      key: 'agences.tab.all'      },
    { id: 'active',   key: 'agences.tab.active'   },
    { id: 'inactive', key: 'agences.tab.inactive' },
  ];

  @HostListener('window:resize')
  onResize(): void { this.isMobile = window.innerWidth < 768; }

  form = this.fb.group({
    nom:               ['', Validators.required],
    adresse:           ['', Validators.required],
    pays:              ['MA', Validators.required],
    plafondJournalier: [null as number | null, [Validators.required, Validators.min(1)]],
    responsableId:     [null as number | null],
    estCentrale:       [false],
    agenceCentraleId:  [null as number | null],
  });

  constructor(
    private agenceService: AgenceService,
    private userService: UserService,
    private fb: FormBuilder,
    private translate: TranslateService,
  ) {}

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading = true;
    this.agenceService.findAll().subscribe({
      next:  data => { this.agences = data; this.loading = false; },
      error: ()   => { this.loading = false; },
    });
  }

  private loadManagers(): void {
    if (this.managers.length) return;
    this.userService.findAll(RoleUtilisateur.ROLE_MANAGER).subscribe({
      next: data => { this.managers = data; },
    });
  }

  private loadCentrales(): void {
    this.agenceService.findCentrales().subscribe({
      next: data => { this.centrales = data; },
    });
  }

  get isCentrale(): boolean { return !!this.form.get('estCentrale')?.value; }

  get filtered(): AgenceResponse[] {
    return this.agences.filter(a => {
      const matchSearch = !this.search ||
        a.nom.toLowerCase().includes(this.search.toLowerCase()) ||
        a.adresse.toLowerCase().includes(this.search.toLowerCase());
      const matchTab =
        this.activeTab === 'all'      ? true :
        this.activeTab === 'active'   ? a.active :
        !a.active;
      return matchSearch && matchTab;
    });
  }

  get countActive():   number  { return this.agences.filter(a =>  a.active).length; }
  get countInactive(): number  { return this.agences.filter(a => !a.active).length; }
  get showPanel():     boolean { return this.showModal || this.detailAgence !== null; }

  get pageSubtitle(): string {
    return `${this.agences.length} ${this.translate.instant('agences.subtitle')} · ${this.countActive} ${this.translate.instant('agences.activeCount')} · ${this.countInactive} ${this.translate.instant('agences.inactiveCount')}`;
  }

  capacityPct(a: AgenceResponse): number {
    if (!a.plafondJournalier) return 0;
    return Math.min(100, Math.round((a.montantTraiteAujourdhui / a.plafondJournalier) * 100));
  }

  capacityColor(pct: number): string {
    return pct < 70 ? '#00C48C' : pct < 90 ? '#FAAD14' : '#FF4D4F';
  }

  initials(name: string | null): string {
    if (!name) return '?';
    return name.split(' ').map(s => s[0]).filter(Boolean).join('').slice(0, 2).toUpperCase();
  }

  openCreate(): void {
    this.loadManagers();
    this.loadCentrales();
    this.detailAgence = null;
    this.modalMode = 'create';
    this.editingId = null;
    this.form.reset({ pays: 'MA', estCentrale: false, agenceCentraleId: null });
    this.error = null;
    this.showModal = true;
  }

  openEdit(a: AgenceResponse): void {
    this.loadManagers();
    this.loadCentrales();
    this.detailAgence = null;
    this.modalMode = 'edit';
    this.editingId = a.id;
    this.form.patchValue({
      nom:               a.nom,
      adresse:           a.adresse,
      pays:              a.pays,
      plafondJournalier: a.plafondJournalier,
      responsableId:     a.responsableId ?? null,
      estCentrale:       a.estCentrale ?? false,
      agenceCentraleId:  a.agenceCentraleId ?? null,
    });
    this.error = null;
    this.showModal = true;
  }

  closeModal(): void { this.showModal = false; }

  submit(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid) return;
    this.saving = true;
    this.error  = null;

    const req = this.form.value as AgenceRequest;

    if (this.modalMode === 'create') {
      this.agenceService.create(req).subscribe({
        next: a => {
          this.agences.unshift(a);
          this.saving = false;
          this.showModal = false;
        },
        error: () => {
          this.error  = this.translate.instant('agences.error.create');
          this.saving = false;
        },
      });
    } else {
      this.agenceService.update(this.editingId!, req).subscribe({
        next: updated => {
          const idx = this.agences.findIndex(a => a.id === this.editingId);
          if (idx !== -1) this.agences[idx] = updated;
          this.saving = false;
          this.showModal = false;
        },
        error: () => {
          this.error  = this.translate.instant('agences.error.update');
          this.saving = false;
        },
      });
    }
  }

  toggleActive(a: AgenceResponse): void {
    if (a.active) {
      this.confirmDialog = {
        title:        this.translate.instant('agences.deactivate'),
        message:      this.translate.instant('agences.deactivateConfirm', { nom: a.nom }),
        confirmLabel: this.translate.instant('agences.deactivate'),
        danger:       true,
        action: () => this.doToggleActive(a),
      };
    } else {
      this.doToggleActive(a);
    }
  }

  private doToggleActive(a: AgenceResponse): void {
    this.confirmDialog = null;
    this.toggling = a.id;
    this.agenceService.toggleActive(a.id).subscribe({
      next: updated => {
        const idx = this.agences.findIndex(x => x.id === a.id);
        if (idx !== -1) this.agences[idx] = updated;
        if (this.detailAgence?.id === a.id) this.detailAgence = updated;
        this.toggling = null;
      },
      error: () => { this.toggling = null; },
    });
  }

  deleteAgence(a: AgenceResponse): void {
    this.confirmDialog = {
      title:        this.translate.instant('agences.deleteConfirm'),
      message:      `"${a.nom}" — ${this.translate.instant('common.noRevert') || 'Cette action est irréversible.'}`,
      confirmLabel: this.translate.instant('common.delete'),
      danger:       true,
      action: () => this.doDelete(a),
    };
  }

  private doDelete(a: AgenceResponse): void {
    this.confirmDialog = null;
    this.deleting = a.id;
    this.agenceService.delete(a.id).subscribe({
      next: () => {
        this.agences = this.agences.filter(x => x.id !== a.id);
        if (this.detailAgence?.id === a.id) this.detailAgence = null;
        this.deleting = null;
      },
      error: () => { this.deleting = null; },
    });
  }

  goDetail(id: number): void {
    this.showModal = false;
    this.detailAgence = this.agences.find(a => a.id === id) ?? null;
  }

  closeDetail(): void { this.detailAgence = null; }
}
