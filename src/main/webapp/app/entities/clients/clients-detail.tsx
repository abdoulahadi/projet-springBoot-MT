import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './clients.reducer';

export const ClientsDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const clientsEntity = useAppSelector(state => state.clients.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="clientsDetailsHeading">
          <Translate contentKey="multitiersApp.clients.detail.title">Clients</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{clientsEntity.id}</dd>
          <dt>
            <span id="nom">
              <Translate contentKey="multitiersApp.clients.nom">Nom</Translate>
            </span>
          </dt>
          <dd>{clientsEntity.nom}</dd>
          <dt>
            <span id="prenom">
              <Translate contentKey="multitiersApp.clients.prenom">Prenom</Translate>
            </span>
          </dt>
          <dd>{clientsEntity.prenom}</dd>
          <dt>
            <span id="adresse">
              <Translate contentKey="multitiersApp.clients.adresse">Adresse</Translate>
            </span>
          </dt>
          <dd>{clientsEntity.adresse}</dd>
          <dt>
            <span id="telephone">
              <Translate contentKey="multitiersApp.clients.telephone">Telephone</Translate>
            </span>
          </dt>
          <dd>{clientsEntity.telephone}</dd>
          <dt>
            <span id="email">
              <Translate contentKey="multitiersApp.clients.email">Email</Translate>
            </span>
          </dt>
          <dd>{clientsEntity.email}</dd>
          <dt>
            <Translate contentKey="multitiersApp.clients.user">User</Translate>
          </dt>
          <dd>{clientsEntity.user ? clientsEntity.user.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/clients" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/clients/${clientsEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ClientsDetail;
