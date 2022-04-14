package com.luizbebe.vip.files;

import com.luizbebe.vip.Main;
import com.luizbebe.vip.utils.MultipleFile;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
public class PaperVipFile {

    private final MultipleFile file;
    private final FileConfiguration config;

    public PaperVipFile(Main main) {
        file = new MultipleFile(main, null, "papervip.yml");
        config = file.getConfig();
    }

}
