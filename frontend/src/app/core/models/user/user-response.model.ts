import { RoleUtilisateur } from '../enums/role-utilisateur.enum';

export interface UserResponse {
  id: number;
  nom: string;
  prenom: string;
  email: string | null;
  telephone: string;
  pays: string;
  role: RoleUtilisateur;
  actif: boolean;
  creeLe: string;
}
