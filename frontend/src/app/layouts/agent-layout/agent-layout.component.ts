import { Component, OnInit } from '@angular/core';
import { RouterOutlet, Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { TranslateService } from '@ngx-translate/core';
import { SidebarComponent } from '../../shared/components/sidebar/sidebar.component';
import { TopbarComponent } from '../../shared/components/topbar/topbar.component';
import { ToastComponent } from '../../shared/components/toast/toast.component';

@Component({
  selector: 'app-agent-layout',
  standalone: true,
  imports: [RouterOutlet, SidebarComponent, TopbarComponent, ToastComponent],
  template: `
    <div class="flex overflow-hidden font-sans" style="height:100vh;background:#1A1F36;color:#F3F4F8;">
      <app-sidebar />
      <div class="flex flex-col flex-1 min-w-0 overflow-hidden">
        <app-topbar [breadcrumb]="t('agent.space')" [title]="pageTitle" />
        <div class="flex-1 overflow-y-auto p-4 sm:p-7">
          <router-outlet />
        </div>
      </div>
    </div>
    <app-toast />
  `,
})
export class AgentLayoutComponent implements OnInit {

  private currentUrl = '';

  private readonly routeKeys: Record<string, string> = {
    '/agent/dashboard':  'nav.tableau',
    '/agent/envoi':      'nav.transfert',
    '/agent/paiement':   'nav.paiement',
    '/agent/caisse':     'nav.caisse',
    '/agent/historique': 'nav.historique',
  };

  constructor(private router: Router, private translate: TranslateService) {}

  ngOnInit(): void {
    this.currentUrl = this.router.url;
    this.router.events
      .pipe(filter(e => e instanceof NavigationEnd))
      .subscribe((e: any) => { this.currentUrl = e.urlAfterRedirects; });
  }

  t(key: string): string { return this.translate.instant(key); }

  get pageTitle(): string {
    return this.translate.instant(this.routeKeys[this.currentUrl] ?? 'nav.tableau');
  }
}
