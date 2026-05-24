import { Component } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { SidebarStateService } from '../../../core/services/sidebar-state.service';
import { TranslationService } from '../../../core/services/translation.service';
import { RoleUtilisateur } from '../../../core/models/enums';
import { IconComponent } from '../icon/icon.component';
import { TranslatePipe } from '../../pipes/translate.pipe';

interface NavItem {
  id: string;
  label: string;
  icon: string;
  route: string;
  badge?: number;
}

const NAV_CONFIG: Record<string, NavItem[]> = {
  [RoleUtilisateur.ROLE_ADMIN]: [
    { id: 'dashboard',    label: 'nav.dashboard',    icon: 'dashboard', route: '/admin/dashboard' },
    { id: 'devises',      label: 'nav.devises',      icon: 'currency',  route: '/admin/devises' },
    { id: 'frais',        label: 'nav.frais',        icon: 'percent',   route: '/admin/frais' },
    { id: 'agences',      label: 'nav.agences',      icon: 'building',  route: '/admin/agences' },
    { id: 'utilisateurs', label: 'nav.utilisateurs', icon: 'users',     route: '/admin/utilisateurs' },
    { id: 'conformite',   label: 'nav.conformite',   icon: 'shield',    route: '/admin/conformite', badge: 7 },
    { id: 'audit',        label: 'nav.audit',        icon: 'list',      route: '/admin/audit' },
    { id: 'rapports',     label: 'nav.rapports',     icon: 'report',    route: '/admin/rapports' },
  ],
  [RoleUtilisateur.ROLE_MANAGER]: [
    { id: 'dashboard',    label: 'nav.dashboard',   icon: 'dashboard', route: '/manager/dashboard' },
    { id: 'validations',  label: 'nav.validations', icon: 'check',     route: '/manager/validations' },
    { id: 'equipe',       label: 'nav.equipe',      icon: 'users',     route: '/manager/equipe' },
    { id: 'plafonds',     label: 'nav.plafonds',    icon: 'wallet',    route: '/manager/plafonds' },
    { id: 'rapports',     label: 'nav.rapports',    icon: 'report',    route: '/manager/rapports' },
  ],
  [RoleUtilisateur.ROLE_AGENT]: [
    { id: 'dashboard', label: 'nav.tableau',   icon: 'dashboard',     route: '/agent/dashboard' },
    { id: 'transfert', label: 'nav.transfert', icon: 'send',          route: '/agent/transfert' },
    { id: 'paiement',  label: 'nav.paiement',  icon: 'arrowDownLeft', route: '/agent/paiement' },
    { id: 'caisse',    label: 'nav.caisse',    icon: 'wallet',        route: '/agent/caisse' },
    { id: 'cloture',   label: 'nav.cloture',   icon: 'lock',          route: '/agent/cloture' },
  ],
};

const ROLE_COLORS: Record<string, string> = {
  [RoleUtilisateur.ROLE_ADMIN]:   '#F5A623',
  [RoleUtilisateur.ROLE_MANAGER]: '#7BB0FF',
  [RoleUtilisateur.ROLE_AGENT]:   '#5EEAD4',
  [RoleUtilisateur.ROLE_CLIENT]:  '#9D7FFF',
};

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, IconComponent, TranslatePipe],
  templateUrl: './sidebar.component.html',
})
export class SidebarComponent {
  constructor(
    public auth: AuthService,
    private router: Router,
    public sidebarState: SidebarStateService,
    public ts: TranslationService,
  ) {}

  get collapsed(): boolean { return this.sidebarState.collapsed; }
  toggleCollapse(): void { this.sidebarState.toggle(); }

  get navItems(): NavItem[] {
    return NAV_CONFIG[this.auth.currentUser?.role ?? ''] ?? [];
  }

  get initials(): string {
    const u = this.auth.currentUser;
    if (!u) return '?';
    return `${u.prenom?.[0] ?? ''}${u.nom?.[0] ?? ''}`.toUpperCase();
  }

  get roleColor(): string {
    return ROLE_COLORS[this.auth.currentUser?.role ?? ''] ?? '#F5A623';
  }

  get roleName(): string {
    return (this.auth.currentUser?.role ?? '').replace('ROLE_', '');
  }

  get userName(): string {
    const u = this.auth.currentUser;
    return u ? `${u.prenom} ${u.nom}` : '';
  }

  logout(): void {
    this.auth.logout().subscribe({
      next:  () => this.router.navigate(['/auth/login']),
      error: () => { this.auth.clearSession(); this.router.navigate(['/auth/login']); },
    });
  }
}
