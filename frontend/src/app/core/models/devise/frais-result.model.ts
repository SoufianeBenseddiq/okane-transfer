export interface FraisResult {
  montantFrais: number;
  partAgence: number;
  partCentrale: number;
  montantRecu: number;
  taux?: number;
  delaiMin?: number;
  grilleTarifaireId?: number | null;
}
