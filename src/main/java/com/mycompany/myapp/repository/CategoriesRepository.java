package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Categories;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Categories entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CategoriesRepository extends ReactiveCrudRepository<Categories, Long>, CategoriesRepositoryInternal {
    @Override
    <S extends Categories> Mono<S> save(S entity);

    @Override
    Flux<Categories> findAll();

    @Override
    Mono<Categories> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface CategoriesRepositoryInternal {
    <S extends Categories> Mono<S> save(S entity);

    Flux<Categories> findAllBy(Pageable pageable);

    Flux<Categories> findAll();

    Mono<Categories> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Categories> findAllBy(Pageable pageable, Criteria criteria);
}
