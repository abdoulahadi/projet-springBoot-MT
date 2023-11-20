import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './commandes.reducer';

export const CommandesDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const commandesEntity = useAppSelector(state => state.commandes.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="commandesDetailsHeading">
          <Translate contentKey="multitiersApp.commandes.detail.title">Commandes</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{commandesEntity.id}</dd>
          <dt>
            <span id="dateCommande">
              <Translate contentKey="multitiersApp.commandes.dateCommande">Date Commande</Translate>
            </span>
          </dt>
          <dd>
            {commandesEntity.dateCommande ? <TextFormat value={commandesEntity.dateCommande} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <Translate contentKey="multitiersApp.commandes.clients">Clients</Translate>
          </dt>
          <dd>{commandesEntity.clients ? commandesEntity.clients.id : ''}</dd>
          <dt>
            <Translate contentKey="multitiersApp.commandes.produits">Produits</Translate>
          </dt>
          <dd>{commandesEntity.produits ? commandesEntity.produits.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/commandes" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/commandes/${commandesEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CommandesDetail;
