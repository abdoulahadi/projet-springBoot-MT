import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Produits from './produits';
import ProduitsDetail from './produits-detail';
import ProduitsUpdate from './produits-update';
import ProduitsDeleteDialog from './produits-delete-dialog';

const ProduitsRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Produits />} />
    <Route path="new" element={<ProduitsUpdate />} />
    <Route path=":id">
      <Route index element={<ProduitsDetail />} />
      <Route path="edit" element={<ProduitsUpdate />} />
      <Route path="delete" element={<ProduitsDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ProduitsRoutes;
