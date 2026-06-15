import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

import { DashboardService } from './dashboard.service';
import { DashboardData } from './dashboard.models';

import { WelcomeBannerComponent } from './Components/welcome-component/welcome-banner.component';
import { ActiveTransferCardComponent } from './Components/ActivatedCard-component/ active-transfer-card.component';
import { StatsCardComponent } from './Components/StatsCard-component/stats-card.component';
import { BeneficiariesCardComponent } from './Components/Beneficiaries-component/beneficiaries-card.component';
import { RecentTransfersComponent } from './Components/Recenttransfers-component/recent-transfers.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    WelcomeBannerComponent,
    ActiveTransferCardComponent,
    StatsCardComponent,
    BeneficiariesCardComponent,
    RecentTransfersComponent,
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
})
export class DashboardComponent implements OnInit {
  data: DashboardData | null = null;
  isLoading = true;
  error: string | null = null;

  constructor(
    private dashboardService: DashboardService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.dashboardService.getDashboardData().subscribe({
      next: (data) => {
        this.data = data;
        this.isLoading = false;
      },
      error: () => {
        this.error = 'Impossible de charger le tableau de bord.';
        this.isLoading = false;
      },
    });
  }

  onSendMoney(): void {
    this.router.navigate(['/send']);
  }

  onViewHistory(): void {
    this.router.navigate(['/transfers']);
  }

  onManageBeneficiaries(): void {
    this.router.navigate(['/beneficiaries']);
  }

  onViewAllTransfers(): void {
    this.router.navigate(['/transfers']);
  }
}