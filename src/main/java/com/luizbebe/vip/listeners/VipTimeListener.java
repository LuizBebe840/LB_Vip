package com.luizbebe.vip.listeners;

import com.luizbebe.vip.Main;
import com.luizbebe.vip.controllers.UserController;
import com.luizbebe.vip.dao.UserDAO;
import com.luizbebe.vip.inventories.VipTimeInventory;
import com.luizbebe.vip.registry.EventRegistry;
import lombok.val;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

public class VipTimeListener extends EventRegistry {

    private final UserController userController;

    private final String menuTitle;

    public VipTimeListener(Main main) {
        super(main, main.getMenusFile().getConfig());

        userController = main.getUserController();

        menuTitle = config.getString("Vip-Time.Title").replace("&", "§");
    }

    @EventHandler
    void vipClick(InventoryClickEvent event) {
        val title = event.getView().getTitle();
        if (!title.equals(menuTitle))
            return;

        val item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR || !(item.hasItemMeta() && item.getItemMeta().hasDisplayName()))
            return;

        event.setCancelled(true);
        val player = (Player) event.getWhoClicked();
        val user = UserDAO.getUser(player.getUniqueId());
        val vip = user.getVip(item);
        if (vip == null)
            return;

        userController.changeVip(user, vip);
        new VipTimeInventory(main).myVips(player);
    }

}
