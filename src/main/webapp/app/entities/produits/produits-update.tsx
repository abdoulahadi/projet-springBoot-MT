import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { ICategories } from 'app/shared/model/categories.model';
import { getEntities as getCategories } from 'app/entities/categories/categories.reducer';
import { IProduits } from 'app/shared/model/produits.model';
import { getEntity, updateEntity, createEntity, reset } from './produits.reducer';

export const ProduitsUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const categories = useAppSelector(state => state.categories.entities);
  const produitsEntity = useAppSelector(state => state.produits.entity);
  const loading = useAppSelector(state => state.produits.loading);
  const updating = useAppSelector(state => state.produits.updating);
  const updateSuccess = useAppSelector(state => state.produits.updateSuccess);

  const handleClose = () => {
    navigate('/produits');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getCategories({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  // eslint-disable-next-line complexity
  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    if (values.idProduit !== undefined && typeof values.idProduit !== 'number') {
      values.idProduit = Number(values.idProduit);
    }
    if (values.prixProduit !== undefined && typeof values.prixProduit !== 'number') {
      values.prixProduit = Number(values.prixProduit);
    }

    const entity = {
      ...produitsEntity,
      ...values,
      categories: categories.find(it => it.id.toString() === values.categories.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...produitsEntity,
          categories: produitsEntity?.categories?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="jobMultiTiersApp.produits.home.createOrEditLabel" data-cy="ProduitsCreateUpdateHeading">
            <Translate contentKey="jobMultiTiersApp.produits.home.createOrEditLabel">Create or edit a Produits</Translate>
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
                  id="produits-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('jobMultiTiersApp.produits.idProduit')}
                id="produits-idProduit"
                name="idProduit"
                data-cy="idProduit"
                type="text"
              />
              <ValidatedField
                label={translate('jobMultiTiersApp.produits.nomProduit')}
                id="produits-nomProduit"
                name="nomProduit"
                data-cy="nomProduit"
                type="text"
              />
              <ValidatedField
                label={translate('jobMultiTiersApp.produits.descriptionProduit')}
                id="produits-descriptionProduit"
                name="descriptionProduit"
                data-cy="descriptionProduit"
                type="text"
              />
              <ValidatedField
                label={translate('jobMultiTiersApp.produits.prixProduit')}
                id="produits-prixProduit"
                name="prixProduit"
                data-cy="prixProduit"
                type="text"
              />
              <ValidatedField
                label={translate('jobMultiTiersApp.produits.imageProduit')}
                id="produits-imageProduit"
                name="imageProduit"
                data-cy="imageProduit"
                type="text"
              />
              <ValidatedField
                id="produits-categories"
                name="categories"
                data-cy="categories"
                label={translate('jobMultiTiersApp.produits.categories')}
                type="select"
              >
                <option value="" key="0" />
                {categories
                  ? categories.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.idCategorie}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/produits" replace color="info">
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

export default ProduitsUpdate;
