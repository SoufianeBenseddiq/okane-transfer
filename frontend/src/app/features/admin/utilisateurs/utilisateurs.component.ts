import { Component, HostListener, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { UserService } from '../../../core/services/user.service';
import { UserResponse, CreateUserRequest } from '../../../core/models/user';
import { RoleUtilisateur } from '../../../core/models/enums';
import { TopbarComponent } from '../../../shared/components/topbar/topbar.component';
import { IconComponent } from '../../../shared/components/icon/icon.component';
import { TranslateService, TranslatePipe } from '@ngx-translate/core';

type RoleFilter = 'all' | RoleUtilisateur;
type ModalMode  = 'create' | 'edit';

@Component({
  selector: 'app-utilisateurs',
  standalone: true,
  host: { class: 'flex flex-col flex-1 min-w-0 overflow-hidden' },
  imports: [TopbarComponent, IconComponent, ReactiveFormsModule, FormsModule, DatePipe, TranslatePipe],
  templateUrl: './utilisateurs.component.html',
})
export class UtilisateursComponent implements OnInit {
  users: UserResponse[] = [];
  loading  = true;
  saving   = false;
  deleting: number | null = null;
  error: string | null = null;

  showModal  = false;
  modalMode: ModalMode = 'create';
  editingId: number | null = null;

  search     = '';
  roleFilter: RoleFilter = 'all';
  isMobile   = window.innerWidth < 768;

  @HostListener('window:resize')
  onResize(): void { this.isMobile = window.innerWidth < 768; }

  readonly roleOptions: RoleUtilisateur[] = [
    RoleUtilisateur.ROLE_ADMIN,
    RoleUtilisateur.ROLE_MANAGER,
    RoleUtilisateur.ROLE_AGENT,
    RoleUtilisateur.ROLE_CLIENT,
  ];

  readonly roleFilterTabs = [
    { id: 'all',                        label: 'users.tab.all' },
    { id: RoleUtilisateur.ROLE_ADMIN,   label: 'Admin'         },
    { id: RoleUtilisateur.ROLE_MANAGER, label: 'Manager'       },
    { id: RoleUtilisateur.ROLE_AGENT,   label: 'Agent'         },
    { id: RoleUtilisateur.ROLE_CLIENT,  label: 'Client'        },
  ];

  form = this.fb.group({
    nom:        ['', Validators.required],
    prenom:     ['', Validators.required],
    email:      ['', [Validators.required, Validators.email]],
    telephone:  [''],
    pays:       ['MA', Validators.required],
    motDePasse: [''],
    role:       [RoleUtilisateur.ROLE_AGENT, Validators.required],
  });

  constructor(
    private userService: UserService,
    private fb: FormBuilder,
    private translate: TranslateService,
  ) {}

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading = true;
    this.userService.findAll().subscribe({
      next:  data => { this.users = data; this.loading = false; },
      error: ()   => { this.loading = false; },
    });
  }

  get pageSubtitle(): string {
    return `${this.users.length} ${this.translate.instant('users.subtitle')}`;
  }

  get filtered(): UserResponse[] {
    return this.users.filter(u => {
      const q = this.search.toLowerCase();
      const matchSearch = !q ||
        `${u.prenom} ${u.nom}`.toLowerCase().includes(q) ||
        u.email.toLowerCase().includes(q);
      const matchRole = this.roleFilter === 'all' || u.role === this.roleFilter;
      return matchSearch && matchRole;
    });
  }

  toggleUser(u: UserResponse): void {
    const obs = u.actif
      ? this.userService.desactiver(u.id)
      : this.userService.reactiver(u.id);
    obs.subscribe({ next: () => { u.actif = !u.actif; } });
  }

  openCreate(): void {
    this.modalMode = 'create';
    this.editingId = null;
    this.form.reset({ role: RoleUtilisateur.ROLE_AGENT, pays: 'MA' });
    this.form.get('motDePasse')!.setValidators([Validators.required, Validators.minLength(8)]);
    this.form.get('motDePasse')!.updateValueAndValidity();
    this.error = null;
    this.showModal = true;
  }

  openEdit(u: UserResponse): void {
    this.modalMode = 'edit';
    this.editingId = u.id;
    this.form.patchValue({
      nom: u.nom, prenom: u.prenom, email: u.email,
      telephone: u.telephone, pays: u.pays, role: u.role as RoleUtilisateur,
      motDePasse: '',
    });
    this.form.get('motDePasse')!.clearValidators();
    this.form.get('motDePasse')!.updateValueAndValidity();
    this.error = null;
    this.showModal = true;
  }

  closeModal(): void { this.showModal = false; }

  submit(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid) return;
    this.saving = true;
    this.error  = null;

    if (this.modalMode === 'create') {
      this.userService.create(this.form.value as CreateUserRequest).subscribe({
        next: user => {
          this.users.unshift(user);
          this.saving = false;
          this.showModal = false;
        },
        error: () => {
          this.error  = this.translate.instant('users.error.create');
          this.saving = false;
        },
      });
    } else {
      const { nom, prenom, email, telephone, pays } = this.form.value;
      this.userService.updateById(this.editingId!, { nom: nom!, prenom: prenom!, email: email!, telephone: telephone ?? undefined, pays: pays! }).subscribe({
        next: updated => {
          const idx = this.users.findIndex(u => u.id === this.editingId);
          if (idx !== -1) this.users[idx] = updated;
          this.saving = false;
          this.showModal = false;
        },
        error: () => {
          this.error  = this.translate.instant('users.error.update');
          this.saving = false;
        },
      });
    }
  }

  deleteUser(u: UserResponse): void {
    if (!confirm(`${this.translate.instant('users.deleteConfirm')} ${u.prenom} ${u.nom} ?`)) return;
    this.deleting = u.id;
    this.userService.deleteById(u.id).subscribe({
      next: () => {
        this.users = this.users.filter(x => x.id !== u.id);
        this.deleting = null;
      },
      error: () => { this.deleting = null; },
    });
  }

  roleBadgeBg(role: string): string {
    return role === 'ROLE_ADMIN'   ? 'rgba(124,58,237,0.18)'  :
           role === 'ROLE_MANAGER' ? 'rgba(29,78,216,0.20)'   :
           role === 'ROLE_AGENT'   ? 'rgba(15,118,110,0.20)'  :
                                     'rgba(140,143,163,0.20)';
  }
  roleBadgeFg(role: string): string {
    return role === 'ROLE_ADMIN'   ? '#9D7FFF' :
           role === 'ROLE_MANAGER' ? '#7BB0FF' :
           role === 'ROLE_AGENT'   ? '#5EEAD4' :
                                     '#C3C6D2';
  }
  roleName(role: string): string { return role.replace('ROLE_', ''); }

  initials(u: UserResponse): string {
    return `${u.prenom?.[0] ?? ''}${u.nom?.[0] ?? ''}`.toUpperCase();
  }

  avatarBg(u: UserResponse): string {
    const palette = ['#F5A623','#22C55E','#3B82F6','#A855F7','#EC4899','#14B8A6','#F97316'];
    const hash = `${u.prenom}${u.nom}`.split('').reduce((a, c) => a + c.charCodeAt(0), 0);
    return palette[hash % palette.length];
  }
}
