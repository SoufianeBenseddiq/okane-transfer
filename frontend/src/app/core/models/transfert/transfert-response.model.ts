import { StatutTransfert } from '../enums/statut-transfert.enum';

export interface TransfertResponse {
  codeRetrait: string;
  numeroReference: string;
  montantEnvoye: number;
  montantRecu: number;
  frais: number;
  statut: StatutTransfert;
  creeLe: string;
}
