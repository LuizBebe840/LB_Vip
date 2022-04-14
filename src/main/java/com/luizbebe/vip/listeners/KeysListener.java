package com.luizbebe.vip.listeners;

import com.luizbebe.vip.Main;
import com.luizbebe.vip.dao.KeyDAO;
import com.luizbebe.vip.inventories.KeysInventory;
import com.luizbebe.vip.managers.KeyManager;
import com.luizbebe.vip.registry.EventRegistry;
import com.luizbebe.vip.utils.LBUtils;
import lombok.val;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

public class KeysListener extends EventRegistry {

    private final KeyManager keyManager;

    private FileConfiguration menusConfig;

    private final String menuTitle;

    public KeysListener(Main main) {
        super(main, main.getConfig());

        menusConfig = main.getMenusFile().getConfig();

        keyManager = main.getKeyManager();

        menuTitle = menusConfig.getString("Keys.Title").replace("&", "§");
    }

    @EventHandler
    void deleteKey(InventoryClickEvent event) {
        val title = event.getView().getTitle();
        if (!title.equals(menuTitle))
            return;

        val item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR)
            return;

        event.setCancelled(true);
        val nmsItem = CraftItemStack.asNMSCopy(item);
        val itemCompound = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();

        if (!itemCompound.hasKey("VipKey-Icon"))
            return;

        val player = (Player) event.getWhoClicked();
        val key = KeyDAO.getKey(itemCompound.getString("VipKey-Icon"));
        if (key == null)
            return;

        if (!LBUtils.hasPermission(player, "lbvip.deletekey"))
            return;

        val message = config.getString("Messages.Delete-Key").replace("{key}", key.getId());

        LBUtils.sendMessage(player, message);
        LBUtils.playSound(player, Sound.valueOf(config.getString("Sounds.Delete-Key").toUpperCase()));

        keyManager.deleteKey(key);
        new KeysInventory(main).allKeys(player);
    }

}
