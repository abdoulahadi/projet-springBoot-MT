export interface IClients {
  id?: number;
  nom?: string | null;
  prenom?: string | null;
  adresse?: string | null;
  telephone?: string | null;
  email?: string | null;
  idUser?: number | null;
}

export const defaultValue: Readonly<IClients> = {};
