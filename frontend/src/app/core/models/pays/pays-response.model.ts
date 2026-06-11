export interface PaysResponse {
  id: number;
  nom: string;
  codeIso: string;
  indicatifTel: string;
  formatTel: string | null;
  longueurTel: number;
  deviseCode: string;
  deviseNom: string;
  deviseSymbole: string;
}
