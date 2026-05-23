export interface AuditResponse {
  id: number;
  acteurId: number;
  action: string;
  entiteCible: string;
  idCible: number;
  detailAvant: string | null;
  detailApres: string | null;
  dateHeure: string;
  ipAdresse: string;
}
