package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Commandes;
import com.mycompany.myapp.repository.rowmapper.ClientsRowMapper;
import com.mycompany.myapp.repository.rowmapper.CommandesRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Commandes entity.
 */
@SuppressWarnings("unused")
class CommandesRepositoryInternalImpl extends SimpleR2dbcRepository<Commandes, Long> implements CommandesRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ClientsRowMapper clientsMapper;
    private final ProduitsRowMapper produitsMapper;
    private final CommandesRowMapper commandesMapper;

    private static final Table entityTable = Table.aliased("commandes", EntityManager.ENTITY_ALIAS);
    private static final Table clientsTable = Table.aliased("clients", "clients");
    private static final Table produitsTable = Table.aliased("produits", "produits");

    public CommandesRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ClientsRowMapper clientsMapper,
        ProduitsRowMapper produitsMapper,
        CommandesRowMapper commandesMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Commandes.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.clientsMapper = clientsMapper;
        this.produitsMapper = produitsMapper;
        this.commandesMapper = commandesMapper;
    }

    @Override
    public Flux<Commandes> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Commandes> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = CommandesSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ClientsSqlHelper.getColumns(clientsTable, "clients"));
        columns.addAll(ProduitsSqlHelper.getColumns(produitsTable, "produits"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(clientsTable)
            .on(Column.create("clients_id", entityTable))
            .equals(Column.create("id", clientsTable))
            .leftOuterJoin(produitsTable)
            .on(Column.create("produits_id", entityTable))
            .equals(Column.create("id", produitsTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Commandes.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Commandes> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Commandes> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Commandes> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Commandes> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Commandes> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Commandes process(Row row, RowMetadata metadata) {
        Commandes entity = commandesMapper.apply(row, "e");
        entity.setClients(clientsMapper.apply(row, "clients"));
        entity.setProduits(produitsMapper.apply(row, "produits"));
        return entity;
    }

    @Override
    public <S extends Commandes> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
