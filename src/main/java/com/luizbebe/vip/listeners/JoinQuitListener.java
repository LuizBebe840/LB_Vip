package com.luizbebe.vip.listeners;

import com.luizbebe.vip.Main;
import com.luizbebe.vip.dao.UserDAO;
import com.luizbebe.vip.hook.Group;
import com.luizbebe.vip.managers.UserManager;
import com.luizbebe.vip.registry.EventRegistry;
import lombok.val;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitListener extends EventRegistry {

    private final Group group;

    private final UserManager userManager;

    public JoinQuitListener(Main main) {
        super(main, main.getConfig());

        group = main.getGroup();

        userManager = main.getUserManager();
    }

    @EventHandler
    void playerJoin(PlayerJoinEvent event) {
        val player = event.getPlayer();
        if (!userManager.hasAccount(player.getUniqueId()))
            userManager.createAccount(player.getUniqueId());

        userManager.saveUserData(player.getUniqueId());
        val user = UserDAO.getUser(player.getUniqueId());

        if (user.getCurrentVip() != null)
            group.setGroup(player, user.getCurrentVip().getGroup());
    }

    @EventHandler
    void playerQuit(PlayerQuitEvent event) {
        val player = event.getPlayer();
        val user = UserDAO.getUser(player.getUniqueId());

        userManager.save(user);
        UserDAO.getUsers().remove(user);
    }

}
