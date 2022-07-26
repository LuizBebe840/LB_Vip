package com.luizbebe.vip;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.luizbebe.vip.commands.*;
import com.luizbebe.vip.controllers.PaperVipController;
import com.luizbebe.vip.controllers.UserController;
import com.luizbebe.vip.controllers.VipController;
import com.luizbebe.vip.dao.KeyDAO;
import com.luizbebe.vip.dao.UserDAO;
import com.luizbebe.vip.files.MenusFile;
import com.luizbebe.vip.files.PaperVipFile;
import com.luizbebe.vip.files.VipsFile;
import com.luizbebe.vip.hook.Group;
import com.luizbebe.vip.hook.groups.LuckPermsHook;
import com.luizbebe.vip.hook.groups.PexHook;
import com.luizbebe.vip.listeners.JoinListener;
import com.luizbebe.vip.listeners.KeysListener;
import com.luizbebe.vip.listeners.PaperVipListener;
import com.luizbebe.vip.listeners.VipTimeListener;
import com.luizbebe.vip.managers.KeyManager;
import com.luizbebe.vip.managers.UserManager;
import com.luizbebe.vip.runnables.UpdateVipsRunnable;
import com.luizbebe.vip.storage.DBProvider;
import com.luizbebe.vip.storage.providers.MySQL;
import com.luizbebe.vip.storage.providers.SQLite;
import com.luizbebe.vip.utils.LBUtils;
import lombok.Getter;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Main extends JavaPlugin {

    private DBProvider dbProvider;
    private Gson gson;

    private Group group;

    private VipsFile vipsFile;
    private MenusFile menusFile;
    private PaperVipFile paperVipFile;

    private UserManager userManager;
    private KeyManager keyManager;

    private UserController userController;
    private VipController vipController;
    private PaperVipController paperVipController;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        LBUtils.sendMessage(Bukkit.getConsoleSender(), "§fPlugin started, join my Discord Server:");
        LBUtils.sendMessage(Bukkit.getConsoleSender(), "§bhttps://discord.gg/tVtRaKnJBw");
        setupSQL();
        registerObjects();
        register();

        Bukkit.getOnlinePlayers().forEach(players -> userManager.saveUserData(players.getUniqueId()));
        start();
    }

    @Override
    public void onDisable() {
        UserDAO.getUsers().forEach(userManager::save);
        KeyDAO.getKeys().forEach(keyManager::save);

        dbProvider.closeConnection();
        Bukkit.getScheduler().cancelTasks(this);
    }

    private void register() {
        new GiveVipCommand(this);
        new ChangeVipCommand(this);
        new RemoveVipCommand(this);
        new VipTimeCommand(this);
        new GenerateKeyCommand(this);
        new UseKeyCommand(this);
        new DeleteKeyCommand(this);
        new KeysCommand(this);
        new PaperVipCommand(this);
        new JoinListener(this);
        new VipTimeListener(this);
        new KeysListener(this);
        new PaperVipListener(this);
    }

    private void registerObjects() {
        group = Bukkit.getPluginManager().getPlugin("LuckPerms") != null ? new LuckPermsHook() : new PexHook();

        vipsFile = new VipsFile(this);
        menusFile = new MenusFile(this);
        paperVipFile = new PaperVipFile(this);

        userManager = new UserManager(this);
        keyManager = new KeyManager(this);

        userController = new UserController(this);
        vipController = new VipController(this);
        paperVipController = new PaperVipController(this);
    }

    private void setupSQL() {
        dbProvider = getConfig().getBoolean("MySQL.Enable") ? new MySQL(this) : new SQLite();
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    private void start() {
        val update = new UpdateVipsRunnable(this);
        if (Bukkit.getPluginManager().getPlugin("LuckPerms") == null)
            update.runTaskTimerAsynchronously(this, 20L, 20L);

        else
            update.runTaskTimer(this, 20L, 20L);
    }

}
