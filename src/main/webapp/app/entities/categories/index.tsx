import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Categories from './categories';
import CategoriesDetail from './categories-detail';
import CategoriesUpdate from './categories-update';
import CategoriesDeleteDialog from './categories-delete-dialog';

const CategoriesRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Categories />} />
    <Route path="new" element={<CategoriesUpdate />} />
    <Route path=":id">
      <Route index element={<CategoriesDetail />} />
      <Route path="edit" element={<CategoriesUpdate />} />
      <Route path="delete" element={<CategoriesDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CategoriesRoutes;
