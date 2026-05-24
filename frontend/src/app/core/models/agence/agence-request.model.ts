export interface AgenceRequest {
  nom: string;
  adresse: string;
  pays: string;
  plafondJournalier: number;
  responsableId?: number | null;
}
