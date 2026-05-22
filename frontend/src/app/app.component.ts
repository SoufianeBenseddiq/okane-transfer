import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {TranslatePipe, TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, TranslatePipe],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'frontend';

  constructor(private translate: TranslateService) {
    // Définir la langue active au démarrage
    this.translate.use('fr');
  }

  changerLangue(lang: string) {
    this.translate.use(lang);
  }
}
