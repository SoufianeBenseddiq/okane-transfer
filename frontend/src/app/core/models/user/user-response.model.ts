import { RoleUtilisateur } from '../enums/role-utilisateur.enum';

export interface UserResponse {
  id: number;
  nom: string;
  prenom: string;
  email: string | null;
  telephone: string;
  pays: string | null;        // pays.nom
  paysCodeIso: string | null; // pays.codeIso — for flag emoji
  paysId: number | null;
  role: RoleUtilisateur;
  actif: boolean;
  soldeCaisse?: number | null; // ROLE_AGENT only
  caisseOuverteAujourdhui?: boolean | null; // ROLE_AGENT only
  agenceId?: number;
  creeLe: string;
}
