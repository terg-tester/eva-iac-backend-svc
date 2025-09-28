import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Deployment from './deployment';
import DeploymentDetail from './deployment-detail';
import DeploymentUpdate from './deployment-update';
import DeploymentDeleteDialog from './deployment-delete-dialog';

const DeploymentRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Deployment />} />
    <Route path="new" element={<DeploymentUpdate />} />
    <Route path=":id">
      <Route index element={<DeploymentDetail />} />
      <Route path="edit" element={<DeploymentUpdate />} />
      <Route path="delete" element={<DeploymentDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default DeploymentRoutes;
