package com.luizbebe.vip.commands;

import com.luizbebe.vip.Main;
import com.luizbebe.vip.controllers.VipController;
import com.luizbebe.vip.dao.KeyDAO;
import com.luizbebe.vip.dao.UserDAO;
import com.luizbebe.vip.managers.KeyManager;
import com.luizbebe.vip.registry.CommandRegistry;
import com.luizbebe.vip.utils.LBUtils;
import com.luizbebe.vip.utils.TimeFormat;
import lombok.val;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class UseKeyCommand extends CommandRegistry {

    private final KeyManager keyManager;

    private final VipController vipController;

    public UseKeyCommand(Main main) {
        super(main, main.getConfig(), "usekey");

        keyManager = main.getKeyManager();

        vipController = main.getVipController();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender)
            return true;

        val player = (Player) sender;
        if (args.length == 0) {
            LBUtils.sendMessage(player, "§cUse: /usekey <key>");
            LBUtils.playSound(player, Sound.NOTE_PLING);
            return true;

        }
        val key = KeyDAO.getKey(args[0]);
        if (key == null) {
            LBUtils.sendMessage(sender, config.getString("Messages.Invalid-Key"));
            LBUtils.playSound(sender, Sound.valueOf(config.getString("Sounds.Invalid-Key").toUpperCase()));
            return true;

        }
        val user = UserDAO.getUser(player.getUniqueId());
        val time = key.getVip().getTime();
        val targetVip = user.getVip(key.getVip().getId());
        if (targetVip != null) {
            if (targetVip.isEternal()) {
                LBUtils.sendMessage(sender, "§cThis VIP is eternal!");
                LBUtils.playSound(sender, Sound.NOTE_PLING);
                return true;

            }
            vipController.addAlreadyVip(player, key.getVip(), time);

        } else
            vipController.addVip(player, key.getVip(), time);

        val message = config.getString("Messages.Actived-Key").replace("{key}", key.getId()).replace("{time}", TimeFormat.format(time)).replace("{name}", key.getVip().getName());

        LBUtils.sendMessage(player, message);
        LBUtils.playSound(player, Sound.valueOf(config.getString("Sounds.Actived-Key").toUpperCase()));

        keyManager.deleteKey(key);
        return false;
    }

}
