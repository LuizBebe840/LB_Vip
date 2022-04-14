package com.luizbebe.vip.commands;

import com.luizbebe.vip.Main;
import com.luizbebe.vip.controllers.VipController;
import com.luizbebe.vip.dao.KeyDAO;
import com.luizbebe.vip.dao.VipDAO;
import com.luizbebe.vip.data.vip.Vip;
import com.luizbebe.vip.managers.KeyManager;
import com.luizbebe.vip.registry.CommandRegistry;
import com.luizbebe.vip.utils.LBUtils;
import com.luizbebe.vip.utils.TimeFormat;
import lombok.val;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class GenerateKeyCommand extends CommandRegistry {

    private final KeyManager keyManager;

    private final VipController vipController;

    public GenerateKeyCommand(Main main) {
        super(main, main.getConfig(), "generatekey");

        keyManager = main.getKeyManager();

        vipController = main.getVipController();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!LBUtils.hasPermission(sender, "lbvip.generatekey"))
            return true;

        if (args.length != 4) {
            LBUtils.sendMessage(sender, "§cUse: /generatekey <vip> <type> <time/eternal> <key/random>");
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
        val vip = VipDAO.getVip(args[0]);
        if (vip == null) {
            sender.sendMessage("");
            sender.sendMessage(" §c§lVIP's AVAILABLE");
            sender.sendMessage("");
            Arrays.stream(VipDAO.getVips().toArray(new Vip[0])).map(vips -> " §c➤ §7" + vips.getId()).forEach(sender::sendMessage);
            sender.sendMessage("");

            LBUtils.playSound(sender, Sound.NOTE_PLING);
            return true;

        }
        val timeType = vipController.getTimeType(args[1]);
        val time = vipController.isEternal(args[2]) ? -1 : timeType.toMillis(Integer.parseInt(args[2]));
        val formattedTime = time == -1 ? vipController.getPermanentLore() : TimeFormat.format(time);

        if (time != -1 && !LBUtils.isNumber(sender, args[2]))
            return true;

        if (time != -1 && time <= 0) {
            LBUtils.sendMessage(sender, "§cTime must be greater than 0!");
            LBUtils.playSound(sender, Sound.NOTE_PLING);
            return true;

        }
        String keyId;
        if (args[3].equalsIgnoreCase("random"))
            keyId = RandomStringUtils.random(LBUtils.randomNumber(10, 3), true, true).toUpperCase();

        else {
            val targetKey = KeyDAO.getKey(args[3].toUpperCase());
            if (targetKey != null) {
                LBUtils.sendMessage(sender, "§cThis key already exists!");
                LBUtils.playSound(sender, Sound.NOTE_PLING);
                return true;

            }
            keyId = args[3].toUpperCase();
        }
        if (keyId.length() > 60) {
            LBUtils.sendMessage(sender, "§cThe key cannot be longer than 60 characters!");
            LBUtils.playSound(sender, Sound.NOTE_PLING);
            return true;

        }
        val message = config.getString("Messages.Generate-Key").replace("{key}", keyId).replace("{time}", formattedTime);

        LBUtils.sendMessage(sender, message);
        LBUtils.playSound(sender, Sound.valueOf(config.getString("Sounds.Generate-Key").toUpperCase()));

        vip.setTime(time);
        keyManager.createKey(vip, keyId);
        return false;
    }

}
