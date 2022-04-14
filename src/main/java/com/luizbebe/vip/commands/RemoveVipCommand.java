package com.luizbebe.vip.commands;

import com.luizbebe.vip.Main;
import com.luizbebe.vip.dao.UserDAO;
import com.luizbebe.vip.hook.Group;
import com.luizbebe.vip.registry.CommandRegistry;
import com.luizbebe.vip.utils.LBUtils;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class RemoveVipCommand extends CommandRegistry {

    private final Group group;

    private final String removeVipStaff;
    private final String removeVipPlayer;

    public RemoveVipCommand(Main main) {
        super(main, main.getConfig(), "removevip");

        group = main.getGroup();

        removeVipStaff = config.getString("Messages.Remove-Vip.Successfully.Staff");
        removeVipPlayer = config.getString("Messages.Remove-Vip.Successfully.Player");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!LBUtils.hasPermission(sender, "lbvip.removevip"))
            return true;

        if (args.length != 2) {
            LBUtils.sendMessage(sender, "§cUse: /removevip <player> <vip>");
            LBUtils.playSound(sender, Sound.NOTE_PLING);
            return true;

        }
        val target = Bukkit.getPlayer(args[0]);
        if (!LBUtils.playerIsOnline(sender, target))
            return true;

        val user = UserDAO.getUser(target.getUniqueId());
        val vip = user.getVip(args[1]);
        if (vip == null) {
            LBUtils.sendMessage(sender, config.getString("Messages.Remove-Vip.NoVip"));
            LBUtils.playSound(sender, Sound.valueOf(config.getString("Sounds.Remove-Vip.NoVip").toUpperCase()));
            return true;

        }
        LBUtils.sendMessage(sender, removeVipStaff.replace("{player}", target.getName()).replace("{name}", vip.getName()));
        LBUtils.sendMessage(target, removeVipPlayer.replace("{player}", sender.getName()).replace("{name}", vip.getName()));
        LBUtils.playSound(sender, Sound.valueOf(config.getString("Sounds.Remove-Vip.Successfully.Staff").toUpperCase()));
        LBUtils.playSound(target, Sound.valueOf(config.getString("Sounds.Remove-Vip.Successfully.Staff").toUpperCase()));

        user.removeVip(group, vip, config.getString("Vip-Settings.Default-Group"));
        return false;
    }

}
