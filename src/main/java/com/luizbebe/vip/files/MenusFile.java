package com.luizbebe.vip.files;

import com.luizbebe.vip.Main;
import com.luizbebe.vip.utils.MultipleFile;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
public class MenusFile {

    private final MultipleFile file;
    private final FileConfiguration config;

    public MenusFile(Main main) {
        file = new MultipleFile(main, null, "menus.yml");
        config = file.getConfig();
    }

}
