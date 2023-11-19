import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Clients from './clients';
import ClientsDetail from './clients-detail';
import ClientsUpdate from './clients-update';
import ClientsDeleteDialog from './clients-delete-dialog';

const ClientsRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Clients />} />
    <Route path="new" element={<ClientsUpdate />} />
    <Route path=":id">
      <Route index element={<ClientsDetail />} />
      <Route path="edit" element={<ClientsUpdate />} />
      <Route path="delete" element={<ClientsDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ClientsRoutes;
