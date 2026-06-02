export interface PaysRequest {
  nom: string;
  codeIso: string;
  indicatifTel: string;
  formatTel?: string;
  longueurTel?: number;
  deviseId: number;
}
