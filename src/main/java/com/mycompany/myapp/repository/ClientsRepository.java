package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Clients;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Clients entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClientsRepository extends ReactiveCrudRepository<Clients, Long>, ClientsRepositoryInternal {
    @Query("SELECT * FROM clients entity WHERE entity.user_id = :id")
    Flux<Clients> findByUser(Long id);

    @Query("SELECT * FROM clients entity WHERE entity.user_id IS NULL")
    Flux<Clients> findAllWhereUserIsNull();

    @Override
    <S extends Clients> Mono<S> save(S entity);

    @Override
    Flux<Clients> findAll();

    @Override
    Mono<Clients> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ClientsRepositoryInternal {
    <S extends Clients> Mono<S> save(S entity);

    Flux<Clients> findAllBy(Pageable pageable);

    Flux<Clients> findAll();

    Mono<Clients> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Clients> findAllBy(Pageable pageable, Criteria criteria);
}
