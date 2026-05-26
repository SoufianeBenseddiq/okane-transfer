export interface CreateTransfertAvecNouveauClientRequest {
  nouveauClient: {
    nom: string;
    prenom: string;
    email: string;
    motDePasse: string;
    telephone: string;
    pays: string;
  };
  pieceIdentite: {
    numero: string;
    type: 'CIN' | 'PASSEPORT' | 'CARTE_SEJOUR' | 'PERMIS';
    paysEmetteur: string;
    dateExpiration?: string;
  };
  agentId: number | null;
  agenceEnvoiId: number | null;
  corridorId: number | null;
  grilleTarifaireId: number | null;
  nomBeneficiaire: string;
  prenomBeneficiaire: string;
  telephoneBeneficiaire: string;
  paysBeneficiaire: string;
  montant: number;
}
