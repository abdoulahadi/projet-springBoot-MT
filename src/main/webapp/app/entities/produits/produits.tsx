import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { byteSize, Translate, getSortState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortUp, faSortDown } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC, SORT } from 'app/shared/util/pagination.constants';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './produits.reducer';

export const Produits = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const produitsList = useAppSelector(state => state.produits.entities);
  const loading = useAppSelector(state => state.produits.loading);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        sort: `${sortState.sort},${sortState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?sort=${sortState.sort},${sortState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [sortState.order, sortState.sort]);

  const sort = p => () => {
    setSortState({
      ...sortState,
      order: sortState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = sortState.sort;
    const order = sortState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    } else {
      return order === ASC ? faSortUp : faSortDown;
    }
  };
  const displayFirstImage = produit => {
    // Vérifiez si imageProduit est défini
    if (produit && produit.imageProduit) {
      // Split the concatenated string into an array of image strings
      const imagesArray = produit.imageProduit.split('*');

      // Récupérez seulement le premier élément du tableau
      const firstImage = imagesArray[0];

      // Retournez l'élément img avec la première image
      return <img src={`${firstImage}`} alt={`Image ${produit.nomProduit}`} style={{ maxWidth: '100%', marginBottom: '10px' }} />;
    }

    // Si imageProduit n'est pas défini, renvoyez un message ou un rendu alternatif
    return <p>No images available</p>;
  };

  return (
    <div>
      <h2 id="produits-heading" data-cy="ProduitsHeading">
        <Translate contentKey="multitiersApp.produits.home.title">Produits</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="multitiersApp.produits.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/produits/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="multitiersApp.produits.home.createLabel">Create new Produits</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {produitsList && produitsList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="multitiersApp.produits.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('nomProduit')}>
                  <Translate contentKey="multitiersApp.produits.nomProduit">Nom Produit</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('nomProduit')} />
                </th>
                <th className="hand" onClick={sort('descriptionProduit')}>
                  <Translate contentKey="multitiersApp.produits.descriptionProduit">Description Produit</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('descriptionProduit')} />
                </th>
                <th className="hand" onClick={sort('prixProduit')}>
                  <Translate contentKey="multitiersApp.produits.prixProduit">Prix Produit</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('prixProduit')} />
                </th>
                <th className="hand" onClick={sort('imageProduit')}>
                  <Translate contentKey="multitiersApp.produits.imageProduit">Image Produit</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('imageProduit')} />
                </th>
                <th>
                  <Translate contentKey="multitiersApp.produits.categories">Categories</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {produitsList.map((produits, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/produits/${produits.id}`} color="link" size="sm">
                      {produits.id}
                    </Button>
                  </td>
                  <td>{produits.nomProduit}</td>
                  <td>{produits.descriptionProduit}</td>
                  <td>{produits.prixProduit}</td>
                  <td>
                    {/* {produits.imageProduit && (
                      <img src={`${produits.imageProduit}`} alt={produits.nomProduit} style={{ maxWidth: '100%' }} />
                    )} */}
                    <dd>{displayFirstImage(produits)}</dd>
                  </td>
                  <td>{produits.categories ? <Link to={`/categories/${produits.categories.id}`}>{produits.categories.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/produits/${produits.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/produits/${produits.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() => (location.href = `/produits/${produits.id}/delete`)}
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="multitiersApp.produits.home.notFound">No Produits found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default Produits;
