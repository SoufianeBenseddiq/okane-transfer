export interface AgenceResponse {
  id: number;
  nom: string;
  adresse: string;
  pays: string;
  plafondJournalier: number;
  montantTraiteAujourdhui: number;
  soldeCaisseAgence: number;
  active: boolean;
  responsableNom: string | null;
  responsableId: number | null;
  estCentrale: boolean;
  agenceCentraleId: number | null;
  agenceCentraleNom: string | null;
  pourcentagePlafond: number;
}
