import { TypeOperation } from '../enums/type-operation.enum';

export interface CaisseOperationResponse {
  id: number;
  agentId: number;
  agentNom: string;
  type: TypeOperation;
  montant: number;
  dateHeure: string;
  referenceTransfert: string | null;
}
