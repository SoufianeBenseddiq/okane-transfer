import { TypePiece } from '../enums/type-piece.enum';

export interface PieceIdentiteResponse {
  id: number;
  numero: string;
  type: TypePiece;
  paysEmetteur: string;
  dateExpiration: string | null;
  principale: boolean;
}
