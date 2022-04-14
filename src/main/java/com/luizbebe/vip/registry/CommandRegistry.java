package com.luizbebe.vip.registry;

import com.luizbebe.vip.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public abstract class CommandRegistry implements CommandExecutor {

    protected Main main;

    protected FileConfiguration config;

    public CommandRegistry(Main main, FileConfiguration config, String command) {
        this.main = main;
        this.config = config;
        main.getCommand(command).setExecutor(this);
    }

    public abstract boolean onCommand(CommandSender sender, Command command, String label, String[] args);

}
