import {TypePiece} from "../enums";

export interface PaiementRequest {
  codeRetrait: string;
  agenceRetraitId: number;
  typePieceBeneficiaire: TypePiece;
  numeroPieceBeneficiaire: string;
  agentId?: number;
  agenceRetraitId?: number;
}
