import { TypePiece } from '../enums/type-piece.enum';

export interface PieceIdentiteRequest {
  numero: string;
  type: TypePiece;
  paysEmetteur: string;
  dateExpiration?: string;
}
