import { IUser } from 'app/shared/model/user.model';

export interface IClients {
  id?: number;
  nom?: string | null;
  prenom?: string | null;
  adresse?: string | null;
  telephone?: string | null;
  email?: string | null;
  user?: IUser | null;
}

export const defaultValue: Readonly<IClients> = {};
