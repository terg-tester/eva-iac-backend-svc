import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './deliverable.reducer';

export const DeliverableDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const deliverableEntity = useAppSelector(state => state.deliverable.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="deliverableDetailsHeading">
          <Translate contentKey="evagApp.deliverable.detail.title">Deliverable</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{deliverableEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="evagApp.deliverable.name">Name</Translate>
            </span>
          </dt>
          <dd>{deliverableEntity.name}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="evagApp.deliverable.description">Description</Translate>
            </span>
          </dt>
          <dd>{deliverableEntity.description}</dd>
          <dt>
            <span id="type">
              <Translate contentKey="evagApp.deliverable.type">Type</Translate>
            </span>
          </dt>
          <dd>{deliverableEntity.type}</dd>
          <dt>
            <span id="format">
              <Translate contentKey="evagApp.deliverable.format">Format</Translate>
            </span>
          </dt>
          <dd>{deliverableEntity.format}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="evagApp.deliverable.status">Status</Translate>
            </span>
          </dt>
          <dd>{deliverableEntity.status}</dd>
          <dt>
            <span id="packagePath">
              <Translate contentKey="evagApp.deliverable.packagePath">Package Path</Translate>
            </span>
          </dt>
          <dd>{deliverableEntity.packagePath}</dd>
          <dt>
            <span id="packageSize">
              <Translate contentKey="evagApp.deliverable.packageSize">Package Size</Translate>
            </span>
          </dt>
          <dd>{deliverableEntity.packageSize}</dd>
          <dt>
            <span id="checksum">
              <Translate contentKey="evagApp.deliverable.checksum">Checksum</Translate>
            </span>
          </dt>
          <dd>{deliverableEntity.checksum}</dd>
          <dt>
            <span id="createdDate">
              <Translate contentKey="evagApp.deliverable.createdDate">Created Date</Translate>
            </span>
          </dt>
          <dd>
            {deliverableEntity.createdDate ? (
              <TextFormat value={deliverableEntity.createdDate} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="lastModifiedDate">
              <Translate contentKey="evagApp.deliverable.lastModifiedDate">Last Modified Date</Translate>
            </span>
          </dt>
          <dd>
            {deliverableEntity.lastModifiedDate ? (
              <TextFormat value={deliverableEntity.lastModifiedDate} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="addendum">
              <Translate contentKey="evagApp.deliverable.addendum">Addendum</Translate>
            </span>
          </dt>
          <dd>{deliverableEntity.addendum}</dd>
          <dt>
            <Translate contentKey="evagApp.deliverable.createdBy">Created By</Translate>
          </dt>
          <dd>{deliverableEntity.createdBy ? deliverableEntity.createdBy.id : ''}</dd>
          <dt>
            <Translate contentKey="evagApp.deliverable.project">Project</Translate>
          </dt>
          <dd>{deliverableEntity.project ? deliverableEntity.project.name : ''}</dd>
        </dl>
        <Button tag={Link} to="/deliverable" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/deliverable/${deliverableEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default DeliverableDetail;
