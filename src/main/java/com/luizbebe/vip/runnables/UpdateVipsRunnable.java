package com.luizbebe.vip.runnables;

import com.luizbebe.vip.Main;
import com.luizbebe.vip.controllers.UserController;
import com.luizbebe.vip.dao.UserDAO;
import org.bukkit.scheduler.BukkitRunnable;

public class UpdateVipsRunnable extends BukkitRunnable {

    private final UserController userController;

    public UpdateVipsRunnable(Main main) {
        userController = main.getUserController();
    }

    @Override
    public void run() {
        UserDAO.getUsers().stream().filter(users -> !users.getVips().isEmpty()).forEach(userController::updateVIPs);
    }

}
