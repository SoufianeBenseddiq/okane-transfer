export interface RegleAML {
  id?: number;
  nom: string;
  seuilMontant: number | null;
  nbTransactionsMax: number | null;
  fenetreHeures: number | null;
  active: boolean;
}
