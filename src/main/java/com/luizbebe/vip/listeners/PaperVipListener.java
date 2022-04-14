package com.luizbebe.vip.listeners;

import com.luizbebe.vip.Main;
import com.luizbebe.vip.controllers.PaperVipController;
import com.luizbebe.vip.data.vip.Vip;
import com.luizbebe.vip.registry.EventRegistry;
import com.luizbebe.vip.utils.ObjectSerializer;
import lombok.val;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PaperVipListener extends EventRegistry {

    private final PaperVipController paperVipController;

    public PaperVipListener(Main main) {
        super(main, main.getConfig());

        paperVipController = main.getPaperVipController();
    }

    @EventHandler
    void useVipPaper(PlayerInteractEvent event) {
        val player = event.getPlayer();
        val action = event.getAction();
        val item = player.getItemInHand();

        if (!(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK))
            return;

        if (item == null || item.getType() == Material.AIR)
            return;

        if (!paperVipController.hasTag(item))
            return;

        event.setCancelled(true);
        val vip = (Vip) ObjectSerializer.deserializeObject(paperVipController.getVipId(item));
        if (vip == null)
            return;

        paperVipController.usePaper(player, vip);
    }

}
