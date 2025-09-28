import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { getEntities as getDeliverables } from 'app/entities/deliverable/deliverable.reducer';
import { DeploymentStatus } from 'app/shared/model/enumerations/deployment-status.model';
import { createEntity, getEntity, reset, updateEntity } from './deployment.reducer';

export const DeploymentUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const deliverables = useAppSelector(state => state.deliverable.entities);
  const deploymentEntity = useAppSelector(state => state.deployment.entity);
  const loading = useAppSelector(state => state.deployment.loading);
  const updating = useAppSelector(state => state.deployment.updating);
  const updateSuccess = useAppSelector(state => state.deployment.updateSuccess);
  const deploymentStatusValues = Object.keys(DeploymentStatus);

  const handleClose = () => {
    navigate(`/deployment${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getUsers({}));
    dispatch(getDeliverables({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    values.deploymentDate = convertDateTimeToServer(values.deploymentDate);

    const entity = {
      ...deploymentEntity,
      ...values,
      deployedBy: users.find(it => it.id.toString() === values.deployedBy?.toString()),
      deliverable: deliverables.find(it => it.id.toString() === values.deliverable?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          deploymentDate: displayDefaultDateTime(),
        }
      : {
          status: 'SUCCESS',
          ...deploymentEntity,
          deploymentDate: convertDateTimeFromServer(deploymentEntity.deploymentDate),
          deployedBy: deploymentEntity?.deployedBy?.id,
          deliverable: deploymentEntity?.deliverable?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="evagApp.deployment.home.createOrEditLabel" data-cy="DeploymentCreateUpdateHeading">
            <Translate contentKey="evagApp.deployment.home.createOrEditLabel">Create or edit a Deployment</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="deployment-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('evagApp.deployment.deploymentDate')}
                id="deployment-deploymentDate"
                name="deploymentDate"
                data-cy="deploymentDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('evagApp.deployment.status')}
                id="deployment-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {deploymentStatusValues.map(deploymentStatus => (
                  <option value={deploymentStatus} key={deploymentStatus}>
                    {translate(`evagApp.DeploymentStatus.${deploymentStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('evagApp.deployment.logs')}
                id="deployment-logs"
                name="logs"
                data-cy="logs"
                type="textarea"
              />
              <ValidatedField
                label={translate('evagApp.deployment.addendum')}
                id="deployment-addendum"
                name="addendum"
                data-cy="addendum"
                type="text"
                validate={{
                  maxLength: { value: 4000, message: translate('entity.validation.maxlength', { max: 4000 }) },
                }}
              />
              <ValidatedField
                id="deployment-deployedBy"
                name="deployedBy"
                data-cy="deployedBy"
                label={translate('evagApp.deployment.deployedBy')}
                type="select"
              >
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="deployment-deliverable"
                name="deliverable"
                data-cy="deliverable"
                label={translate('evagApp.deployment.deliverable')}
                type="select"
              >
                <option value="" key="0" />
                {deliverables
                  ? deliverables.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/deployment" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default DeploymentUpdate;
