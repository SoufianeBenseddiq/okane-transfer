export interface AgenceResponse {
  id: number;
  nom: string;
  adresse: string;
  pays: string;
  plafondJournalier: number;
  montantTraiteAujourdhui: number;
  active: boolean;
  responsableNom: string | null;
}
