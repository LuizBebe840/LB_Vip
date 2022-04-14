package com.luizbebe.vip.utils;

import com.google.common.base.Strings;
import com.luizbebe.vip.Main;
import lombok.val;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class LBUtils {

    private static final Main main = Main.getPlugin(Main.class);
    private static final FileConfiguration config = main.getConfig();

    public static void sendMessage(CommandSender sender, String message) {
        message = message.replace("&", "§");

        if (sender instanceof ConsoleCommandSender) {
            val name = main.getName();
            sender.sendMessage("§b[" + name + "] " + message);
            return;

        }
        val prefix = config.getString("Prefix.Model").replace("&", "§");
        val prefixEnabled = config.getBoolean("Prefix.Enable");
        if (prefixEnabled) {
            if (message.isEmpty())
                sender.sendMessage(message);

            else
                sender.sendMessage(prefix + " " + message);

        } else
            sender.sendMessage(message);
    }

    public static void getLogger(String logType, String message) {
        val name = main.getName();
        Bukkit.getConsoleSender().sendMessage("[" + logType.toUpperCase() + "] §b[" + name + "] §f" + message);
    }

    public static void sendTitle(Player player, String titleLine1, String titleLine2) {
        titleLine1 = titleLine1.replace("&", "§").replace("{player}", player.getName());
        titleLine2 = titleLine2.replace("&", "§").replace("{player}", player.getName());

        player.sendTitle(titleLine1, titleLine2);
    }

    public static void sendActionBar(String text, Player player) {
        sendPacket(new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + text.replace("&", "§") + "\"}"), (byte) 2), player);
    }

    @SuppressWarnings("rawtypes")
    private static void sendPacket(Packet packet, Player player) {
        val craftPlayer = (CraftPlayer) player;
        (craftPlayer.getHandle()).playerConnection.sendPacket(packet);
    }

    public static void playSound(CommandSender sender, Sound sound) {
        if (!(sender instanceof Player))
            return;

        val player = (Player) sender;
        player.playSound(player.getLocation(), sound, 1.0F, 1.0F);
    }

    public static boolean hasPermission(CommandSender sender, String permission) {
        if (!sender.hasPermission(permission)) {
            playSound(sender, Sound.VILLAGER_NO);
            sendMessage(sender, "§cVocê não tem permissão para isto.");
            return false;
        }
        return true;
    }

    public static boolean playerIsOnline(CommandSender sender, Player target) {
        if (target == null) {
            sendMessage(sender, "§cEste jogador não está online!");
            playSound(sender, Sound.VILLAGER_NO);
            return false;
        }
        return true;
    }

    public static boolean isNumber(CommandSender sender, String number) {
        if (!NumberUtils.isNumber(number)) {
            sendMessage(sender, "§cDigite um número válido.");
            playSound(sender, Sound.VILLAGER_NO);
            return false;
        }
        return true;
    }

    public static void runCommand(Player player, String command) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", player.getName()));
    }

    public static void runCommandList(Player player, List<String> commands) {
        commands.forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", player.getName())));
    }

    public static String getProgressBar(double current, double max, int totalBars) {
        val symbol = config.getString("ProgressBar.Symbol");
        val completedColor = ChatColor.valueOf(config.getString("ProgressBar.Symbol-Colors.Completed"));
        val notCompleteColor = ChatColor.valueOf(config.getString("ProgressBar.Symbol-Colors.NotComplete"));

        val percent = (float) ((float) current / max);
        val progressBars = (int) (totalBars * percent);

        return Strings.repeat(completedColor + symbol, progressBars) + Strings.repeat(notCompleteColor + symbol, totalBars - progressBars);
    }

    public static void removeItemInHand(Player player) {
        val item = player.getItemInHand();
        if (item.getAmount() <= 1)
            player.setItemInHand(new ItemStack(Material.AIR));

        else
            item.setAmount(item.getAmount() - 1);
    }

    public static int getItemsAmount(Inventory inventory, ItemStack item) {
        int amount = 0;

        for (ItemStack items : inventory.all(item.getType()).values()) {
            if (items == item)
                continue;

            val quantity = items.getAmount();
            amount += quantity;
        }
        return amount;
    }

    public static void removeItemsFromInventory(Inventory inventory, ItemStack item, int amount) {
        for (Map.Entry<Integer, ? extends ItemStack> entry : inventory.all(item.getType()).entrySet()) {
            val items = entry.getValue();

            if (items.isSimilar(item)) {
                val itemsAmount = items.getAmount();

                if (itemsAmount <= amount) {
                    amount -= itemsAmount;
                    inventory.clear(entry.getKey());
                    return;

                }
                items.setAmount(itemsAmount - amount);
                amount = 0;
            }
            if (amount == 0)
                break;
        }

    }

    public static ItemStack getPlayerHead(String playerName) {
        val playerHead = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        val meta = (SkullMeta) playerHead.getItemMeta();

        meta.setOwner(playerName);
        playerHead.setItemMeta(meta);

        return playerHead;
    }

    public static String locationSerializer(Location location) {
        val world = location.getWorld().getName();

        val x = location.getX();
        val y = location.getY();
        val z = location.getZ();

        val yaw = location.getYaw();
        val pitch = location.getPitch();

        return world + ";" + x + ";" + y + ";" + z + ";" + yaw + ";" + pitch;
    }

    public static Location locationDeserializer(String location) {
        val split = location.split(";");

        val world = Bukkit.getWorld(split[0]);

        val x = Double.parseDouble(split[1]);
        val y = Double.parseDouble(split[2]);
        val z = Double.parseDouble(split[3]);

        val yaw = Float.parseFloat(split[4]);
        val pitch = Float.parseFloat(split[5]);

        return new Location(world, x, y, z, yaw, pitch);
    }

    public static boolean calculateChance(int max, int min) {
        val random = new Random();
        val chance = random.nextInt(max);

        return chance <= min;
    }

    public static int randomNumber(int max, int min) {
        val value1 = new Random().nextInt(max) + min;
        val value2 = new Random().nextInt(max) + min;

        return value1 + value2 / 2;
    }

}
