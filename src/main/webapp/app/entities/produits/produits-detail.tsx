import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './produits.reducer';

export const ProduitsDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const produitsEntity = useAppSelector(state => state.produits.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="produitsDetailsHeading">
          <Translate contentKey="jobMultiTiersApp.produits.detail.title">Produits</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{produitsEntity.id}</dd>
          <dt>
            <span id="idProduit">
              <Translate contentKey="jobMultiTiersApp.produits.idProduit">Id Produit</Translate>
            </span>
          </dt>
          <dd>{produitsEntity.idProduit}</dd>
          <dt>
            <span id="nomProduit">
              <Translate contentKey="jobMultiTiersApp.produits.nomProduit">Nom Produit</Translate>
            </span>
          </dt>
          <dd>{produitsEntity.nomProduit}</dd>
          <dt>
            <span id="descriptionProduit">
              <Translate contentKey="jobMultiTiersApp.produits.descriptionProduit">Description Produit</Translate>
            </span>
          </dt>
          <dd>{produitsEntity.descriptionProduit}</dd>
          <dt>
            <span id="prixProduit">
              <Translate contentKey="jobMultiTiersApp.produits.prixProduit">Prix Produit</Translate>
            </span>
          </dt>
          <dd>{produitsEntity.prixProduit}</dd>
          <dt>
            <span id="imageProduit">
              <Translate contentKey="jobMultiTiersApp.produits.imageProduit">Image Produit</Translate>
            </span>
          </dt>
          <dd>{produitsEntity.imageProduit}</dd>
          <dt>
            <Translate contentKey="jobMultiTiersApp.produits.categories">Categories</Translate>
          </dt>
          <dd>{produitsEntity.categories ? produitsEntity.categories.idCategorie : ''}</dd>
        </dl>
        <Button tag={Link} to="/produits" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/produits/${produitsEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ProduitsDetail;
