import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SidebarComponent } from '../../shared/components/sidebar/sidebar.component';

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [RouterOutlet, SidebarComponent],
  template: `
    <div class="flex overflow-hidden font-sans" style="height:100vh;background:#1A1F36;color:#F3F4F8;">
      <app-sidebar />
      <div class="flex flex-col flex-1 min-w-0 overflow-hidden">
        <router-outlet />
      </div>
    </div>
  `,
})
export class AdminLayoutComponent {}
