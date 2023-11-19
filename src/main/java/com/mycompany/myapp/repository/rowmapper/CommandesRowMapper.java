package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Commandes;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Commandes}, with proper type conversions.
 */
@Service
public class CommandesRowMapper implements BiFunction<Row, String, Commandes> {

    private final ColumnConverter converter;

    public CommandesRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Commandes} stored in the database.
     */
    @Override
    public Commandes apply(Row row, String prefix) {
        Commandes entity = new Commandes();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setIdCommande(converter.fromRow(row, prefix + "_id_commande", Long.class));
        entity.setDateCommande(converter.fromRow(row, prefix + "_date_commande", Instant.class));
        entity.setClientsId(converter.fromRow(row, prefix + "_clients_id", Long.class));
        entity.setProduitsId(converter.fromRow(row, prefix + "_produits_id", Long.class));
        return entity;
    }
}
