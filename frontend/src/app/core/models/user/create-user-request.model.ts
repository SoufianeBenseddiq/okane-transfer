import { RoleUtilisateur } from '../enums/role-utilisateur.enum';

export interface CreateUserRequest {
  nom: string;
  prenom: string;
  email: string;
  motDePasse: string;
  telephone: string;
  pays: string;
  role: RoleUtilisateur;
}
