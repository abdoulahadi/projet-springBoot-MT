package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Produits;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Produits entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProduitsRepository extends ReactiveCrudRepository<Produits, Long>, ProduitsRepositoryInternal {
    @Override
    Mono<Produits> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Produits> findAllWithEagerRelationships();

    @Override
    Flux<Produits> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM produits entity WHERE entity.categories_id = :id")
    Flux<Produits> findByCategories(Long id);

    @Query("SELECT * FROM produits entity WHERE entity.categories_id IS NULL")
    Flux<Produits> findAllWhereCategoriesIsNull();

    @Override
    <S extends Produits> Mono<S> save(S entity);

    @Override
    Flux<Produits> findAll();

    @Override
    Mono<Produits> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ProduitsRepositoryInternal {
    <S extends Produits> Mono<S> save(S entity);

    Flux<Produits> findAllBy(Pageable pageable);

    Flux<Produits> findAll();

    Mono<Produits> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Produits> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Produits> findOneWithEagerRelationships(Long id);

    Flux<Produits> findAllWithEagerRelationships();

    Flux<Produits> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
