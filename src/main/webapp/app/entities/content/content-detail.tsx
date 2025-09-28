import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate, byteSize, openFile } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './content.reducer';

export const ContentDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const contentEntity = useAppSelector(state => state.content.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="contentDetailsHeading">
          <Translate contentKey="evagApp.content.detail.title">Content</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="evagApp.content.id">Id</Translate>
            </span>
          </dt>
          <dd>{contentEntity.id}</dd>
          <dt>
            <span id="fileName">
              <Translate contentKey="evagApp.content.fileName">File Name</Translate>
            </span>
          </dt>
          <dd>{contentEntity.fileName}</dd>
          <dt>
            <span id="dateCreated">
              <Translate contentKey="evagApp.content.dateCreated">Date Created</Translate>
            </span>
          </dt>
          <dd>
            {contentEntity.dateCreated ? <TextFormat value={contentEntity.dateCreated} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="content">
              <Translate contentKey="evagApp.content.content">Content</Translate>
            </span>
          </dt>
          <dd>
            {contentEntity.content ? (
              <div>
                {contentEntity.contentContentType ? (
                  <a onClick={openFile(contentEntity.contentContentType, contentEntity.content)}>
                    <Translate contentKey="entity.action.open">Open</Translate>&nbsp;
                  </a>
                ) : null}
                <span>
                  {contentEntity.contentContentType}, {byteSize(contentEntity.content)}
                </span>
              </div>
            ) : null}
          </dd>
        </dl>
        <Button tag={Link} to="/content" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/content/${contentEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ContentDetail;
