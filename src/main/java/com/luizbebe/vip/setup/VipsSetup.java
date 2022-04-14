package com.luizbebe.vip.setup;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.luizbebe.vip.dao.VipDAO;
import com.luizbebe.vip.data.vip.Vip;
import com.luizbebe.vip.data.vip.VipSettings;
import lombok.val;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.stream.Collectors;

public class VipsSetup {

    private final FileConfiguration config;

    public VipsSetup(FileConfiguration config) {
        this.config = config;

        setupVips();
    }

    public void setupVips() {
        for (val path : config.getConfigurationSection("Vips").getKeys(false)) {
            val key = config.getConfigurationSection("Vips." + path);

            val group = key.getString("Group");
            val name = key.getString("Name").replace("&", "§");
            val commands = key.getStringList("Commands");

            List<String> iconLore = key.getStringList("Icon.Lore");
            iconLore = iconLore.stream().map(l -> l.replace("{name}", name)).collect(Collectors.toList());

            val iconName = key.getString("Icon.Name").replace("&", "§").replace("{name}", name);
            val iconURL = key.getString("Icon.Skull-URL");
            val iconMaterial = Material.valueOf(key.getString("Icon.Material").toUpperCase().split(":")[0]);
            val iconData = Integer.parseInt(key.getString("Icon.Material").split(":")[1]);
            val iconGlow = Boolean.parseBoolean(key.getString("Icon.Material").split(":")[2]);
            val customSkull = key.getBoolean("Icon.Custom-Skull");

            val particleType = EnumWrappers.Particle.valueOf(key.getString("Animation.Particle-Type").toUpperCase());
            val periodBasis = key.getInt("Animation.Period-Basis");
            val quantity = key.getInt("Animation.Quantity");
            val degrees = key.getDouble("Animation.Settings.Degrees");
            val x = key.getDouble("Animation.Settings.X");
            val y = key.getDouble("Animation.Settings.Y");
            val z = key.getDouble("Animation.Settings.Z");
            val enabledAnimation = key.getBoolean("Animation.Enable");

            val titleLine1 = key.getString("Title.Line1").replace("{name}", name);
            val titleLine2 = key.getString("Title.Line2").replace("{name}", name);

            val vipSettings = new VipSettings(iconName, iconURL, titleLine1, titleLine2, iconLore, particleType, iconMaterial, iconData, periodBasis, quantity, degrees, x, y, z, enabledAnimation, customSkull, iconGlow);
            val vips = new Vip(vipSettings, path, group, name, "", commands, 0);

            VipDAO.addVip(vips, vipSettings);
        }

    }

}
