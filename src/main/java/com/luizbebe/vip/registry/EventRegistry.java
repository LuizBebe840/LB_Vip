package com.luizbebe.vip.registry;

import com.luizbebe.vip.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;

public class EventRegistry implements Listener {

    protected Main main;

    protected FileConfiguration config;

    public EventRegistry(Main main, FileConfiguration config) {
        this.main = main;
        this.config = config;
        Bukkit.getPluginManager().registerEvents(this, main);
    }

}
