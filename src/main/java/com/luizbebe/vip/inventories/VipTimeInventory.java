package com.luizbebe.vip.inventories;

import com.luizbebe.vip.Main;
import com.luizbebe.vip.controllers.VipController;
import com.luizbebe.vip.dao.UserDAO;
import com.luizbebe.vip.data.user.User;
import com.luizbebe.vip.data.vip.Vip;
import com.luizbebe.vip.utils.ItemBuilder;
import com.luizbebe.vip.utils.LBUtils;
import com.luizbebe.vip.utils.Scroller.ScrollerBuilder;
import com.luizbebe.vip.utils.TimeFormat;
import lombok.val;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VipTimeInventory {

    protected Main main;

    private final VipController vipController;

    private final FileConfiguration config;
    private final FileConfiguration menusConfig;
    private final FileConfiguration vipsConfig;

    private final Sound openMenuSound;

    private final List<Integer> allowedSlots;

    private final String title;
    private final String using;
    private final String notUsing;

    private final int size;
    private final int nextSlot;
    private final int previousSlot;

    public VipTimeInventory(Main main) {
        this.main = main;

        vipController = main.getVipController();

        config = main.getConfig();
        menusConfig = main.getMenusFile().getConfig();
        vipsConfig = main.getVipsFile().getConfig();

        openMenuSound = Sound.valueOf(config.getString("Sounds.Open-Menu").toUpperCase());

        allowedSlots = menusConfig.getIntegerList("Vip-Time.Allowed-Slots");

        title = menusConfig.getString("Vip-Time.Title").replace("&", "§");
        using = vipsConfig.getString("Status.Using").replace("&", "§");
        notUsing = vipsConfig.getString("Status.NotUsing").replace("&", "§");

        size = menusConfig.getInt("Vip-Time.Size");
        nextSlot = menusConfig.getInt("Vip-Time.Next-Slot");
        previousSlot = menusConfig.getInt("Vip-Time.Back-Slot");
    }

    public void myVips(Player player) {
        val user = UserDAO.getUser(player.getUniqueId());
        if (user.getVips().isEmpty())
            new NoVipInventory(main).noVip(player);

        else {
            val items = new ArrayList<ItemStack>();
            user.getVips().forEach(vips -> items.add(getIcon(user, vips)));

            val scroller = new ScrollerBuilder().withName(title).withSize(size * 9).withArrowsSlots(previousSlot, nextSlot).withItemsSlots(allowedSlots).withItems(items).build();
            scroller.open(player);

            LBUtils.playSound(player, openMenuSound);
        }

    }

    public ItemStack getIcon(User user, Vip vip) {
        val settings = vip.getSettings();

        val time = vip.isEternal() ? vipController.getPermanentLore() : TimeFormat.format(vip.getTime());
        val status = user.isCurrentVip(vip) ? using : notUsing;

        List<String> lore = settings.getLore();
        lore = lore.stream().map(l -> l.replace("{date}", vip.getDate()).replace("{remaining_time}", time).replace("{status}", status)).collect(Collectors.toList());

        val item = new ItemBuilder(settings.getMaterial(), 1, settings.getData()).setName(settings.getName()).setLore(lore).setGlow(settings.isGlow()).build();
        val head = new ItemBuilder(settings.getSkullURL()).setName(settings.getName()).setLore(lore).build();

        return settings.isCustomSkull() ? head : item;
    }

}
