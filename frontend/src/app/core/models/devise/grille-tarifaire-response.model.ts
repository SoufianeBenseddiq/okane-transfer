export interface GrilleTarifaireResponse {
  id: number;
  corridorId: number;
  montantMin: number;
  montantMax: number;
  fraisFixe: number;
  fraisPourcentage: number;
  partAgence: number;
  partCentrale: number;
}
