import { TypePiece } from "../enums";

export interface PaiementRequest {
  // Identifiants du transfert
  codeRetrait: string;

  // Acteurs et guichet
  agentId?: number;
  agenceRetraitId: number; // Rendu obligatoire (HEAD) pour tracer le lieu du paiement

  // Vérification de l'identité du bénéficiaire
  typePieceBeneficiaire: TypePiece;
  numeroPieceBeneficiaire: string;
}
