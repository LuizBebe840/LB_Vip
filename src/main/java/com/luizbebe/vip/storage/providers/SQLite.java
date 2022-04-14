package com.luizbebe.vip.storage.providers;

import com.luizbebe.vip.storage.DBProvider;
import com.luizbebe.vip.storage.DBTransform;
import com.luizbebe.vip.utils.LBUtils;
import lombok.val;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLite implements DBProvider {

    private Connection connection;

    public SQLite() {
        openConnection();
        createTables();
    }

    @Override
    public void openConnection() {
        val file = new File("plugins/LB_Vip/database.db");
        val url = "jdbc:sqlite:" + file;

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(url);

            LBUtils.getLogger("DEBUG", "§fOpened §bSQLite §fconnection");

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void closeConnection() {
        if (connection == null)
            return;

        try {
            connection.close();
            LBUtils.getLogger("DEBUG", "§fClosed §bSQLite §fconnection");

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void executeQuery(String query) {
        try (val preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void executeUpdate(String query, Object... values) {
        try (val preparedStatement = connection.prepareStatement(query)) {
            if (values != null && values.length > 0)
                for (int index = 0; index < values.length; index++)
                    preparedStatement.setObject(index + 1, values[index]);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getValue(String table, String column, Object value, int columnIndex) {
        String valueFinally = null;

        try (val preparedStatement = connection.prepareStatement("SELECT * FROM " + table + " WHERE " + column + "='" + value + "'")) {
            val resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
                valueFinally = resultSet.getString(columnIndex);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return valueFinally;
    }

    @Override
    public <I> List<I> findAll(String table, DBTransform<I> transform) {
        List<I> values = new ArrayList<>();

        try (val preparedStatement = connection.prepareStatement("SELECT * FROM `" + table + "`")) {
            val resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                val value = transform.applyThrowing(resultSet);
                if (value == null)
                    continue;

                values.add(value);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return values;
    }

    @Override
    public void createTables() {
        executeQuery("CREATE TABLE IF NOT EXISTS `vip_users` (uuid TEXT NOT NULL, user TEXT NOT NULL)");
        executeQuery("CREATE TABLE IF NOT EXISTS `vip_keys` (id VARCHAR(64) NOT NULL, key TEXT NOT NULL)");
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

}
