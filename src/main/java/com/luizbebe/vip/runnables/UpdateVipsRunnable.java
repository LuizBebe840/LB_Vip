package com.luizbebe.vip.runnables;

import com.luizbebe.vip.Main;
import com.luizbebe.vip.controllers.UserController;
import com.luizbebe.vip.dao.UserDAO;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class UpdateVipsRunnable extends BukkitRunnable {

    private final UserController userController;

    private final boolean countOffline;

    public UpdateVipsRunnable(Main main) {
        userController = main.getUserController();

        countOffline = main.getConfig().getBoolean("Vip-Settings.Count-Offline");
    }

    @Override
    public void run() {
        if (countOffline)
            UserDAO.getUsers().stream().filter(users -> !users.getVips().isEmpty()).forEach(userController::updateVIPs);

        else
            Bukkit.getOnlinePlayers().stream().filter(players -> !UserDAO.getUser(players.getUniqueId()).getVips().isEmpty()).forEach(players -> userController.updateVIPs(UserDAO.getUser(players.getUniqueId())));
    }

}
