package com.luizbebe.vip.hook;

import org.bukkit.OfflinePlayer;

public interface Group {

    String getGroup(OfflinePlayer player);

    void setGroup(OfflinePlayer player, String group);

}
