package com.luizbebe.vip.dao;

import com.luizbebe.vip.data.user.User;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserDAO {

    @Getter
    private static final List<User> users = new ArrayList<>();

    public static User getUser(UUID uuid) {
        return users.stream().filter(user -> user.getUuid().toString().equals(uuid.toString())).findFirst().orElse(null);
    }

}
