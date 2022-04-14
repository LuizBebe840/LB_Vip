package com.luizbebe.vip.inventories;

import com.luizbebe.vip.Main;
import com.luizbebe.vip.controllers.VipController;
import com.luizbebe.vip.dao.KeyDAO;
import com.luizbebe.vip.data.vip.key.Key;
import com.luizbebe.vip.utils.ItemBuilder;
import com.luizbebe.vip.utils.LBUtils;
import com.luizbebe.vip.utils.Scroller;
import com.luizbebe.vip.utils.TimeFormat;
import lombok.val;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagString;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class KeysInventory {

    protected Main main;

    private final VipController vipController;

    private final FileConfiguration config;
    private final FileConfiguration menusConfig;

    private final Sound openMenuSound;

    private final String title;

    private final List<Integer> allowedSlots;

    private final int size;
    private final int nextSlot;
    private final int previousSlot;

    public KeysInventory(Main main) {
        this.main = main;

        vipController = main.getVipController();

        config = main.getConfig();
        menusConfig = main.getMenusFile().getConfig();

        openMenuSound = Sound.valueOf(config.getString("Sounds.Open-Menu").toUpperCase());

        title = menusConfig.getString("Keys.Title").replace("&", "§");

        allowedSlots = menusConfig.getIntegerList("Keys.Allowed-Slots");

        size = menusConfig.getInt("Keys.Size");
        nextSlot = menusConfig.getInt("Keys.Next-Slot");
        previousSlot = menusConfig.getInt("Keys.Back-Slot");
    }

    public void allKeys(Player player) {
        val keys = KeyDAO.getKeys();
        if (keys.isEmpty())
            new NoKeysInventory(main).noKeys(player);

        else {
            val items = new ArrayList<ItemStack>();
            keys.forEach(key -> items.add(getIcon(player, key)));

            val scroller = new Scroller.ScrollerBuilder().withName(title).withSize(size * 9).withArrowsSlots(previousSlot, nextSlot).withItemsSlots(allowedSlots).withItems(items).build();
            scroller.open(player);

            LBUtils.playSound(player, openMenuSound);
        }

    }

    public ItemStack getIcon(Player player, Key key) {
        val vip = key.getVip();
        val time = vip.isEternal() ? vipController.getPermanentLore() : TimeFormat.format(vip.getTime());
        val deleteKey = player.hasPermission("lbvip.deletekey") ? menusConfig.getString("Key-Delete.Delete") : menusConfig.getString("Key-Delete.NoAccess");

        List<String> lore = menusConfig.getStringList("Keys.Items.Key.Lore");
        lore = lore.stream().map(l -> l.replace("{vip}", vip.getName()).replace("{key}", key.getId()).replace("{vip_time}", time).replace("{delete_key}", deleteKey)).collect(Collectors.toList());

        val name = menusConfig.getString("Keys.Items.Key.Name").replace("{key}", key.getId()).replace("{vip}", vip.getName());
        val url = menusConfig.getString("Keys.Items.Key.Icon.Skull-URL");
        val customSkull = menusConfig.getBoolean("Keys.Items.Key.Icon.Custom-Skull");

        val material = Material.valueOf(menusConfig.getString("Keys.Items.Key.Icon.Material").toUpperCase().split(":")[0]);
        val data = Integer.parseInt(menusConfig.getString("Keys.Items.Key.Icon.Material").split(":")[1]);
        val glow = Boolean.parseBoolean(menusConfig.getString("Keys.Items.Key.Icon.Material").split(":")[2]);

        val item = new ItemBuilder(material, 1, data).setName(name).setLore(lore).setGlow(glow).build();
        val head = new ItemBuilder(url).setName(name).setLore(lore).build();

        val icon = customSkull ? head : item;
        val nmsItem = CraftItemStack.asNMSCopy(icon);
        val itemCompound = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();

        itemCompound.set("VipKey-Icon", new NBTTagString(key.getId()));
        nmsItem.setTag(itemCompound);

        return CraftItemStack.asBukkitCopy(nmsItem);
    }

}
