package com.luizbebe.vip.managers;

import com.google.gson.Gson;
import com.luizbebe.vip.Main;
import com.luizbebe.vip.dao.KeyDAO;
import com.luizbebe.vip.data.vip.Vip;
import com.luizbebe.vip.data.vip.key.Key;
import com.luizbebe.vip.storage.DBProvider;
import lombok.val;

import java.sql.SQLException;

public class KeyManager {

    protected Main main;

    private final DBProvider db;
    private final Gson gson;

    public KeyManager(Main main) {
        this.main = main;

        db = main.getDbProvider();
        gson = main.getGson();

        KeyDAO.loadKeys();
    }

    public void createKey(Vip vip, String keyId) {
        val key = new Key(vip, keyId.toUpperCase());

        try (val preparedStatement = db.getConnection().prepareStatement("INSERT INTO `vip_keys` (id, key) VALUES(?,?)")) {
            preparedStatement.setString(1, keyId.toUpperCase());
            preparedStatement.setString(2, gson.toJson(key));
            preparedStatement.execute();

            KeyDAO.getKeys().add(key);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public boolean hasKey(String keyId) {
        return db.getValue("vip_keys", "id", keyId, 1) != null;
    }

    public void deleteKey(Key key) {
        if (hasKey(key.getId())) {
            db.executeUpdate("DELETE FROM `vip_keys` WHERE id = ?", key.getId());
            db.executeUpdate("DELETE FROM `vip_keys` WHERE key = ?", gson.toJson(key));

        }
        KeyDAO.getKeys().remove(key);
    }

    public void save(Key key) {
        db.executeUpdate("UPDATE `vip_keys` SET key = ? WHERE id='" + key.getId() + "'", gson.toJson(key));
    }

}
