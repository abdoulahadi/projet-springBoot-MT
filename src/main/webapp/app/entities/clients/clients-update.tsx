import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IClients } from 'app/shared/model/clients.model';
import { getEntity, updateEntity, createEntity, reset } from './clients.reducer';

export const ClientsUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const clientsEntity = useAppSelector(state => state.clients.entity);
  const loading = useAppSelector(state => state.clients.loading);
  const updating = useAppSelector(state => state.clients.updating);
  const updateSuccess = useAppSelector(state => state.clients.updateSuccess);

  const handleClose = () => {
    navigate('/clients');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
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
    if (values.idUser !== undefined && typeof values.idUser !== 'number') {
      values.idUser = Number(values.idUser);
    }

    const entity = {
      ...clientsEntity,
      ...values,
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
          ...clientsEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="jobMultiTiersApp.clients.home.createOrEditLabel" data-cy="ClientsCreateUpdateHeading">
            <Translate contentKey="jobMultiTiersApp.clients.home.createOrEditLabel">Create or edit a Clients</Translate>
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
                  id="clients-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField label={translate('jobMultiTiersApp.clients.nom')} id="clients-nom" name="nom" data-cy="nom" type="text" />
              <ValidatedField
                label={translate('jobMultiTiersApp.clients.prenom')}
                id="clients-prenom"
                name="prenom"
                data-cy="prenom"
                type="text"
              />
              <ValidatedField
                label={translate('jobMultiTiersApp.clients.adresse')}
                id="clients-adresse"
                name="adresse"
                data-cy="adresse"
                type="text"
              />
              <ValidatedField
                label={translate('jobMultiTiersApp.clients.telephone')}
                id="clients-telephone"
                name="telephone"
                data-cy="telephone"
                type="text"
              />
              <ValidatedField
                label={translate('jobMultiTiersApp.clients.email')}
                id="clients-email"
                name="email"
                data-cy="email"
                type="text"
              />
              <ValidatedField
                label={translate('jobMultiTiersApp.clients.idUser')}
                id="clients-idUser"
                name="idUser"
                data-cy="idUser"
                type="text"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/clients" replace color="info">
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

export default ClientsUpdate;
