package com.luizbebe.vip.storage.transform;

import com.google.gson.Gson;
import com.luizbebe.vip.Main;
import com.luizbebe.vip.data.user.User;
import com.luizbebe.vip.storage.DBTransform;
import lombok.val;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserTransform implements DBTransform<User> {

    private final Gson gson;

    public UserTransform(Main main) {
        gson = main.getGson();
    }

    @Override
    public User applyThrowing(ResultSet resultSet) throws SQLException {
        val user = resultSet.getString("user");
        return gson.fromJson(user, User.class);
    }

}
