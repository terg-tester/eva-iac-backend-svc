import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { getEntities as getProjects } from 'app/entities/project/project.reducer';
import { DeliverableType } from 'app/shared/model/enumerations/deliverable-type.model';
import { DeliverableFormat } from 'app/shared/model/enumerations/deliverable-format.model';
import { DeploymentStatus } from 'app/shared/model/enumerations/deployment-status.model';
import { createEntity, getEntity, reset, updateEntity } from './deliverable.reducer';

export const DeliverableUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const projects = useAppSelector(state => state.project.entities);
  const deliverableEntity = useAppSelector(state => state.deliverable.entity);
  const loading = useAppSelector(state => state.deliverable.loading);
  const updating = useAppSelector(state => state.deliverable.updating);
  const updateSuccess = useAppSelector(state => state.deliverable.updateSuccess);
  const deliverableTypeValues = Object.keys(DeliverableType);
  const deliverableFormatValues = Object.keys(DeliverableFormat);
  const deploymentStatusValues = Object.keys(DeploymentStatus);

  const handleClose = () => {
    navigate(`/deliverable${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getUsers({}));
    dispatch(getProjects({}));
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
    if (values.packageSize !== undefined && typeof values.packageSize !== 'number') {
      values.packageSize = Number(values.packageSize);
    }
    values.createdDate = convertDateTimeToServer(values.createdDate);
    values.lastModifiedDate = convertDateTimeToServer(values.lastModifiedDate);

    const entity = {
      ...deliverableEntity,
      ...values,
      createdBy: users.find(it => it.id.toString() === values.createdBy?.toString()),
      project: projects.find(it => it.id.toString() === values.project?.toString()),
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
          createdDate: displayDefaultDateTime(),
          lastModifiedDate: displayDefaultDateTime(),
        }
      : {
          type: 'TERRAFORM',
          format: 'ZIP_ARCHIVE',
          status: 'SUCCESS',
          ...deliverableEntity,
          createdDate: convertDateTimeFromServer(deliverableEntity.createdDate),
          lastModifiedDate: convertDateTimeFromServer(deliverableEntity.lastModifiedDate),
          createdBy: deliverableEntity?.createdBy?.id,
          project: deliverableEntity?.project?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="evagApp.deliverable.home.createOrEditLabel" data-cy="DeliverableCreateUpdateHeading">
            <Translate contentKey="evagApp.deliverable.home.createOrEditLabel">Create or edit a Deliverable</Translate>
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
                  id="deliverable-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('evagApp.deliverable.name')}
                id="deliverable-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  minLength: { value: 3, message: translate('entity.validation.minlength', { min: 3 }) },
                  maxLength: { value: 100, message: translate('entity.validation.maxlength', { max: 100 }) },
                }}
              />
              <ValidatedField
                label={translate('evagApp.deliverable.description')}
                id="deliverable-description"
                name="description"
                data-cy="description"
                type="text"
                validate={{
                  maxLength: { value: 500, message: translate('entity.validation.maxlength', { max: 500 }) },
                }}
              />
              <ValidatedField label={translate('evagApp.deliverable.type')} id="deliverable-type" name="type" data-cy="type" type="select">
                {deliverableTypeValues.map(deliverableType => (
                  <option value={deliverableType} key={deliverableType}>
                    {translate(`evagApp.DeliverableType.${deliverableType}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('evagApp.deliverable.format')}
                id="deliverable-format"
                name="format"
                data-cy="format"
                type="select"
              >
                {deliverableFormatValues.map(deliverableFormat => (
                  <option value={deliverableFormat} key={deliverableFormat}>
                    {translate(`evagApp.DeliverableFormat.${deliverableFormat}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('evagApp.deliverable.status')}
                id="deliverable-status"
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
                label={translate('evagApp.deliverable.packagePath')}
                id="deliverable-packagePath"
                name="packagePath"
                data-cy="packagePath"
                type="text"
                validate={{
                  maxLength: { value: 500, message: translate('entity.validation.maxlength', { max: 500 }) },
                }}
              />
              <ValidatedField
                label={translate('evagApp.deliverable.packageSize')}
                id="deliverable-packageSize"
                name="packageSize"
                data-cy="packageSize"
                type="text"
              />
              <ValidatedField
                label={translate('evagApp.deliverable.checksum')}
                id="deliverable-checksum"
                name="checksum"
                data-cy="checksum"
                type="text"
                validate={{
                  maxLength: { value: 128, message: translate('entity.validation.maxlength', { max: 128 }) },
                }}
              />
              <ValidatedField
                label={translate('evagApp.deliverable.createdDate')}
                id="deliverable-createdDate"
                name="createdDate"
                data-cy="createdDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('evagApp.deliverable.lastModifiedDate')}
                id="deliverable-lastModifiedDate"
                name="lastModifiedDate"
                data-cy="lastModifiedDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('evagApp.deliverable.addendum')}
                id="deliverable-addendum"
                name="addendum"
                data-cy="addendum"
                type="text"
                validate={{
                  maxLength: { value: 4000, message: translate('entity.validation.maxlength', { max: 4000 }) },
                }}
              />
              <ValidatedField
                id="deliverable-createdBy"
                name="createdBy"
                data-cy="createdBy"
                label={translate('evagApp.deliverable.createdBy')}
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
                id="deliverable-project"
                name="project"
                data-cy="project"
                label={translate('evagApp.deliverable.project')}
                type="select"
              >
                <option value="" key="0" />
                {projects
                  ? projects.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/deliverable" replace color="info">
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

export default DeliverableUpdate;
