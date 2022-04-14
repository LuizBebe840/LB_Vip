package com.luizbebe.vip.managers;

import com.google.gson.Gson;
import com.luizbebe.vip.Main;
import com.luizbebe.vip.dao.UserDAO;
import com.luizbebe.vip.data.user.User;
import com.luizbebe.vip.storage.DBProvider;
import com.luizbebe.vip.storage.transform.UserTransform;
import lombok.val;

import java.sql.SQLException;
import java.util.UUID;

public class UserManager {

    protected Main main;

    private final DBProvider db;
    private final Gson gson;

    public UserManager(Main main) {
        this.main = main;

        db = main.getDbProvider();
        gson = main.getGson();
    }

    public void createAccount(UUID uuid) {
        val user = new User(uuid);
        UserDAO.getUsers().add(user);

        try (val preparedStatement = db.getConnection().prepareStatement("INSERT INTO `vip_users` (uuid, user) VALUES(?,?)")) {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, gson.toJson(user));
            preparedStatement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public boolean hasAccount(UUID uuid) {
        return db.getValue("vip_users", "uuid", uuid.toString(), 1) != null;
    }

    public User getUser(UUID uuid) {
        return UserDAO.getUser(uuid);
    }

    public void saveUserData(UUID uuid) {
        try (val preparedStatement = db.getConnection().prepareStatement("SELECT * FROM `vip_users` WHERE uuid='" + uuid.toString() + "'")) {
            val resultSet = preparedStatement.executeQuery();
            val user = new UserTransform(main).applyThrowing(resultSet);

            if (user != null)
                if (!UserDAO.getUsers().contains(user))
                    UserDAO.getUsers().add(user);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void save(User user) {
        db.executeUpdate("UPDATE `vip_users` SET user = ? WHERE uuid='" + user.getUuid().toString() + "'", gson.toJson(user));
    }

}
