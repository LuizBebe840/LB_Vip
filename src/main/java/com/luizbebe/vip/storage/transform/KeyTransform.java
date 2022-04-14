package com.luizbebe.vip.storage.transform;

import com.google.gson.Gson;
import com.luizbebe.vip.Main;
import com.luizbebe.vip.data.vip.key.Key;
import com.luizbebe.vip.storage.DBTransform;
import lombok.val;

import java.sql.ResultSet;
import java.sql.SQLException;

public class KeyTransform implements DBTransform<Key> {

    private final Gson gson;

    public KeyTransform(Main main) {
        gson = main.getGson();
    }

    @Override
    public Key applyThrowing(ResultSet resultSet) throws SQLException {
        val key = resultSet.getString("key");
        return gson.fromJson(key, Key.class);
    }

}
