// src/app/core/models/piece-identite/piece-identite-response.model.ts

export interface PieceIdentiteResponse {
  id: number;
  numero: string;
  type: string;           // string simple — le backend renvoie "CIN", "PASSEPORT", etc.
  paysEmetteur: string;
  dateExpiration: string | null;
  principale: boolean;
}