export interface ClotureCaisseResponse {
  id: number;
  agentId: number;
  agentNom: string;
  date: string;
  soldeTheorique: number;
  soldeSaisi: number;
  ecart: number;
  ecartSignale: boolean;
  clotureeLe: string;
}
