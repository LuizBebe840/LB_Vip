package com.luizbebe.vip.commands;

import com.luizbebe.vip.Main;
import com.luizbebe.vip.controllers.UserController;
import com.luizbebe.vip.dao.UserDAO;
import com.luizbebe.vip.registry.CommandRegistry;
import com.luizbebe.vip.utils.LBUtils;
import lombok.val;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class ChangeVipCommand extends CommandRegistry {

    private final UserController userController;

    public ChangeVipCommand(Main main) {
        super(main, main.getConfig(), "changevip");

        userController = main.getUserController();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender)
            return true;

        val player = (Player) sender;
        if (args.length == 0) {
            LBUtils.sendMessage(player, "§cUse: /changevip <vip>");
            LBUtils.playSound(player, Sound.NOTE_PLING);
            return true;

        }
        val user = UserDAO.getUser(player.getUniqueId());
        if (user.getVips().isEmpty()) {
            user.sendMessage(config.getString("Messages.Change-Vip.NoVip"));
            user.playSound(Sound.valueOf(config.getString("Sounds.Change-Vip.NoVip").toUpperCase()));
            return true;

        }
        val vip = user.getVip(args[0]);
        if (vip == null) {
            for (val vips : user.getVips())
                user.sendMessage(config.getString("Messages.Your-VIPs").replace("{vips}", " §a➤ §f" + vips.getId()));

            user.playSound(Sound.valueOf(config.getString("Sounds.Your-VIPs").toUpperCase()));
            return true;

        }
        userController.changeVip(user, vip);
        return false;
    }

}
