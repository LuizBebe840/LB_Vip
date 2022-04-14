package com.luizbebe.vip.commands;

import com.luizbebe.vip.Main;
import com.luizbebe.vip.inventories.VipTimeInventory;
import com.luizbebe.vip.registry.CommandRegistry;
import lombok.val;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class VipTimeCommand extends CommandRegistry {

    public VipTimeCommand(Main main) {
        super(main, main.getConfig(), "viptime");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender)
            return true;

        val player = (Player) sender;
        new VipTimeInventory(main).myVips(player);
        return false;
    }

}
