package com.luizbebe.vip.controllers;

import com.luizbebe.vip.Main;
import com.luizbebe.vip.data.user.User;
import com.luizbebe.vip.data.vip.Vip;
import com.luizbebe.vip.hook.Group;
import lombok.val;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.stream.Collectors;

public class UserController {

    private final FileConfiguration config;

    private final Group group;

    private final String expiredVipMessage;
    private final Sound expiredVipSound;

    public UserController(Main main) {
        config = main.getConfig();

        group = main.getGroup();

        expiredVipMessage = config.getString("Messages.Expired-Vip").replace("&", "§");
        expiredVipSound = Sound.valueOf(config.getString("Sounds.Expired-Vip").toUpperCase());
    }

    public void updateVIPs(User user) {
        try {
            if (user.getVips().isEmpty())
                return;

            user.getVips().stream().filter(vips -> !vips.isEternal() && vips.getId().equalsIgnoreCase(user.getCurrentVip().getId())).forEach(vips -> {
                if (vips.getTime() > 0)
                    vips.setTime(vips.getTime() - 1000L);

                else {
                    user.sendMessage(expiredVipMessage.replace("{name}", vips.getName()));
                    user.playSound(expiredVipSound);

                    user.removeVip(group, vips, config.getString("Vip-Settings.Default-Group"));
                }

            });
        } catch (Exception ignored) {
        }
    }

    public synchronized void changeVip(User user, Vip vip) {
        val currentVip = user.getCurrentVip();
        if (currentVip.getId().equalsIgnoreCase(vip.getId())) {
            user.sendMessage(config.getString("Messages.Change-Vip.InUse"));
            user.playSound(Sound.valueOf(config.getString("Sounds.Change-Vip.InUse").toUpperCase()));
            return;

        }
        List<String> messageList = config.getStringList("Messages.Change-Vip.Successfully");
        messageList = messageList.stream().map(m -> m.replace("{vip}", currentVip.getName()).replace("{vip_exchanged}", vip.getName())).collect(Collectors.toList());

        messageList.forEach(user::sendMessage);
        user.playSound(Sound.valueOf(config.getString("Sounds.Change-Vip.Successfully").toUpperCase()));

        group.setGroup(user.getBukkitPlayer(), vip.getGroup());
        user.setCurrentVip(vip);
    }

}
