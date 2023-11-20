import { ICategories } from 'app/shared/model/categories.model';

export interface IProduits {
  id?: number;
  nomProduit?: string | null;
  descriptionProduit?: string | null;
  prixProduit?: number | null;
  imageProduit?: string | null;
  categories?: ICategories | null;
}

export const defaultValue: Readonly<IProduits> = {};
