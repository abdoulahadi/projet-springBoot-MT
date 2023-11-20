package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Produits;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Produits}, with proper type conversions.
 */
@Service
public class ProduitsRowMapper implements BiFunction<Row, String, Produits> {

    private final ColumnConverter converter;

    public ProduitsRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Produits} stored in the database.
     */
    @Override
    public Produits apply(Row row, String prefix) {
        Produits entity = new Produits();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNomProduit(converter.fromRow(row, prefix + "_nom_produit", String.class));
        entity.setDescriptionProduit(converter.fromRow(row, prefix + "_description_produit", String.class));
        entity.setPrixProduit(converter.fromRow(row, prefix + "_prix_produit", Long.class));
        entity.setImageProduit(converter.fromRow(row, prefix + "_image_produit", String.class));
        entity.setCategoriesId(converter.fromRow(row, prefix + "_categories_id", Long.class));
        return entity;
    }
}
