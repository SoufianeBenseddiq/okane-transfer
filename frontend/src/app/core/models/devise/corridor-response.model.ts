import { PaysResponse } from '../pays/pays-response.model';

export interface CorridorResponse {
  id: number;
  deviseSource: string;
  deviseDestination: string;
  actif: boolean;
  dateActivation: string;
  paysSource: PaysResponse | null;
  paysDestination: PaysResponse | null;
}
