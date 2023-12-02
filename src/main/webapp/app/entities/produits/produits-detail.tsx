import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, byteSize } from 'react-jhipster';
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

  const displayImages = () => {
    // Vérifiez si imageProduit est défini
    if (produitsEntity && produitsEntity.imageProduit) {
      // Split the concatenated string into an array of image strings
      const imagesArray = produitsEntity.imageProduit.split('*');

      return (
        <div>
          {imagesArray.map((base64Image, index) => (
            <img key={index} src={`${base64Image}`} alt={`Image ${index}`} style={{ maxWidth: '100%', marginBottom: '10px' }} />
          ))}
        </div>
      );
    }

    // Si imageProduit n'est pas défini, renvoyez un message ou un rendu alternatif
    return <p>No images available</p>;
  };
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="produitsDetailsHeading">
          <Translate contentKey="multitiersApp.produits.detail.title">Produits</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{produitsEntity.id}</dd>
          <dt>
            <span id="nomProduit">
              <Translate contentKey="multitiersApp.produits.nomProduit">Nom Produit</Translate>
            </span>
          </dt>
          <dd>{produitsEntity.nomProduit}</dd>
          <dt>
            <span id="descriptionProduit">
              <Translate contentKey="multitiersApp.produits.descriptionProduit">Description Produit</Translate>
            </span>
          </dt>
          <dd>{produitsEntity.descriptionProduit}</dd>
          <dt>
            <span id="prixProduit">
              <Translate contentKey="multitiersApp.produits.prixProduit">Prix Produit</Translate>
            </span>
          </dt>
          <dd>{produitsEntity.prixProduit}</dd>
          <dt>
            <span id="imageProduit">
              <Translate contentKey="multitiersApp.produits.imageProduit">Image Produit</Translate>
            </span>
          </dt>
          <dd>
            {/* {produitsEntity.imageProduit && (
              <img src={`${produitsEntity.imageProduit}`} alt={produitsEntity.nomProduit} style={{ maxWidth: '100%' }} />
            )} */}
            <dd>{displayImages()}</dd>
          </dd>
          <dt>
            <Translate contentKey="multitiersApp.produits.categories">Categories</Translate>
          </dt>
          <dd>{produitsEntity.categories ? produitsEntity.categories.id : ''}</dd>
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
