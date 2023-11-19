package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Clients;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Clients}, with proper type conversions.
 */
@Service
public class ClientsRowMapper implements BiFunction<Row, String, Clients> {

    private final ColumnConverter converter;

    public ClientsRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Clients} stored in the database.
     */
    @Override
    public Clients apply(Row row, String prefix) {
        Clients entity = new Clients();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setIdClient(converter.fromRow(row, prefix + "_id_client", Long.class));
        entity.setNom(converter.fromRow(row, prefix + "_nom", String.class));
        entity.setPrenom(converter.fromRow(row, prefix + "_prenom", String.class));
        entity.setAdresse(converter.fromRow(row, prefix + "_adresse", String.class));
        entity.setTelephone(converter.fromRow(row, prefix + "_telephone", String.class));
        entity.setEmail(converter.fromRow(row, prefix + "_email", String.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        return entity;
    }
}
