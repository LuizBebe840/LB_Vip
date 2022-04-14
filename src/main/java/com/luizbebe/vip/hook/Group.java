package com.luizbebe.vip.hook;

import org.bukkit.entity.Player;

public interface Group {

    String getGroup(Player player);

    void setGroup(Player player, String group);

}
