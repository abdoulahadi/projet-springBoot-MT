import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Categories from './categories';
import Produits from './produits';
import Clients from './clients';
import Commandes from './commandes';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="categories/*" element={<Categories />} />
        <Route path="produits/*" element={<Produits />} />
        <Route path="clients/*" element={<Clients />} />
        <Route path="commandes/*" element={<Commandes />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
