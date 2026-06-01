import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Injectable({ providedIn: 'root' })
export class LanguageService {
  readonly langs = ['fr', 'en', 'ar'] as const;

  constructor(private translate: TranslateService) {}

  init(): void {
    const stored = (localStorage.getItem('lang') ?? 'fr').toLowerCase();
    const lang = (this.langs as readonly string[]).includes(stored) ? stored : 'fr';
    this.use(lang);
  }

  get current(): string {
    return this.translate.currentLang ?? 'fr';
  }

  use(lang: string): void {
    this.translate.use(lang);
    localStorage.setItem('lang', lang);
    document.documentElement.lang = lang;
    document.documentElement.dir = lang === 'ar' ? 'rtl' : 'ltr';
  }
}
