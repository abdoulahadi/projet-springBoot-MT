package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ProduitsSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("id_produit", table, columnPrefix + "_id_produit"));
        columns.add(Column.aliased("nom_produit", table, columnPrefix + "_nom_produit"));
        columns.add(Column.aliased("description_produit", table, columnPrefix + "_description_produit"));
        columns.add(Column.aliased("prix_produit", table, columnPrefix + "_prix_produit"));
        columns.add(Column.aliased("image_produit", table, columnPrefix + "_image_produit"));

        columns.add(Column.aliased("categories_id", table, columnPrefix + "_categories_id"));
        return columns;
    }
}
