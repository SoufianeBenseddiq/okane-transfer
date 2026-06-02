import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService, Toast } from '../../../core/services/toast.service';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="fixed top-5 right-5 z-[9999] flex flex-col gap-3 pointer-events-none"
         style="max-width:400px;">
      @for (toast of toastService.toasts(); track toast.id) {
        <div class="flex items-start gap-3 px-4 py-3.5 rounded-[12px] pointer-events-auto
                    shadow-2xl backdrop-blur-sm animate-slide-in"
             [style]="toastStyle(toast)">

          <!-- Icon -->
          <div class="flex-shrink-0 mt-0.5">
            @if (toast.type === 'error') {
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none"
                   [attr.stroke]="iconColor(toast.type)" stroke-width="2"
                   stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="10"/>
                <path d="M12 8v4M12 16h.01"/>
              </svg>
            }
            @if (toast.type === 'warning') {
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none"
                   [attr.stroke]="iconColor(toast.type)" stroke-width="2"
                   stroke-linecap="round" stroke-linejoin="round">
                <path d="M10.3 3.9 1.8 18a2 2 0 0 0 1.7 3h17a2 2 0 0 0 1.7-3L13.7 3.9a2 2 0 0 0-3.4 0z"/>
                <path d="M12 9v4M12 17h.01"/>
              </svg>
            }
            @if (toast.type === 'success') {
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none"
                   [attr.stroke]="iconColor(toast.type)" stroke-width="2"
                   stroke-linecap="round" stroke-linejoin="round">
                <path d="M12 3l8 3v6c0 5-3.5 8.5-8 9.5-4.5-1-8-4.5-8-9.5V6z"/>
                <path d="M9 12l2 2 4-4"/>
              </svg>
            }
            @if (toast.type === 'info') {
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none"
                   [attr.stroke]="iconColor(toast.type)" stroke-width="2"
                   stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="10"/>
                <path d="M12 16v-4M12 8h.01"/>
              </svg>
            }
          </div>

          <!-- Message -->
          <div class="flex-1 min-w-0">
            <p class="text-[13px] font-semibold leading-snug" [style.color]="textColor(toast.type)">
              {{ toastTitle(toast.type) }}
            </p>
            <p class="text-[12px] mt-0.5 leading-relaxed" style="color:rgba(255,255,255,0.7);">
              {{ toast.message }}
            </p>
          </div>

          <!-- Close -->
          <button (click)="toastService.dismiss(toast.id)"
                  class="flex-shrink-0 cursor-pointer border-none bg-transparent outline-none opacity-60 hover:opacity-100 transition-opacity"
                  style="color:#fff;">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                 stroke-width="2.5" stroke-linecap="round">
              <path d="M18 6L6 18M6 6l12 12"/>
            </svg>
          </button>
        </div>
      }
    </div>
  `,
  styles: [`
    @keyframes slide-in {
      from { transform: translateX(120%); opacity: 0; }
      to   { transform: translateX(0);   opacity: 1; }
    }
    .animate-slide-in { animation: slide-in 0.3s cubic-bezier(0.34,1.56,0.64,1) forwards; }
  `],
})
export class ToastComponent {
  readonly toastService = inject(ToastService);

  toastStyle(toast: Toast): string {
    const styles: Record<string, string> = {
      error:   'background:rgba(30,10,10,0.92);border:1px solid rgba(255,77,79,0.4);',
      warning: 'background:rgba(30,25,10,0.92);border:1px solid rgba(250,173,20,0.4);',
      success: 'background:rgba(10,30,20,0.92);border:1px solid rgba(0,196,140,0.4);',
      info:    'background:rgba(10,20,35,0.92);border:1px solid rgba(123,176,255,0.4);',
    };
    return styles[toast.type] ?? styles['info'];
  }

  iconColor(type: string): string {
    const colors: Record<string, string> = {
      error: '#FF4D4F', warning: '#FAAD14', success: '#00C48C', info: '#7BB0FF',
    };
    return colors[type] ?? '#7BB0FF';
  }

  textColor(type: string): string { return this.iconColor(type); }

  toastTitle(type: string): string {
    const titles: Record<string, string> = {
      error:   'Accès refusé',
      warning: 'Attention',
      success: 'Succès',
      info:    'Information',
    };
    return titles[type] ?? '';
  }
}
