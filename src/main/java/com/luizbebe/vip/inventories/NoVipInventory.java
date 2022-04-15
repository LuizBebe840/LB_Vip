package com.luizbebe.vip.inventories;

import com.luizbebe.vip.Main;
import com.luizbebe.vip.utils.ItemBuilder;
import com.luizbebe.vip.utils.LBUtils;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class NoVipInventory {

    private final FileConfiguration menusConfig;

    private final Sound openMenuSound;

    private final String title;
    private final int size;
    private final int slot;

    public NoVipInventory(Main main) {
        val config = main.getConfig();

        menusConfig = main.getMenusFile().getConfig();

        openMenuSound = Sound.valueOf(config.getString("Sounds.Open-Menu").toUpperCase());

        title = menusConfig.getString("Vip-Time.Title").replace("&", "§");
        size = menusConfig.getInt("Vip-Time.Size");
        slot = menusConfig.getInt("Vip-Time.Items.NoVip.Slot");
    }

    public void noVip(Player player) {
        val inv = Bukkit.createInventory(null, size * 9, title);

        inv.setItem(slot, getIcon());

        player.openInventory(inv);
        LBUtils.playSound(player, openMenuSound);
    }

    public ItemStack getIcon() {
        val name = menusConfig.getString("Vip-Time.Items.NoVip.Name");
        val lore = menusConfig.getStringList("Vip-Time.Items.NoVip.Lore");

        val url = menusConfig.getString("Vip-Time.Items.NoVip.Icon.Skull-URL");
        val customSkull = menusConfig.getBoolean("Vip-Time.Items.NoVip.Icon.Custom-Skull");

        val material = Material.valueOf(menusConfig.getString("Vip-Time.Items.NoVip.Icon.Material").toUpperCase().split(":")[0]);
        val data = Integer.parseInt(menusConfig.getString("Vip-Time.Items.NoVip.Icon.Material").split(":")[1]);
        val glow = Boolean.parseBoolean(menusConfig.getString("Vip-Time.Items.NoVip.Icon.Material").split(":")[2]);

        val item = new ItemBuilder(material, 1, data).setName(name).setLore(lore).setGlow(glow).build();
        val head = new ItemBuilder(url).setName(name).setLore(lore).build();

        return customSkull ? head : item;
    }

}
