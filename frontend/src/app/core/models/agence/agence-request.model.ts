export interface AgenceRequest {
  nom: string;
  adresse: string;
  pays: string;
  plafondJournalier: number;
  soldeCaisseAgence?: number | null;
  responsableId?: number | null;
  estCentrale?: boolean;
  agenceCentraleId?: number | null;
}
