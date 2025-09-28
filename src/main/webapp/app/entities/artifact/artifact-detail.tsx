import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './artifact.reducer';

export const ArtifactDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const artifactEntity = useAppSelector(state => state.artifact.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="artifactDetailsHeading">
          <Translate contentKey="evagApp.artifact.detail.title">Artifact</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{artifactEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="evagApp.artifact.name">Name</Translate>
            </span>
          </dt>
          <dd>{artifactEntity.name}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="evagApp.artifact.description">Description</Translate>
            </span>
          </dt>
          <dd>{artifactEntity.description}</dd>
          <dt>
            <span id="type">
              <Translate contentKey="evagApp.artifact.type">Type</Translate>
            </span>
          </dt>
          <dd>{artifactEntity.type}</dd>
          <dt>
            <span id="link">
              <Translate contentKey="evagApp.artifact.link">Link</Translate>
            </span>
          </dt>
          <dd>{artifactEntity.link}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="evagApp.artifact.status">Status</Translate>
            </span>
          </dt>
          <dd>{artifactEntity.status}</dd>
          <dt>
            <span id="fileSize">
              <Translate contentKey="evagApp.artifact.fileSize">File Size</Translate>
            </span>
          </dt>
          <dd>{artifactEntity.fileSize}</dd>
          <dt>
            <span id="createdDate">
              <Translate contentKey="evagApp.artifact.createdDate">Created Date</Translate>
            </span>
          </dt>
          <dd>
            {artifactEntity.createdDate ? <TextFormat value={artifactEntity.createdDate} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="lastModifiedDate">
              <Translate contentKey="evagApp.artifact.lastModifiedDate">Last Modified Date</Translate>
            </span>
          </dt>
          <dd>
            {artifactEntity.lastModifiedDate ? (
              <TextFormat value={artifactEntity.lastModifiedDate} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="addendum">
              <Translate contentKey="evagApp.artifact.addendum">Addendum</Translate>
            </span>
          </dt>
          <dd>{artifactEntity.addendum}</dd>
          <dt>
            <Translate contentKey="evagApp.artifact.uploadedBy">Uploaded By</Translate>
          </dt>
          <dd>{artifactEntity.uploadedBy ? artifactEntity.uploadedBy.id : ''}</dd>
          <dt>
            <Translate contentKey="evagApp.artifact.project">Project</Translate>
          </dt>
          <dd>
            {artifactEntity.projects
              ? artifactEntity.projects.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.name}</a>
                    {artifactEntity.projects && i === artifactEntity.projects.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/artifact" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/artifact/${artifactEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ArtifactDetail;
