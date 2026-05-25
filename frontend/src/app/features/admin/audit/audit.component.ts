import { Component, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TranslateService, TranslatePipe } from '@ngx-translate/core';
import { AmlService } from '../../../core/services/aml.service';
import { AuditResponse } from '../../../core/models/aml';
import { TopbarComponent } from '../../../shared/components/topbar/topbar.component';
import { IconComponent } from '../../../shared/components/icon/icon.component';

@Component({
  selector: 'app-audit',
  standalone: true,
  host: { class: 'flex flex-col flex-1 min-w-0 overflow-hidden' },
  imports: [TopbarComponent, IconComponent, FormsModule, DatePipe, TranslatePipe],
  templateUrl: './audit.component.html',
})
export class AuditComponent implements OnInit {
  logs: AuditResponse[] = [];
  loading = true;
  search = '';
  expandedId: number | null = null;

  constructor(
    private aml: AmlService,
    private translate: TranslateService,
  ) {}

  ngOnInit(): void {
    this.aml.getAllAuditLogs().subscribe({
      next: data => { this.logs = data; this.loading = false; },
      error: () => { this.loading = false; },
    });
  }

  get subtitle(): string {
    return `${this.filtered.length} ${this.translate.instant('audit.subtitle')}`;
  }

  get filtered(): AuditResponse[] {
    if (!this.search.trim()) return this.logs;
    const q = this.search.toLowerCase();
    return this.logs.filter(l =>
      l.action.toLowerCase().includes(q) ||
      l.entiteCible.toLowerCase().includes(q) ||
      (l.ipAdresse ?? '').toLowerCase().includes(q) ||
      String(l.acteurId).includes(q)
    );
  }

  toggleExpand(id: number): void {
    this.expandedId = this.expandedId === id ? null : id;
  }

  formatJson(raw: string | null): string {
    if (!raw) return '';
    try { return JSON.stringify(JSON.parse(raw), null, 2); }
    catch { return raw; }
  }

  actionColor(action: string): string {
    if (action.toUpperCase().includes('DELETE') || action.toUpperCase().includes('SUPPRIM')) return '#FF4D4F';
    if (action.toUpperCase().includes('CREATE') || action.toUpperCase().includes('CREAT')) return '#00C48C';
    if (action.toUpperCase().includes('UPDATE') || action.toUpperCase().includes('MODIF')) return '#F5A623';
    return '#6B7280';
  }

  actionBg(action: string): string {
    if (action.toUpperCase().includes('DELETE') || action.toUpperCase().includes('SUPPRIM')) return 'rgba(255,77,79,0.1)';
    if (action.toUpperCase().includes('CREATE') || action.toUpperCase().includes('CREAT')) return 'rgba(0,196,140,0.1)';
    if (action.toUpperCase().includes('UPDATE') || action.toUpperCase().includes('MODIF')) return 'rgba(245,166,35,0.1)';
    return 'rgba(107,114,128,0.1)';
  }
}
