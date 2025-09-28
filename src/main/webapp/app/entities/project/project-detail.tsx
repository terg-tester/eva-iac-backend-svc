import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './project.reducer';

export const ProjectDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const projectEntity = useAppSelector(state => state.project.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="projectDetailsHeading">
          <Translate contentKey="evagApp.project.detail.title">Project</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{projectEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="evagApp.project.name">Name</Translate>
            </span>
          </dt>
          <dd>{projectEntity.name}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="evagApp.project.description">Description</Translate>
            </span>
          </dt>
          <dd>{projectEntity.description}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="evagApp.project.status">Status</Translate>
            </span>
          </dt>
          <dd>{projectEntity.status}</dd>
          <dt>
            <span id="priority">
              <Translate contentKey="evagApp.project.priority">Priority</Translate>
            </span>
          </dt>
          <dd>{projectEntity.priority}</dd>
          <dt>
            <span id="startDate">
              <Translate contentKey="evagApp.project.startDate">Start Date</Translate>
            </span>
          </dt>
          <dd>{projectEntity.startDate ? <TextFormat value={projectEntity.startDate} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="endDate">
              <Translate contentKey="evagApp.project.endDate">End Date</Translate>
            </span>
          </dt>
          <dd>{projectEntity.endDate ? <TextFormat value={projectEntity.endDate} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="createdDate">
              <Translate contentKey="evagApp.project.createdDate">Created Date</Translate>
            </span>
          </dt>
          <dd>
            {projectEntity.createdDate ? <TextFormat value={projectEntity.createdDate} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="lastModifiedDate">
              <Translate contentKey="evagApp.project.lastModifiedDate">Last Modified Date</Translate>
            </span>
          </dt>
          <dd>
            {projectEntity.lastModifiedDate ? (
              <TextFormat value={projectEntity.lastModifiedDate} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="addendum">
              <Translate contentKey="evagApp.project.addendum">Addendum</Translate>
            </span>
          </dt>
          <dd>{projectEntity.addendum}</dd>
          <dt>
            <Translate contentKey="evagApp.project.createdBy">Created By</Translate>
          </dt>
          <dd>{projectEntity.createdBy ? projectEntity.createdBy.id : ''}</dd>
          <dt>
            <Translate contentKey="evagApp.project.artifact">Artifact</Translate>
          </dt>
          <dd>
            {projectEntity.artifacts
              ? projectEntity.artifacts.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.name}</a>
                    {projectEntity.artifacts && i === projectEntity.artifacts.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/project" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/project/${projectEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ProjectDetail;
