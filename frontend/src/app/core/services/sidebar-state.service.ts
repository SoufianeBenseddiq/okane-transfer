import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class SidebarStateService {
  private _collapsed = localStorage.getItem('sidebar_collapsed') === 'true';

  get collapsed(): boolean { return this._collapsed; }

  toggle(): void {
    this._collapsed = !this._collapsed;
    localStorage.setItem('sidebar_collapsed', String(this._collapsed));
  }
}
