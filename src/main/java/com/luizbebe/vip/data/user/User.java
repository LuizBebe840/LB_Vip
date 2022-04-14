package com.luizbebe.vip.data.user;

import com.luizbebe.vip.data.vip.Vip;
import com.luizbebe.vip.hook.Group;
import com.luizbebe.vip.utils.LBUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class User {

    private UUID uuid;
    private Vip currentVip;
    private List<Vip> vips;

    public User(UUID uuid) {
        this.uuid = uuid;
        currentVip = null;
        vips = new ArrayList<>();
    }

    public Player getBukkitPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public Vip getVip(String vipId) {
        return vips.stream().filter(vips -> vips.getId().equalsIgnoreCase(vipId)).findFirst().orElse(null);
    }

    public Vip getVip(ItemStack item) {
        return vips.stream().filter(vips -> item.getItemMeta().getDisplayName().equalsIgnoreCase(vips.getSettings().getName())).findFirst().orElse(null);
    }

    public void removeVip(Group group, Vip vip, String defaultGroup) {
        val nextVip = (vips.size() - 1) == 0 ? null : vips.get(vips.size() - 1);
        if (nextVip == null)
            group.setGroup(getBukkitPlayer(), defaultGroup);

        else
            group.setGroup(getBukkitPlayer(), nextVip.getGroup());

        setCurrentVip(nextVip);
        vips.remove(vip);
    }

    public boolean isCurrentVip(Vip vip) {
        return currentVip.getId().equalsIgnoreCase(vip.getId());
    }

    public void sendMessage(String message) {
        LBUtils.sendMessage(getBukkitPlayer(), message);
    }

    public void playSound(Sound sound) {
        LBUtils.playSound(getBukkitPlayer(), sound);
    }

}
