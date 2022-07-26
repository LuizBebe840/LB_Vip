package com.luizbebe.vip.controllers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.luizbebe.vip.Main;
import com.luizbebe.vip.dao.UserDAO;
import com.luizbebe.vip.data.vip.Vip;
import com.luizbebe.vip.hook.Group;
import com.luizbebe.vip.utils.LBUtils;
import com.luizbebe.vip.utils.TimeFormat;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class VipController {

    protected Main main;

    private final FileConfiguration config;
    private final FileConfiguration vipsConfig;

    private final SimpleDateFormat dateFormat;

    private final Group group;

    private final String alreadyVipPlayer;
    private final String addedVipPlayer;

    @Getter
    private final String permanentLore;

    public VipController(Main main) {
        this.main = main;

        config = main.getConfig();
        vipsConfig = main.getVipsFile().getConfig();

        dateFormat = new SimpleDateFormat(config.getString("Vip-Settings.Date-Format"));

        group = main.getGroup();

        alreadyVipPlayer = config.getString("Messages.Add-Vip.Already-Vip.Player");
        addedVipPlayer = config.getString("Messages.Add-Vip.Player");

        permanentLore = vipsConfig.getString("Status.Permanent-Lore").replace("&", "§");
    }

    public void sendParticle(Player player, Vip vip) {
        val settings = vip.getSettings();
        val protocol = ProtocolLibrary.getProtocolManager();

        new BukkitRunnable() {
            double degrees = 0;
            int periodBasis = 0;

            @SneakyThrows
            @Override
            public void run() {
                if (periodBasis >= settings.getPeriodBasis())
                    cancel();

                else {
                    val x = Math.cos(degrees) * settings.getX();
                    val y = settings.getY();
                    val z = Math.sin(degrees) * settings.getZ();
                    val location = player.getLocation();
                    val packet = protocol.createPacket(PacketType.Play.Server.WORLD_PARTICLES);

                    packet.getModifier().writeDefaults();
                    packet.getParticles().write(0, settings.getParticleType());
                    packet.getFloat().write(0, (float) (location.getX() + x)).write(1, (float) (location.getY() + y)).write(2, (float) (location.getZ() + z));

                    for (val players : Bukkit.getOnlinePlayers())
                        for (int index = 0; index < settings.getQuantity(); index++)
                            protocol.sendServerPacket(players, packet);
                }
                degrees += settings.getDegrees();
                periodBasis++;
            }
        }.runTaskTimerAsynchronously(main, 0L, 1L);
    }

    public void addAlreadyVip(OfflinePlayer player, Vip vip, long time) {
        val newTime = vip.getTime() + time;
        if (player.isOnline()) {
            LBUtils.sendMessage(player.getPlayer(), alreadyVipPlayer.replace("<nl>", "\n").replace("{time}", TimeFormat.format(time)).replace("{name}", vip.getName()).replace("{remaining_time}", TimeFormat.format(newTime)));
            LBUtils.playSound(player.getPlayer(), Sound.valueOf(config.getString("Sounds.Add-Vip.Already-Vip.Player").toUpperCase()));

            if (vip.getSettings().isEnabledAnimation())
                sendParticle(player.getPlayer(), vip);
        }
        vip.setTime(newTime);
        LBUtils.runCommandList(player.getPlayer(), vip.getCommands());
    }

    public void addVip(OfflinePlayer player, Vip vip, long time) {
        val user = UserDAO.getUser(player.getUniqueId());
        val settings = vip.getSettings();

        vip.setTime(time);
        vip.setDate(dateFormat.format(Calendar.getInstance().getTime()));
        user.setCurrentVip(vip);
        user.getVips().add(vip);

        val formattedTime = vip.isEternal() ? permanentLore : TimeFormat.format(time);
        Bukkit.getOnlinePlayers().forEach(players -> {
            LBUtils.sendTitle(players, settings.getTitleLine1().replace("{group}", group.getGroup(player)).replace("{player}", player.getName()).replace("{name}", vip.getName()).replace("{time}", formattedTime), settings.getTitleLine2().replace("{group}", group.getGroup(player)).replace("{player}", player.getName()).replace("{name}", vip.getName()).replace("{time}", formattedTime));
            LBUtils.playSound(players, Sound.valueOf(config.getString("Sounds.Add-Vip.Online-Players").toUpperCase()));

        });
        if (player.isOnline()) {
            if (settings.isEnabledAnimation())
                sendParticle(player.getPlayer(), vip);

            group.setGroup(player, vip.getGroup());

            LBUtils.sendMessage(player.getPlayer(), addedVipPlayer.replace("{name}", vip.getName()).replace("{time}", formattedTime));
            LBUtils.playSound(player.getPlayer(), Sound.valueOf(config.getString("Sounds.Add-Vip.Player").toUpperCase()));
            LBUtils.runCommandList(player.getPlayer(), vip.getCommands());
        }

    }

    public TimeUnit getTimeType(String arg) {
        switch (arg.toLowerCase()) {
            case "hours":
            case "hour":
            case "horas":
            case "hora":
                return TimeUnit.HOURS;

            case "minutes":
            case "minutos":
            case "minute":
            case "minuto":
                return TimeUnit.MINUTES;

            case "seconds":
            case "second":
            case "segundos":
            case "segundo":
                return TimeUnit.SECONDS;

            default:
                return TimeUnit.DAYS;
        }

    }

    public boolean isEternal(String time) {
        return time.equalsIgnoreCase("eternal") || time.equalsIgnoreCase("eterno") || Integer.parseInt(time) == -1;
    }

}
