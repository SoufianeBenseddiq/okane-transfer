import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';

interface MenuItem {
  icon: string;
  label: string;
  route: string;
  badge?: number;
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, TranslatePipe],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss'
})
export class SidebarComponent implements OnInit {
  isCollapsed = false;
  currentRoute: string = '';

  menuItems: MenuItem[] = [
    { icon: 'dashboard', label: 'Dashboard', route: '/manager/dashboard' },
    { icon: 'team', label: 'Mon équipe', route: '/manager/agents' },
    { icon: 'clock', label: 'Historique', route: '/manager/caisse' },
    { icon: 'report', label: 'Rapport d\'agence', route: '/manager/rapports-agence' },
    { icon: 'gauge', label: 'Plafond journalier', route: '/manager/plafond' }
  ];

  constructor(private router: Router) {}

  ngOnInit() {
    this.currentRoute = this.router.url;
  }

  toggleSidebar() {
    this.isCollapsed = !this.isCollapsed;
  }

  isActive(route: string): boolean {
    return this.router.url.includes(route);
  }
}
