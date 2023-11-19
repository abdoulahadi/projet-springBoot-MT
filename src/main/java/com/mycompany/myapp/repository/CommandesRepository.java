package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Commandes;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Commandes entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CommandesRepository extends ReactiveCrudRepository<Commandes, Long>, CommandesRepositoryInternal {
    @Override
    Mono<Commandes> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Commandes> findAllWithEagerRelationships();

    @Override
    Flux<Commandes> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM commandes entity WHERE entity.clients_id = :id")
    Flux<Commandes> findByClients(Long id);

    @Query("SELECT * FROM commandes entity WHERE entity.clients_id IS NULL")
    Flux<Commandes> findAllWhereClientsIsNull();

    @Query("SELECT * FROM commandes entity WHERE entity.produits_id = :id")
    Flux<Commandes> findByProduits(Long id);

    @Query("SELECT * FROM commandes entity WHERE entity.produits_id IS NULL")
    Flux<Commandes> findAllWhereProduitsIsNull();

    @Override
    <S extends Commandes> Mono<S> save(S entity);

    @Override
    Flux<Commandes> findAll();

    @Override
    Mono<Commandes> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface CommandesRepositoryInternal {
    <S extends Commandes> Mono<S> save(S entity);

    Flux<Commandes> findAllBy(Pageable pageable);

    Flux<Commandes> findAll();

    Mono<Commandes> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Commandes> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Commandes> findOneWithEagerRelationships(Long id);

    Flux<Commandes> findAllWithEagerRelationships();

    Flux<Commandes> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
