import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {Router, RouterModule} from '@angular/router';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import {AuthService} from "../../../core/services/auth.service";
import {SidebarStateService} from "../../../core/services/sidebar-state.service";

interface NavItem {
  labelKey: string;
  label: string;
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
export class NavbarComponent implements OnInit {

  languages = ['fr', 'en', 'ar'];
  currentLang = 'fr';

  navItems: NavItem[] = [
    { labelKey: 'NAV.ACCUEIL',       label: '', route: '/client/dashboard',     exact: true  },
    { labelKey: 'NAV.ENVOYER',       label: '', route: '/client/envoyer',        exact: false },
    { labelKey: 'NAV.MES_TRANSFERTS',label: '', route: '/client/historique',     exact: false },
    { labelKey: 'NAV.BENEFICIAIRES', label: '', route: '/client/beneficiaires',  exact: false },
    { labelKey: 'NAV.AIDE',          label: '', route: '/client/aide',           exact: false },
    {labelKey : 'profil', label: '', route: '/client/profil', exact: false}
  ];

  constructor(private translate: TranslateService,  public auth: AuthService) {}

  ngOnInit(): void {
    this.translate.setDefaultLang('fr');
    this.translate.use(this.currentLang);
    this.updateLabels();
    this.translate.onLangChange.subscribe(() => this.updateLabels());
  }

  switchLang(lang: string): void {
    this.currentLang = lang;
    this.translate.use(lang);
    document.dir = lang === 'ar' ? 'rtl' : 'ltr';
  }

  private updateLabels(): void {
    this.navItems.forEach(item => {
      this.translate.get(item.labelKey).subscribe(val => item.label = val);
    });
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
