export interface RegleAML {
  id?: number;
  nom: string;
  description: string;
  seuilMontant: number | null;
  seuilNbTransactions: number | null;
  fenetreTempsMinutes: number | null;
  active: boolean;
}
