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
  :host nav { background: #0d1b2a; border-bottom: 1px solid rgba(255,255,255,0.07); }
  .nav-link { color: #7a92a8; }
  .nav-link:hover { background: rgba(245,166,35,0.08); color: #f5a623; }
  .active-link { background: rgba(245,166,35,0.10); color: #f5a623; font-weight: 600; }
  .lang-btn { color: #7a92a8; background: transparent; }
  .lang-btn.active-lang { background: #f5a623; color: #0d1b2a; }
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