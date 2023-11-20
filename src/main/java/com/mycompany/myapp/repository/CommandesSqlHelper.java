package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class CommandesSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("date_commande", table, columnPrefix + "_date_commande"));

        columns.add(Column.aliased("clients_id", table, columnPrefix + "_clients_id"));
        columns.add(Column.aliased("produits_id", table, columnPrefix + "_produits_id"));
        return columns;
    }
}
