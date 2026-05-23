export interface CreateTransfertRequest {
  clientId: number;
  pieceIdentiteId: number;
  agentId: number | null;
  agenceEnvoiId: number | null;
  corridorId: number | null;
  grilleTarifaireId: number | null;
  nomBeneficiaire: string;
  prenomBeneficiaire: string;
  telephoneBeneficiaire: string;
  paysBeneficiaire: string;
  montant: number;
}
