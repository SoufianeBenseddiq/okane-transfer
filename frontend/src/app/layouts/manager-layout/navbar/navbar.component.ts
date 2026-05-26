import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, TranslatePipe],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent {
  currentLanguage = 'fr';
  languages = ['fr', 'en', 'ar'];
  notificationCount = 3;

  constructor(
    private translate: TranslateService,
    private router: Router
  ) {}

  changeLanguage(lang: string) {
    this.currentLanguage = lang;
    this.translate.use(lang);
  }

  logout() {
    console.log('Logout clicked');
  }

  exportReport() {
    console.log('Export report clicked');
  }
}
