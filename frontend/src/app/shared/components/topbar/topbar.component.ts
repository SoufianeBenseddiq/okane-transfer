import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { IconComponent } from '../icon/icon.component';
import { SidebarStateService } from '../../../core/services/sidebar-state.service';
import { LanguageService } from '../../../core/services/language.service';
import { AuthService } from '../../../core/services/auth.service';
import { NotificationService } from '../../../core/services/notification.service';
import { RoleUtilisateur } from '../../../core/models/enums';

@Component({
  selector: 'app-topbar',
  standalone: true,
  imports: [IconComponent],
  templateUrl: './topbar.component.html',
})
export class TopbarComponent implements OnInit {
  @Input() breadcrumb = '';
  @Input() title = '';
  @Input() subtitle = '';

  unreadCount = 0;

  constructor(
    public sidebarState: SidebarStateService,
    public lang: LanguageService,
    private auth: AuthService,
    private notifService: NotificationService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    if (this.auth.currentUser?.role === RoleUtilisateur.ROLE_ADMIN) {
      this.notifService.getUnreadCount().subscribe({
        next: count => { this.unreadCount = count; },
      });
    }
  }

  toggleSidebar(): void { this.sidebarState.toggle(); }

  openNotifications(): void {
    if (this.auth.currentUser?.role === RoleUtilisateur.ROLE_ADMIN) {
      this.router.navigate(['/admin/notifications']);
    }
  }
}
