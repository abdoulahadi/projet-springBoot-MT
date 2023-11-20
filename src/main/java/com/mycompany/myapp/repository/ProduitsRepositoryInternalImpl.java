package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Produits;
import com.mycompany.myapp.repository.rowmapper.CategoriesRowMapper;
import com.mycompany.myapp.repository.rowmapper.ProduitsRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Produits entity.
 */
@SuppressWarnings("unused")
class ProduitsRepositoryInternalImpl extends SimpleR2dbcRepository<Produits, Long> implements ProduitsRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CategoriesRowMapper categoriesMapper;
    private final ProduitsRowMapper produitsMapper;

    private static final Table entityTable = Table.aliased("produits", EntityManager.ENTITY_ALIAS);
    private static final Table categoriesTable = Table.aliased("categories", "categories");

    public ProduitsRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CategoriesRowMapper categoriesMapper,
        ProduitsRowMapper produitsMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Produits.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.categoriesMapper = categoriesMapper;
        this.produitsMapper = produitsMapper;
    }

    @Override
    public Flux<Produits> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Produits> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ProduitsSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CategoriesSqlHelper.getColumns(categoriesTable, "categories"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(categoriesTable)
            .on(Column.create("categories_id", entityTable))
            .equals(Column.create("id", categoriesTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Produits.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Produits> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Produits> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Produits process(Row row, RowMetadata metadata) {
        Produits entity = produitsMapper.apply(row, "e");
        entity.setCategories(categoriesMapper.apply(row, "categories"));
        return entity;
    }

    @Override
    public <S extends Produits> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
