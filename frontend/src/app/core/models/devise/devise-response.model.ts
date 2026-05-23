export interface DeviseResponse {
  id: number;
  code: string;
  nom: string;
  symbole: string;
  active: boolean;
  tauxVersEuro: number;
  derniereMaj: string;
  sourceTaux: string;
}
