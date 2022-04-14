package com.luizbebe.vip.commands;

import com.luizbebe.vip.Main;
import com.luizbebe.vip.controllers.VipController;
import com.luizbebe.vip.dao.UserDAO;
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

public class GiveVipCommand extends CommandRegistry {

    private final VipController vipController;

    private final String alreadyVipStaff;
    private final String addedVipStaff;

    public GiveVipCommand(Main main) {
        super(main, main.getConfig(), "givevip");

        vipController = main.getVipController();

        alreadyVipStaff = config.getString("Messages.Add-Vip.Already-Vip.Staff");
        addedVipStaff = config.getString("Messages.Add-Vip.Staff");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!LBUtils.hasPermission(sender, "lbvip.givevip"))
            return true;

        if (args.length != 4) {
            sender.sendMessage("");
            LBUtils.sendMessage(sender, "§cUse: /givevip <player> <vip> <type> <time/eternal>");
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
        val user = UserDAO.getUser(target.getUniqueId());
        val targetVip = user.getVip(vip.getId());
        if (targetVip != null) {
            if (targetVip.isEternal()) {
                LBUtils.sendMessage(sender, "§cThis VIP is eternal!");
                LBUtils.playSound(sender, Sound.NOTE_PLING);
                return true;

            }
            val timeType = vipController.getTimeType(args[2]);
            if (!LBUtils.isNumber(sender, args[3]))
                return true;

            val time = Integer.parseInt(args[3]);
            if (time <= 0) {
                LBUtils.sendMessage(sender, "§cTime must be greater than 0!");
                LBUtils.playSound(sender, Sound.NOTE_PLING);
                return true;

            }
            vipController.addAlreadyVip(target, targetVip, timeType.toMillis(time));

            LBUtils.sendMessage(sender, alreadyVipStaff.replace("<nl>", "\n").replace("{time}", "" + time).replace("{player}", target.getName()).replace("{name}", vip.getName()).replace("{remaining_time}", TimeFormat.format(targetVip.getTime())));
            LBUtils.playSound(sender, Sound.valueOf(config.getString("Sounds.Add-Vip.Already-Vip.Staff").toUpperCase()));
            return true;

        }
        val timeType = vipController.getTimeType(args[2]);
        val time = vipController.isEternal(args[3]) ? -1 : timeType.toMillis(Integer.parseInt(args[3]));
        val formattedTime = vipController.isEternal(args[3]) ? vipController.getPermanentLore() : TimeFormat.format(time);

        if (time != -1 && !LBUtils.isNumber(sender, args[3]))
            return true;

        if (time != -1 && time <= 0) {
            LBUtils.sendMessage(sender, "§cTime must be greater than 0!");
            LBUtils.playSound(sender, Sound.NOTE_PLING);
            return true;

        }
        LBUtils.sendMessage(sender, addedVipStaff.replace("{player}", target.getName()).replace("{name}", vip.getName()).replace("{time}", formattedTime));
        LBUtils.playSound(sender, Sound.valueOf(config.getString("Sounds.Add-Vip.Staff").toUpperCase()));

        vipController.addVip(target, vip, time);
        return false;
    }

}
