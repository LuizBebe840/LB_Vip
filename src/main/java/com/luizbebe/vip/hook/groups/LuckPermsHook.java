package com.luizbebe.vip.hook.groups;

import com.luizbebe.vip.hook.Group;
import com.luizbebe.vip.utils.LBUtils;
import lombok.val;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.entity.Player;

public class LuckPermsHook implements Group {

    private final LuckPerms luckPerms;

    public LuckPermsHook() {
        luckPerms = LuckPermsProvider.get();

        LBUtils.getLogger("DEBUG", "§fHooked §bLuckPerms");
    }

    @Override
    public String getGroup(Player player) {
        val user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user == null)
            return "";

        val metaData = user.getCachedData().getMetaData();
        return metaData.getPrefix();
    }

    @Override
    public void setGroup(Player player, String group) {
        val user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user != null)
            user.setPrimaryGroup(group);
    }

}
