import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {TranslatePipe, TranslateService} from "@ngx-translate/core";
import {AgentLayoutComponent} from "./layouts/agent-layout/agent-layout.component";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, TranslatePipe, AgentLayoutComponent],
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
