import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterModule, Router, NavigationEnd } from '@angular/router';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-agent-layout',
  standalone: true,
  imports: [CommonModule, RouterModule, TranslateModule, DatePipe],
  templateUrl: './agent-layout.component.html',
  styleUrls: ['./agent-layout.component.scss']
})
export class AgentLayoutComponent implements OnInit {
  today = new Date();
  currentLang = 'fr';
  langs = ['fr', 'en', 'ar'];
  pageTitleKey = 'pages.dashboard';

  private routeTitleKeys: Record<string, string> = {
    '/agent/dashboard': 'pages.dashboard',
    '/agent/envoi':     'pages.envoi',
    '/agent/paiement':  'pages.paiement',
    '/agent/caisse':    'pages.caisse',
    '/agent/historique':'pages.historique',
  };

  constructor(
    private translate: TranslateService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.currentLang = this.translate.currentLang || this.translate.defaultLang || 'fr';

    this.pageTitleKey = this.routeTitleKeys[this.router.url] || 'pages.dashboard';

    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: any) => {
      this.pageTitleKey = this.routeTitleKeys[event.urlAfterRedirects] || 'pages.dashboard';
    });
  }

  switchLang(lang: string): void {
    this.currentLang = lang;
    this.translate.use(lang);
    document.documentElement.dir = lang === 'ar' ? 'rtl' : 'ltr';
    document.documentElement.lang = lang;
  }
}