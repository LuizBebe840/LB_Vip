package com.luizbebe.vip.storage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

public interface DBTransform<I> extends Function<ResultSet, I> {

    default I apply(ResultSet resultSet) {
        try {
            return applyThrowing(resultSet);

        } catch (SQLException e) {
            return null;
        }

    }

    I applyThrowing(ResultSet resultSet) throws SQLException;

}
