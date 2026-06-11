import { Component, Input, OnInit, OnDestroy, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { IconComponent } from '../icon/icon.component';
import { SidebarStateService } from '../../../core/services/sidebar-state.service';
import { LanguageService } from '../../../core/services/language.service';
import { AuthService } from '../../../core/services/auth.service';
import { NotificationService } from '../../../core/services/notification.service';
import { NotificationResponse } from '../../../core/models/notification';

@Component({
  selector: 'app-topbar',
  standalone: true,
  imports: [IconComponent, CommonModule],
  templateUrl: './topbar.component.html',
})
export class TopbarComponent implements OnInit, OnDestroy {
  @Input() breadcrumb = '';
  @Input() title = '';
  @Input() subtitle = '';

  unreadCount  = 0;
  notifications: NotificationResponse[] = [];
  dropdownOpen = false;

  private refreshInterval: any;

  constructor(
    public sidebarState: SidebarStateService,
    public lang: LanguageService,
    public auth: AuthService,
    private notifService: NotificationService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.refreshCounts();
    // Refresh every 30s
    this.refreshInterval = setInterval(() => this.refreshCounts(), 30_000);
  }

  ngOnDestroy(): void {
    clearInterval(this.refreshInterval);
  }

  private refreshCounts(): void {
    this.notifService.getMyUnreadCount().subscribe({
      next: count => { this.unreadCount = count; },
      error: () => {},
    });
  }

  toggleDropdown(): void {
    this.dropdownOpen = !this.dropdownOpen;
    if (this.dropdownOpen) this.loadNotifications();
  }

  private loadNotifications(): void {
    this.notifService.getMesNotifications().subscribe({
      next: list => {
        // Show latest 10 sorted by most recent
        this.notifications = [...list]
          .sort((a, b) => new Date(b.envoyeLe).getTime() - new Date(a.envoyeLe).getTime())
          .slice(0, 5);
      },
      error: () => {},
    });
  }

  markAsRead(notif: NotificationResponse, event: Event): void {
    event.stopPropagation();
    if (notif.lue) return;
    this.notifService.markAsRead(notif.id).subscribe({
      next: () => {
        notif.lue = true;
        this.unreadCount = Math.max(0, this.unreadCount - 1);
      },
      error: () => {},
    });
  }

  markAllAsRead(): void {
    this.notifService.markAllMyAsRead().subscribe({
      next: () => {
        this.notifications.forEach(n => n.lue = true);
        this.unreadCount = 0;
      },
      error: () => {},
    });
  }

  formatTime(dateStr: string): string {
    const d = new Date(dateStr);
    const now = new Date();
    const diffMs = now.getTime() - d.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    if (diffMins < 1)  return 'À l\'instant';
    if (diffMins < 60) return `Il y a ${diffMins} min`;
    const diffH = Math.floor(diffMins / 60);
    if (diffH < 24)    return `Il y a ${diffH}h`;
    return d.toLocaleDateString('fr-FR');
  }

  notifTitle(msg: string): string {
    const lines = msg.split('\n');
    return lines[0] ?? '';
  }

  notifBody(msg: string): string {
    const lines = msg.split('\n');
    return lines.slice(1).join(' ').trim();
  }

  @HostListener('document:click', ['$event'])
  onOutsideClick(event: Event): void {
    const target = event.target as HTMLElement;
    if (!target.closest('.notif-dropdown-container')) {
      this.dropdownOpen = false;
    }
  }

  toggleSidebar(): void { this.sidebarState.toggle(); }
}
