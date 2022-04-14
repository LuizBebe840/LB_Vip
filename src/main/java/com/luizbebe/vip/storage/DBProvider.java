package com.luizbebe.vip.storage;

import java.sql.Connection;
import java.util.List;

public interface DBProvider {

    void openConnection();

    void closeConnection();

    void executeQuery(String query);

    void executeUpdate(String query, Object... values);

    String getValue(String table, String column, Object value, int columnIndex);

    <I> List<I> findAll(String table, DBTransform<I> transform);

    void createTables();

    Connection getConnection();

}
