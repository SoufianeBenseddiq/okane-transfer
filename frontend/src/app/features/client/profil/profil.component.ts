import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../../core/services/user.service';
import { UserResponse, UpdateProfilRequest } from '../../../core/models/user/index';
import {AuthService} from "../../../core/services/auth.service";
import {Router} from "@angular/router";

interface NotifRow {
  label: string;
  email: boolean;
  sms: boolean;
  required?: boolean;
  smsLocked?: boolean;
}

@Component({
  selector: 'app-profil',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profil.component.html',
})
export class ProfilComponent implements OnInit {

  user: UserResponse | null = null;
  loading = true;

  editField: string | null = null;
  editValue = '';          // valeur courante de l'input en cours d'édition

  successMsg = '';
  errorMsg   = '';

  notifications: NotifRow[] = [
    { label: 'Transfert envoyé',           email: true,  sms: true  },
    { label: 'Transfert payé',             email: true,  sms: true  },
    { label: 'Code de retrait communiqué', email: true,  sms: false },
    { label: 'Alerte sécurité',            email: true,  sms: true,  required: true, smsLocked: true },
    { label: 'Newsletter & offres',         email: false, sms: false },
  ];

  showPasswordForm = false;
  currentPassword  = '';
  newPassword      = '';
  confirmPassword  = '';
  passwordSuccess  = '';
  passwordError    = '';

  showCurrent = false;
  showNew     = false;
  showConfirm = false;

  constructor(private userService: UserService, private authService : AuthService,  private router: Router,) {}

  ngOnInit(): void {
    this.userService.getMe().subscribe({
      next: (u) => { this.user = u; this.loading = false; },
      error: ()  => { this.loading = false; },
    });
  }

  get initials(): string {
    if (!this.user) return '';
    return `${this.user.prenom?.[0] ?? ''}${this.user.nom?.[0] ?? ''}`.toUpperCase();
  }

  startEdit(field: string): void {
    this.editField = field;
    this.editValue = ((this.user as any)?.[field] ?? '') as string;
    this.successMsg = '';
    this.errorMsg   = '';
  }

  cancelEdit(): void {
    this.editField = null;
    this.editValue = '';
  }

  saveField(field: string): void {
    if (!this.user) return;

    const request: UpdateProfilRequest = {
      nom:           this.user.nom           ?? undefined,
      prenom:        this.user.prenom        ?? undefined,
      email:         this.user.email         ?? undefined,
      telephone:     this.user.telephone     ?? undefined,
      pays: this.user.pays ?? undefined,
      [field]:       this.editValue,
    };

    this.userService.updateMe(request).subscribe({
      next: (updated) => {
        this.user       = updated;
        this.editField  = null;
        this.editValue  = '';
        this.successMsg = 'Informations mises à jour.';
        this.errorMsg   = '';
        setTimeout(() => (this.successMsg = ''), 3000);
      },
      error: (err) => {
        this.errorMsg   = err?.error?.message ?? 'Une erreur est survenue.';
        this.successMsg = '';
      },
    });
  }

  toggleNotif(notif: NotifRow, channel: 'email' | 'sms'): void {
    notif[channel] = !notif[channel];
  }

  logout(): void {
    this.authService.logout().subscribe({
      next:  () => this.router.navigate(['/auth/login']),
      error: () => { this.authService.clearSession(); this.router.navigate(['/auth/login']); },
    });
  }

  // changePassword(): void {
  //   if (this.newPassword !== this.confirmPassword) {
  //     this.passwordError = 'Les mots de passe ne correspondent pas.';
  //     return;
  //   }
  //   this.userService.changePassword(this.currentPassword, this.newPassword).subscribe({
  //     next: () => {
  //       this.passwordSuccess  = 'Mot de passe modifié avec succès.';
  //       this.passwordError    = '';
  //       this.showPasswordForm = false;
  //       this.currentPassword  = '';
  //       this.newPassword      = '';
  //       this.confirmPassword  = '';
  //       setTimeout(() => (this.passwordSuccess = ''), 3000);
  //     },
  //     error: (err) => {
  //       this.passwordError   = err?.error?.message ?? 'Mot de passe actuel incorrect.';
  //       this.passwordSuccess = '';
  //     },
  //   });
  // }

  changePassword(): void {
    if (this.newPassword !== this.confirmPassword) {
      this.passwordError = 'Les mots de passe ne correspondent pas.';
      return;
    }
    this.userService.changePassword(this.currentPassword, this.newPassword).subscribe({
      next: () => {
        this.passwordSuccess  = 'Mot de passe modifié avec succès.';
        this.passwordError    = '';
        this.showPasswordForm = false;
        this.currentPassword  = '';
        this.newPassword      = '';
        this.confirmPassword  = '';
        setTimeout(() => (this.passwordSuccess = ''), 3000);
      },
      error: (err) => {
        this.passwordError   = err.status === 500
          ? 'Mot de passe actuel incorrect.'
          : (err?.error?.message ?? 'Une erreur est survenue.');
        this.passwordSuccess = '';
      },
    });
  }
}
