package com.luizbebe.vip.commands;

import com.luizbebe.vip.Main;
import com.luizbebe.vip.controllers.PaperVipController;
import com.luizbebe.vip.controllers.VipController;
import com.luizbebe.vip.dao.VipDAO;
import com.luizbebe.vip.data.vip.Vip;
import com.luizbebe.vip.registry.CommandRegistry;
import com.luizbebe.vip.utils.LBUtils;
import com.luizbebe.vip.utils.TimeFormat;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class PaperVipCommand extends CommandRegistry {

    private final VipController vipController;
    private final PaperVipController paperVipController;

    public PaperVipCommand(Main main) {
        super(main, main.getConfig(), "papervip");

        vipController = main.getVipController();
        paperVipController = main.getPaperVipController();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!LBUtils.hasPermission(sender, "lbvip.papervip.give"))
            return true;

        if (args.length != 4) {
            LBUtils.sendMessage(sender, "§cUse: /papervip <player> <vip> <type> <time/eternal>");
            sender.sendMessage("");
            sender.sendMessage(" §c§lAVAILABLE TYPES");
            sender.sendMessage("");
            sender.sendMessage(" §c➤ §7Days");
            sender.sendMessage(" §c➤ §7Hours");
            sender.sendMessage(" §c➤ §7Minutes");
            sender.sendMessage(" §c➤ §7Seconds");
            sender.sendMessage("");

            LBUtils.playSound(sender, Sound.NOTE_PLING);
            return true;

        }
        val target = Bukkit.getPlayer(args[0]);
        if (!LBUtils.playerIsOnline(sender, target))
            return true;

        val vip = VipDAO.getVip(args[1]);
        if (vip == null) {
            sender.sendMessage("");
            sender.sendMessage(" §c§lVIP's AVAILABLE");
            sender.sendMessage("");
            Arrays.stream(VipDAO.getVips().toArray(new Vip[0])).map(vips -> " §c➤ §7" + vips.getId()).forEach(sender::sendMessage);
            sender.sendMessage("");

            LBUtils.playSound(sender, Sound.NOTE_PLING);
            return true;

        }
        val timeType = vipController.getTimeType(args[2]);
        val time = vipController.isEternal(args[3]) ? -1 : timeType.toMillis(Integer.parseInt(args[3]));
        val formattedTime = time == -1 ? vipController.getPermanentLore() : TimeFormat.format(time);

        if (time != -1 && !LBUtils.isNumber(sender, args[3]))
            return true;

        if (time != -1 && time <= 0) {
            LBUtils.sendMessage(sender, "§cTime must be greater than 0!");
            LBUtils.playSound(sender, Sound.NOTE_PLING);
            return true;

        }
        val message = config.getString("Messages.PaperVip.Give").replace("{player}", target.getName()).replace("{vip}", vip.getName()).replace("{time}", formattedTime);

        LBUtils.sendMessage(sender, message);
        LBUtils.playSound(sender, Sound.valueOf(config.getString("Sounds.PaperVip.Give").toUpperCase()));

        vip.setTime(time);
        target.getInventory().addItem(paperVipController.getPaperItem(vip));
        return false;
    }

}
