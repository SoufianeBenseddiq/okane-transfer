import { Component, Input } from '@angular/core';
import { IconComponent } from '../icon/icon.component';
import { SidebarStateService } from '../../../core/services/sidebar-state.service';
import { TranslationService, Lang } from '../../../core/services/translation.service';

@Component({
  selector: 'app-topbar',
  standalone: true,
  imports: [IconComponent],
  templateUrl: './topbar.component.html',
})
export class TopbarComponent {
  @Input() breadcrumb = '';
  @Input() title = '';
  @Input() subtitle = '';

  readonly langs: Lang[] = ['FR', 'EN', 'AR'];

  constructor(
    public sidebarState: SidebarStateService,
    public ts: TranslationService,
  ) {}

  get lang(): Lang { return this.ts.lang; }
  setLang(l: Lang): void { this.ts.setLang(l); }
  toggleSidebar(): void { this.sidebarState.toggle(); }
}
