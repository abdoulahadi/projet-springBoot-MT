package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Categories;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Categories}, with proper type conversions.
 */
@Service
public class CategoriesRowMapper implements BiFunction<Row, String, Categories> {

    private final ColumnConverter converter;

    public CategoriesRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Categories} stored in the database.
     */
    @Override
    public Categories apply(Row row, String prefix) {
        Categories entity = new Categories();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNomCategorie(converter.fromRow(row, prefix + "_nom_categorie", String.class));
        return entity;
    }
}
