import { StatutTransfert } from '../enums/statut-transfert.enum';

export interface TransfertResponse {
  // Identifiants & Clés techniques
  id: number;
  codeRetrait: string;
  numeroReference: string;

  // Expéditeur
  nomExpediteur: string;
  paysExpediteur: string;       // code ISO ex: "MA"
  villeExpediteur: string;
  agenceEnvoi: string;
  agenceEnvoiId?: number | null;
  telephoneExpediteur: string;

  // Bénéficiaire
  nomBeneficiaire: string;
  paysBeneficiaire: string;     // code ISO ex: "SN"
  villeBeneficiaire: string;
  telephoneBeneficiaire: string;
  agenceRetraitId?: number | null;

  // Montants & Devises
  montantEnvoye: number;
  montantRecu: number;
  frais: number;
  partAgence?: number;
  deviseReception: string;      // ex: "XOF"
  tauxChange: number;

  // Statut & Audit
  statut: StatutTransfert;
  agentId?: number | null;
  creeLe: string;               // ISO string
  expireLe: string;
  payeLe?: string | null;
}