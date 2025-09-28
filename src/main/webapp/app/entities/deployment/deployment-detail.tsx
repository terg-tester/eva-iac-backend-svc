import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './deployment.reducer';

export const DeploymentDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const deploymentEntity = useAppSelector(state => state.deployment.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="deploymentDetailsHeading">
          <Translate contentKey="evagApp.deployment.detail.title">Deployment</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{deploymentEntity.id}</dd>
          <dt>
            <span id="deploymentDate">
              <Translate contentKey="evagApp.deployment.deploymentDate">Deployment Date</Translate>
            </span>
          </dt>
          <dd>
            {deploymentEntity.deploymentDate ? (
              <TextFormat value={deploymentEntity.deploymentDate} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="status">
              <Translate contentKey="evagApp.deployment.status">Status</Translate>
            </span>
          </dt>
          <dd>{deploymentEntity.status}</dd>
          <dt>
            <span id="logs">
              <Translate contentKey="evagApp.deployment.logs">Logs</Translate>
            </span>
          </dt>
          <dd>{deploymentEntity.logs}</dd>
          <dt>
            <span id="addendum">
              <Translate contentKey="evagApp.deployment.addendum">Addendum</Translate>
            </span>
          </dt>
          <dd>{deploymentEntity.addendum}</dd>
          <dt>
            <Translate contentKey="evagApp.deployment.deployedBy">Deployed By</Translate>
          </dt>
          <dd>{deploymentEntity.deployedBy ? deploymentEntity.deployedBy.id : ''}</dd>
          <dt>
            <Translate contentKey="evagApp.deployment.deliverable">Deliverable</Translate>
          </dt>
          <dd>{deploymentEntity.deliverable ? deploymentEntity.deliverable.name : ''}</dd>
        </dl>
        <Button tag={Link} to="/deployment" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/deployment/${deploymentEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default DeploymentDetail;
