export type TransferStatus = 'pending' | 'validated' | 'to_withdraw' | 'paid' | 'expired';
export type Currency = 'MAD' | 'EUR' | 'USD' | 'XOF' | 'XAF';

export interface Transfer {
  id: string;
  reference: string;
  recipientName: string;
  amount: number;
  currency: Currency;
  convertedAmount: number;
  convertedCurrency: Currency;
  destinationCountry: string;
  destinationFlag: string;
  status: TransferStatus;
  createdAt: Date;
  steps: TransferStep[];
  withdrawalCode?: string;
}

export interface TransferStep {
  label: string;
  time?: string;
  completed: boolean;
  current: boolean;
}

export interface Beneficiary {
  id: string;
  initials: string;
  name: string;
  country: string;
}

export interface DashboardStats {
  sentThisMonth: number;
  currency: Currency;
  changeVsLastMonth: number;
  activeBeneficiariesCount: number;
  beneficiaries: Beneficiary[];
}

export interface DashboardData {
  userName: string;
  activeTransfer: Transfer | null;
  stats: DashboardStats;
  recentTransfers: Transfer[];
}