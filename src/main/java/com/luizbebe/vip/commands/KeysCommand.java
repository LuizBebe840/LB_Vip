package com.luizbebe.vip.commands;

import com.luizbebe.vip.Main;
import com.luizbebe.vip.inventories.KeysInventory;
import com.luizbebe.vip.registry.CommandRegistry;
import com.luizbebe.vip.utils.LBUtils;
import lombok.val;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class KeysCommand extends CommandRegistry {

    public KeysCommand(Main main) {
        super(main, main.getConfig(), "keys");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender)
            return true;

        val player = (Player) sender;
        if (!LBUtils.hasPermission(player, "lbvip.seekeys"))
            return true;

        new KeysInventory(main).allKeys(player);
        return false;
    }

}
