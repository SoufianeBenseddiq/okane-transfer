import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { UserService } from '../../../core/services/user.service';
import { UserResponse, UpdateProfilRequest } from '../../../core/models/user/index';
import { AuthService } from '../../../core/services/auth.service';
import { Router } from '@angular/router';

interface NotifRow {
  labelKey: string;
  email: boolean;
  sms: boolean;
  required?: boolean;
  smsLocked?: boolean;
}

interface LangOption {
  code: string;
  label: string;
  flag: string; // code pour flag-icons (fi fi-xx)
}

@Component({
  selector: 'app-profil',
  standalone: true,
  imports: [CommonModule, FormsModule, TranslateModule],
  templateUrl: './profil.component.html',
  styleUrls: ['./profil.component.scss'],
})
export class ProfilComponent implements OnInit {

  user: UserResponse | null = null;
  loading = true;

  editField: string | null = null;
  editValue = '';

  successMsg = '';   // clé de traduction
  errorMsg   = '';    // clé de traduction

  notifications: NotifRow[] = [
    { labelKey: 'profil.notifications.items.transfertEnvoye',         email: true,  sms: true  },
    { labelKey: 'profil.notifications.items.transfertPaye',           email: true,  sms: true  },
    { labelKey: 'profil.notifications.items.codeRetraitCommunique',   email: true,  sms: false },
    { labelKey: 'profil.notifications.items.alerteSecurite',          email: true,  sms: true,  required: true, smsLocked: true },
    { labelKey: 'profil.notifications.items.newsletter',              email: false, sms: false },
  ];

  showPasswordForm = false;
  currentPassword  = '';
  newPassword      = '';
  confirmPassword  = '';
  passwordSuccess  = '';  // clé de traduction
  passwordError    = '';  // clé de traduction

  showCurrent = false;
  showNew     = false;
  showConfirm = false;

  // ─── Langues ──────────────────────────────────────────────────────────────
  availableLanguages: LangOption[] = [
    { code: 'fr', label: 'Français', flag: 'fr' },
    { code: 'en', label: 'English',  flag: 'gb' },
    { code: 'ar', label: 'العربية',   flag: 'ma' },
  ];

  currentLang = 'fr';

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private router: Router,
    private translate: TranslateService,
  ) {}

  ngOnInit(): void {
    this.currentLang =
      this.translate.currentLang ||
      localStorage.getItem('appLang') ||
      this.translate.getDefaultLang() ||
      'fr';

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
      nom:       this.user.nom       ?? undefined,
      prenom:    this.user.prenom    ?? undefined,
      email:     this.user.email     ?? undefined,
      telephone: this.user.telephone ?? undefined,
      pays:      this.user.pays      ?? undefined,
      [field]:   this.editValue,
    };

    this.userService.updateMe(request).subscribe({
      next: (updated) => {
        this.user       = updated;
        this.editField  = null;
        this.editValue  = '';
        this.successMsg = 'profil.personalInfo.updateSuccess';
        this.errorMsg   = '';
        setTimeout(() => (this.successMsg = ''), 3000);
      },
      error: () => {
        this.errorMsg   = 'profil.personalInfo.updateError';
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

  changePassword(): void {
    if (this.newPassword !== this.confirmPassword) {
      this.passwordError = 'profil.security.passwordMismatch';
      return;
    }
    this.userService.changePassword(this.currentPassword, this.newPassword).subscribe({
      next: () => {
        this.passwordSuccess  = 'profil.security.passwordSuccess';
        this.passwordError    = '';
        this.showPasswordForm = false;
        this.currentPassword  = '';
        this.newPassword      = '';
        this.confirmPassword  = '';
        setTimeout(() => (this.passwordSuccess = ''), 3000);
      },
      error: (err) => {
        this.passwordError   = err.status === 500
          ? 'profil.security.passwordIncorrect'
          : 'profil.security.passwordError';
        this.passwordSuccess = '';
      },
    });
  }

  // ─── Langue ───────────────────────────────────────────────────────────────
  changeLanguage(lang: string): void {
    if (lang === this.currentLang) return;

    this.translate.use(lang);
    localStorage.setItem('appLang', lang);
    this.currentLang = lang;

    document.documentElement.lang = lang;
    document.documentElement.dir = lang === 'ar' ? 'rtl' : 'ltr';
  }
}