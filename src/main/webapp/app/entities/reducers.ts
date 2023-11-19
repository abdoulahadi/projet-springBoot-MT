import categories from 'app/entities/categories/categories.reducer';
import produits from 'app/entities/produits/produits.reducer';
import clients from 'app/entities/clients/clients.reducer';
import commandes from 'app/entities/commandes/commandes.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  categories,
  produits,
  clients,
  commandes,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
