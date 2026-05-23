import { TypePiece } from '../enums/type-piece.enum';

export interface ExpediteurResponse {
  id: number;
  clientId: number;
  nomClient: string;
  prenomClient: string;
  telephoneClient: string;
  paysClient: string;
  pieceIdentiteId: number;
  typePiece: TypePiece;
  paysEmetteurPiece: string;
}
