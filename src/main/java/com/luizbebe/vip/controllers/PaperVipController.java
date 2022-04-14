package com.luizbebe.vip.controllers;

import com.luizbebe.vip.Main;
import com.luizbebe.vip.dao.UserDAO;
import com.luizbebe.vip.data.vip.Vip;
import com.luizbebe.vip.utils.ItemBuilder;
import com.luizbebe.vip.utils.LBUtils;
import com.luizbebe.vip.utils.ObjectSerializer;
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

import java.util.List;
import java.util.stream.Collectors;

public class PaperVipController {

    private final FileConfiguration config;

    private final VipController vipController;

    private final String name;
    private final String url;
    private final List<String> lore;

    private final Material material;
    private final int data;
    private final boolean glow;
    private final boolean customSkull;

    public PaperVipController(Main main) {
        val paperConfig = main.getPaperVipFile().getConfig();

        config = main.getConfig();

        vipController = main.getVipController();

        name = paperConfig.getString("PaperVip-Item.Name");
        url = paperConfig.getString("PaperVip-Item.Skull-URL");
        lore = paperConfig.getStringList("PaperVip-Item.Lore");

        material = Material.valueOf(paperConfig.getString("PaperVip-Item.Material").toUpperCase().split(":")[0]);
        data = Integer.parseInt(paperConfig.getString("PaperVip-Item.Material").split(":")[1]);
        glow = Boolean.parseBoolean(paperConfig.getString("PaperVip-Item.Material").split(":")[2]);
        customSkull = paperConfig.getBoolean("PaperVip-Item.Custom-Skull");
    }

    public ItemStack getPaperItem(Vip vip) {
        val time = vip.isEternal() ? vipController.getPermanentLore() : TimeFormat.format(vip.getTime());

        List<String> lore = this.lore;
        lore = lore.stream().map(l -> l.replace("{vip}", vip.getName()).replace("{vip_time}", time)).collect(Collectors.toList());

        val item = new ItemBuilder(material, 1, data).setName(name.replace("{vip}", vip.getName())).setLore(lore).setGlow(glow).build();
        val head = new ItemBuilder(url).setName(name.replace("{vip}", vip.getName())).setLore(lore).build();

        val itemResult = customSkull ? head : item;
        val nmsItem = CraftItemStack.asNMSCopy(itemResult);
        val itemCompound = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();

        itemCompound.set("PaperVip-VipId", new NBTTagString(ObjectSerializer.serializeObject(vip)));
        nmsItem.setTag(itemCompound);

        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    public boolean hasTag(ItemStack item) {
        val nmsItem = CraftItemStack.asNMSCopy(item);
        val itemCompound = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();

        return itemCompound.hasKey("PaperVip-VipId");
    }

    public String getVipId(ItemStack item) {
        val nmsItem = CraftItemStack.asNMSCopy(item);
        val itemCompound = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();

        return itemCompound.getString("PaperVip-VipId");
    }

    public synchronized void usePaper(Player player, Vip vip) {
        val time = vip.isEternal() ? vipController.getPermanentLore() : TimeFormat.format(vip.getTime());
        val user = UserDAO.getUser(player.getUniqueId());
        val targetVip = user.getVip(vip.getId());

        if (targetVip != null) {
            if (targetVip.isEternal()) {
                LBUtils.sendMessage(player, config.getString("Messages.PaperVip.EternalVip"));
                LBUtils.playSound(player, Sound.valueOf(config.getString("Sounds.PaperVip.EternalVip").toUpperCase()));
                return;

            }
            vipController.addAlreadyVip(player, targetVip, vip.getTime());

        } else
            vipController.addVip(player, vip, vip.getTime());

        val message = config.getString("Messages.PaperVip.Active").replace("{vip}", vip.getName()).replace("{time}", time);

        LBUtils.sendMessage(player, message);
        LBUtils.playSound(player, Sound.valueOf(config.getString("Sounds.PaperVip.Active").toUpperCase()));
        LBUtils.removeItemInHand(player);
    }

}
