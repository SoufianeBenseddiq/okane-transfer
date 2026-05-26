import { StatutTransfert } from '../enums/statut-transfert.enum';

export interface TransfertResponse {
  id: number;
  codeRetrait: string;
  numeroReference: string;
  montantEnvoye: number;
  montantRecu: number;
  frais: number;
  statut: StatutTransfert;
  creeLe: string;
  payeLe?: string | null;
  agentId?: number | null;
  agenceEnvoiId?: number | null;
  agenceRetraitId?: number | null;
}
