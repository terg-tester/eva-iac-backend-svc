import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { getEntities as getProjects } from 'app/entities/project/project.reducer';
import { ArtifactType } from 'app/shared/model/enumerations/artifact-type.model';
import { ArtifactStatus } from 'app/shared/model/enumerations/artifact-status.model';
import { createEntity, getEntity, reset, updateEntity } from './artifact.reducer';

export const ArtifactUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const projects = useAppSelector(state => state.project.entities);
  const artifactEntity = useAppSelector(state => state.artifact.entity);
  const loading = useAppSelector(state => state.artifact.loading);
  const updating = useAppSelector(state => state.artifact.updating);
  const updateSuccess = useAppSelector(state => state.artifact.updateSuccess);
  const artifactTypeValues = Object.keys(ArtifactType);
  const artifactStatusValues = Object.keys(ArtifactStatus);

  const handleClose = () => {
    navigate(`/artifact${location.search}`);
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
    if (values.fileSize !== undefined && typeof values.fileSize !== 'number') {
      values.fileSize = Number(values.fileSize);
    }
    values.createdDate = convertDateTimeToServer(values.createdDate);
    values.lastModifiedDate = convertDateTimeToServer(values.lastModifiedDate);

    const entity = {
      ...artifactEntity,
      ...values,
      uploadedBy: users.find(it => it.id.toString() === values.uploadedBy?.toString()),
      projects: mapIdList(values.projects),
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
          type: 'DOCUMENT',
          status: 'DRAFT',
          ...artifactEntity,
          createdDate: convertDateTimeFromServer(artifactEntity.createdDate),
          lastModifiedDate: convertDateTimeFromServer(artifactEntity.lastModifiedDate),
          uploadedBy: artifactEntity?.uploadedBy?.id,
          projects: artifactEntity?.projects?.map(e => e.id.toString()),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="evagApp.artifact.home.createOrEditLabel" data-cy="ArtifactCreateUpdateHeading">
            <Translate contentKey="evagApp.artifact.home.createOrEditLabel">Create or edit a Artifact</Translate>
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
                  id="artifact-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('evagApp.artifact.name')}
                id="artifact-name"
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
                label={translate('evagApp.artifact.description')}
                id="artifact-description"
                name="description"
                data-cy="description"
                type="text"
                validate={{
                  maxLength: { value: 500, message: translate('entity.validation.maxlength', { max: 500 }) },
                }}
              />
              <ValidatedField label={translate('evagApp.artifact.type')} id="artifact-type" name="type" data-cy="type" type="select">
                {artifactTypeValues.map(artifactType => (
                  <option value={artifactType} key={artifactType}>
                    {translate(`evagApp.ArtifactType.${artifactType}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('evagApp.artifact.link')}
                id="artifact-link"
                name="link"
                data-cy="link"
                type="text"
                validate={{
                  maxLength: { value: 500, message: translate('entity.validation.maxlength', { max: 500 }) },
                }}
              />
              <ValidatedField
                label={translate('evagApp.artifact.status')}
                id="artifact-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {artifactStatusValues.map(artifactStatus => (
                  <option value={artifactStatus} key={artifactStatus}>
                    {translate(`evagApp.ArtifactStatus.${artifactStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('evagApp.artifact.fileSize')}
                id="artifact-fileSize"
                name="fileSize"
                data-cy="fileSize"
                type="text"
              />
              <ValidatedField
                label={translate('evagApp.artifact.createdDate')}
                id="artifact-createdDate"
                name="createdDate"
                data-cy="createdDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('evagApp.artifact.lastModifiedDate')}
                id="artifact-lastModifiedDate"
                name="lastModifiedDate"
                data-cy="lastModifiedDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('evagApp.artifact.addendum')}
                id="artifact-addendum"
                name="addendum"
                data-cy="addendum"
                type="text"
                validate={{
                  maxLength: { value: 4000, message: translate('entity.validation.maxlength', { max: 4000 }) },
                }}
              />
              <ValidatedField
                id="artifact-uploadedBy"
                name="uploadedBy"
                data-cy="uploadedBy"
                label={translate('evagApp.artifact.uploadedBy')}
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
                label={translate('evagApp.artifact.project')}
                id="artifact-project"
                data-cy="project"
                type="select"
                multiple
                name="projects"
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/artifact" replace color="info">
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

export default ArtifactUpdate;
