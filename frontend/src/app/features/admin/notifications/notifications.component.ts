import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { TranslateService, TranslatePipe } from '@ngx-translate/core';
import { NotificationService } from '../../../core/services/notification.service';
import { NotificationResponse } from '../../../core/models/notification';
import { TypeNotification } from '../../../core/models/enums/type-notification.enum';
import { TopbarComponent } from '../../../shared/components/topbar/topbar.component';
import { IconComponent } from '../../../shared/components/icon/icon.component';

type ListFilter = 'all' | 'unread';

@Component({
  selector: 'app-notifications',
  standalone: true,
  host: { class: 'flex flex-col flex-1 min-w-0 overflow-hidden' },
  imports: [TopbarComponent, IconComponent, ReactiveFormsModule, FormsModule, DatePipe, TranslatePipe],
  templateUrl: './notifications.component.html',
})
export class NotificationsComponent implements OnInit {
  notifications: NotificationResponse[] = [];
  loading = true;
  search = '';
  listFilter: ListFilter = 'all';
  sending = false;
  sendSuccess = false;
  markingRead: number | null = null;
  deleting: number | null = null;

  readonly typeOptions = Object.values(TypeNotification);

  form = this.fb.group({
    destinataireId: [null as number | null, [Validators.required, Validators.min(1)]],
    message:        ['', [Validators.required, Validators.minLength(3)]],
    type:           [TypeNotification.SMS, Validators.required],
  });

  constructor(
    private notifService: NotificationService,
    private fb: FormBuilder,
    private translate: TranslateService,
  ) {}

  ngOnInit(): void {
    this.notifService.getAll().subscribe({
      next: data => { this.notifications = data; this.loading = false; },
      error: () => { this.loading = false; },
    });
  }

  get subtitle(): string {
    return `${this.notifications.length} ${this.translate.instant('notifications.subtitle')}`;
  }

  get unreadCount(): number { return this.notifications.filter(n => !n.lue).length; }

  get filtered(): NotificationResponse[] {
    let list = this.notifications;
    if (this.listFilter === 'unread') list = list.filter(n => !n.lue);
    const q = this.search.toLowerCase().trim();
    if (q) list = list.filter(n => n.message.toLowerCase().includes(q) || String(n.destinataireId).includes(q));
    return list;
  }

  send(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid) return;
    this.sending = true;
    this.sendSuccess = false;
    const { destinataireId, message, type } = this.form.value;
    this.notifService.create({ destinataireId: destinataireId!, message: message!, type: type! as TypeNotification }).subscribe({
      next: n => {
        this.notifications.unshift(n);
        this.sending = false;
        this.sendSuccess = true;
        this.form.reset({ type: TypeNotification.SMS });
        setTimeout(() => { this.sendSuccess = false; }, 3000);
      },
      error: () => { this.sending = false; },
    });
  }

  markRead(n: NotificationResponse): void {
    if (n.lue) return;
    this.markingRead = n.id;
    this.notifService.markAsRead(n.id).subscribe({
      next: () => {
        n.lue = true;
        this.markingRead = null;
      },
      error: () => { this.markingRead = null; },
    });
  }

  deleteNotif(n: NotificationResponse): void {
    this.deleting = n.id;
    this.notifService.delete(n.id).subscribe({
      next: () => { this.notifications = this.notifications.filter(x => x.id !== n.id); this.deleting = null; },
      error: () => { this.deleting = null; },
    });
  }

  typeColor(type: string): string {
    if (type === 'SMS') return '#3B82F6';
    if (type === 'EMAIL') return '#8B5CF6';
    return '#F5A623';
  }

  typeBg(type: string): string {
    if (type === 'SMS') return 'rgba(59,130,246,0.12)';
    if (type === 'EMAIL') return 'rgba(139,92,246,0.12)';
    return 'rgba(245,166,35,0.12)';
  }
}
