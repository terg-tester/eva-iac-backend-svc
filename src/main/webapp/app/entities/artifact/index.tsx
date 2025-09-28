import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Artifact from './artifact';
import ArtifactDetail from './artifact-detail';
import ArtifactUpdate from './artifact-update';
import ArtifactDeleteDialog from './artifact-delete-dialog';

const ArtifactRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Artifact />} />
    <Route path="new" element={<ArtifactUpdate />} />
    <Route path=":id">
      <Route index element={<ArtifactDetail />} />
      <Route path="edit" element={<ArtifactUpdate />} />
      <Route path="delete" element={<ArtifactDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ArtifactRoutes;
