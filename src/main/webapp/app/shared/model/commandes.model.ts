import { IClients } from 'app/shared/model/clients.model';
import { IProduits } from 'app/shared/model/produits.model';

export interface ICommandes {
  id?: number;
  dateCommande?: string | null;
  clients?: IClients | null;
  produits?: IProduits | null;
}

export const defaultValue: Readonly<ICommandes> = {};
