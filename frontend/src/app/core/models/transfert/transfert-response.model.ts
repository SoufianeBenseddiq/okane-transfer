// import { StatutTransfert } from '../enums/statut-transfert.enum';
//
// export interface TransfertResponse {
//   codeRetrait: string;
//   numeroReference: string;
//   montantEnvoye: number;
//   montantRecu: number;
//   frais: number;
//   statut: StatutTransfert;
//   creeLe: string;
// }

import { StatutTransfert } from '../enums/statut-transfert.enum';

export interface TransfertResponse {
  // Identifiants
  codeRetrait:       string;
  numeroReference:   string;

  // Expéditeur
  nomExpediteur:     string;
  paysExpediteur:    string;   // code ISO ex: "MA"
  villeExpediteur:   string;
  agenceEnvoi:       string;
  telephoneExpediteur: string;

  // Bénéficiaire
  nomBeneficiaire:        string;
  paysBeneficiaire:       string;  // code ISO ex: "SN"
  villeBeneficiaire:      string;
  telephoneBeneficiaire:  string;

  // Montants
  montantEnvoye:    number;
  montantRecu:      number;
  frais:            number;
  partAgence?:      number;
  deviseReception:  string;   // ex: "XOF"
  tauxChange:       number;

  // Statut & Dates
  statut:    StatutTransfert;
  creeLe:    string;   // ISO string — à parser avec DatePipe côté template
  expireLe:  string;
}



