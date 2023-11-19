import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Commandes from './commandes';
import CommandesDetail from './commandes-detail';
import CommandesUpdate from './commandes-update';
import CommandesDeleteDialog from './commandes-delete-dialog';

const CommandesRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Commandes />} />
    <Route path="new" element={<CommandesUpdate />} />
    <Route path=":id">
      <Route index element={<CommandesDetail />} />
      <Route path="edit" element={<CommandesUpdate />} />
      <Route path="delete" element={<CommandesDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CommandesRoutes;
