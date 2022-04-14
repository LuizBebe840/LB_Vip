package com.luizbebe.vip.dao;

import com.luizbebe.vip.Main;
import com.luizbebe.vip.data.vip.key.Key;
import com.luizbebe.vip.storage.DBProvider;
import com.luizbebe.vip.storage.transform.KeyTransform;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class KeyDAO {

    protected static Main main = Main.getPlugin(Main.class);

    private static final DBProvider db = main.getDbProvider();

    @Getter
    private static final List<Key> keys = new ArrayList<>();

    public static void loadKeys() {
        keys.addAll(db.findAll("vip_keys", new KeyTransform(main)));
    }

    public static Key getKey(String keyId) {
        return keys.stream().filter(keys -> keys.getId().equals(keyId)).findFirst().orElse(null);
    }

}
