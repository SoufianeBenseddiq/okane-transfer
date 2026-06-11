import { TypeOperation } from '../enums/type-operation.enum';

export interface CaisseOperationResponse {
  id: number;
  agentId: number;
  agentNom: string;
  type: TypeOperation;
  montant: number;
  devise: string;              // actual currency: MAD for ENVOI, EUR/XOF/… for RETRAIT
  dateHeure: string;
  referenceTransfert: string | null;
}
