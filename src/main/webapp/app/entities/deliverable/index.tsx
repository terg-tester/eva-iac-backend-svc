import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Deliverable from './deliverable';
import DeliverableDetail from './deliverable-detail';
import DeliverableUpdate from './deliverable-update';
import DeliverableDeleteDialog from './deliverable-delete-dialog';

const DeliverableRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Deliverable />} />
    <Route path="new" element={<DeliverableUpdate />} />
    <Route path=":id">
      <Route index element={<DeliverableDetail />} />
      <Route path="edit" element={<DeliverableUpdate />} />
      <Route path="delete" element={<DeliverableDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default DeliverableRoutes;
