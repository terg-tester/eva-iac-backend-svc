import project from 'app/entities/project/project.reducer';
import artifact from 'app/entities/artifact/artifact.reducer';
import deliverable from 'app/entities/deliverable/deliverable.reducer';
import deployment from 'app/entities/deployment/deployment.reducer';
import content from 'app/entities/content/content.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  project,
  artifact,
  deliverable,
  deployment,
  content,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
