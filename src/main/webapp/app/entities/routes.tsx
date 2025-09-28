import React from 'react';
import { Route } from 'react-router'; // eslint-disable-line

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Project from './project';
import Artifact from './artifact';
import Deliverable from './deliverable';
import Deployment from './deployment';
import Content from './content';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="project/*" element={<Project />} />
        <Route path="artifact/*" element={<Artifact />} />
        <Route path="deliverable/*" element={<Deliverable />} />
        <Route path="deployment/*" element={<Deployment />} />
        <Route path="content/*" element={<Content />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
