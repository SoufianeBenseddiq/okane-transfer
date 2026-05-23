export interface GrilleTarifaireRequest {
  corridorId: number;
  montantMin: number;
  montantMax: number;
  fraisFixe: number;
  fraisPourcentage: number;
  partAgence: number;
  partCentrale: number;
}
