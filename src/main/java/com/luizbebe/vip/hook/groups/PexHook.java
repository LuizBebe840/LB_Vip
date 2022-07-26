package com.luizbebe.vip.hook.groups;

import com.luizbebe.vip.hook.Group;
import com.luizbebe.vip.utils.LBUtils;
import lombok.val;
import org.bukkit.OfflinePlayer;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.Collections;

public class PexHook implements Group {

    private final PermissionManager permissionManager;

    public PexHook() {
        permissionManager = PermissionsEx.getPermissionManager();

        LBUtils.getLogger("DEBUG", "§bPermissionsEx §fhooked");
    }

    @Override
    public String getGroup(OfflinePlayer player) {
        val user = permissionManager.getUser(player.getUniqueId());
        if (user.getPrefix() == null)
            return "";

        return user.getPrefix();
    }

    @Override
    public void setGroup(OfflinePlayer player, String group) {
        val user = permissionManager.getUser(player.getUniqueId());
        val groups = Collections.singletonList(permissionManager.getGroup(group));

        user.setParents(groups);
    }

}
