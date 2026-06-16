import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { AuthService } from "../../../core/services/auth.service";

interface NavItem {
  labelKey: string;
  route: string;
  exact: boolean;
}

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule, TranslateModule],
  templateUrl: './navbar.component.html',
  styles: [`
  .nav-link:hover { background: #f5f5f5; color: #1a1f36; }
  .active-link { background: #f0ede8; color: #1a1f36; font-weight: 600; }
  .lang-btn { color: #888; background: transparent; }
  .lang-btn.active-lang { background: #1a1f36; color: #fff; }
`]
})
export class NavbarComponent {

  languages = ['fr', 'en', 'ar'];
  currentLang = 'fr';

  navItems: NavItem[] = [
    { labelKey: 'NAV.ACCUEIL',        route: '/client/dashboard',     exact: true  },
    { labelKey: 'NAV.MES_TRANSFERTS', route: '/client/historique',     exact: false },
    { labelKey: 'NAV.AIDE',           route: '/client/aide',           exact: false },
  ];

  constructor(private translate: TranslateService, public auth: AuthService) {
    this.currentLang = this.translate.currentLang
      || localStorage.getItem('appLang')
      || 'fr';

    this.translate.setDefaultLang('fr');
    this.translate.use(this.currentLang);
    document.documentElement.dir = this.currentLang === 'ar' ? 'rtl' : 'ltr';
  }

  switchLang(lang: string): void {
    if (lang === this.currentLang) return;

    this.currentLang = lang;
    this.translate.use(lang);
    localStorage.setItem('appLang', lang);
    document.documentElement.lang = lang;
    document.documentElement.dir = lang === 'ar' ? 'rtl' : 'ltr';
  }

  get userName(): string {
    const u = this.auth.currentUser;
    return u ? `${u.prenom} ${u.nom}` : '';
  }

  get initials(): string {
    const u = this.auth.currentUser;
    if (!u) return '?';
    return `${u.prenom?.[0] ?? ''}${u.nom?.[0] ?? ''}`.toUpperCase();
  }
}