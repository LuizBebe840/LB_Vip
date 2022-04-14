package com.luizbebe.vip.commands;

import com.luizbebe.vip.Main;
import com.luizbebe.vip.dao.KeyDAO;
import com.luizbebe.vip.managers.KeyManager;
import com.luizbebe.vip.registry.CommandRegistry;
import com.luizbebe.vip.utils.LBUtils;
import lombok.val;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class DeleteKeyCommand extends CommandRegistry {

    private final KeyManager keyManager;

    public DeleteKeyCommand(Main main) {
        super(main, main.getConfig(), "deletekey");

        keyManager = main.getKeyManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!LBUtils.hasPermission(sender, "lbvip.deletekey"))
            return true;

        if (args.length == 0) {
            LBUtils.sendMessage(sender, "§cUse: /deletekey <key>");
            LBUtils.playSound(sender, Sound.NOTE_PLING);
            return true;

        }
        val key = KeyDAO.getKey(args[0].toUpperCase());
        if (key == null) {
            LBUtils.sendMessage(sender, config.getString("Messages.Invalid-Key"));
            LBUtils.playSound(sender, Sound.valueOf(config.getString("Sounds.Invalid-Key").toUpperCase()));
            return true;

        }
        val message = config.getString("Messages.Delete-Key").replace("{key}", key.getId());

        LBUtils.sendMessage(sender, message);
        LBUtils.playSound(sender, Sound.valueOf(config.getString("Sounds.Delete-Key").toUpperCase()));

        keyManager.deleteKey(key);
        return false;
    }

}
