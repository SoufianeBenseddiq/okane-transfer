import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { HttpClient, provideHttpClient } from '@angular/common/http';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import {provideTranslateHttpLoader, TranslateHttpLoader} from '@ngx-translate/http-loader';

// Nouvelle signature simplifiée pour la v17+ de ngx-translate
export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader();
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(),
    importProvidersFrom(
      TranslateModule.forRoot({
        loader: {
          provide: TranslateLoader,
          useFactory: HttpLoaderFactory,
          deps: [HttpClient]
        },
        defaultLanguage: 'fr'
      })
    ),
    // C'est ici que l'on passe proprement le chemin et l'extension du fichier
    provideTranslateHttpLoader({
      prefix: './assets/i18n/',
      suffix: '.json'
    })
  ]
};
