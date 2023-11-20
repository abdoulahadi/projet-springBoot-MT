import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IClients } from 'app/shared/model/clients.model';
import { getEntities as getClients } from 'app/entities/clients/clients.reducer';
import { IProduits } from 'app/shared/model/produits.model';
import { getEntities as getProduits } from 'app/entities/produits/produits.reducer';
import { ICommandes } from 'app/shared/model/commandes.model';
import { getEntity, updateEntity, createEntity, reset } from './commandes.reducer';

export const CommandesUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const clients = useAppSelector(state => state.clients.entities);
  const produits = useAppSelector(state => state.produits.entities);
  const commandesEntity = useAppSelector(state => state.commandes.entity);
  const loading = useAppSelector(state => state.commandes.loading);
  const updating = useAppSelector(state => state.commandes.updating);
  const updateSuccess = useAppSelector(state => state.commandes.updateSuccess);

  const handleClose = () => {
    navigate('/commandes');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getClients({}));
    dispatch(getProduits({}));
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
    values.dateCommande = convertDateTimeToServer(values.dateCommande);

    const entity = {
      ...commandesEntity,
      ...values,
      clients: clients.find(it => it.id.toString() === values.clients.toString()),
      produits: produits.find(it => it.id.toString() === values.produits.toString()),
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
          dateCommande: displayDefaultDateTime(),
        }
      : {
          ...commandesEntity,
          dateCommande: convertDateTimeFromServer(commandesEntity.dateCommande),
          clients: commandesEntity?.clients?.id,
          produits: commandesEntity?.produits?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="multitiersApp.commandes.home.createOrEditLabel" data-cy="CommandesCreateUpdateHeading">
            <Translate contentKey="multitiersApp.commandes.home.createOrEditLabel">Create or edit a Commandes</Translate>
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
                  id="commandes-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('multitiersApp.commandes.dateCommande')}
                id="commandes-dateCommande"
                name="dateCommande"
                data-cy="dateCommande"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="commandes-clients"
                name="clients"
                data-cy="clients"
                label={translate('multitiersApp.commandes.clients')}
                type="select"
              >
                <option value="" key="0" />
                {clients
                  ? clients.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="commandes-produits"
                name="produits"
                data-cy="produits"
                label={translate('multitiersApp.commandes.produits')}
                type="select"
              >
                <option value="" key="0" />
                {produits
                  ? produits.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/commandes" replace color="info">
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

export default CommandesUpdate;
