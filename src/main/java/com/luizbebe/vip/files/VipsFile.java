package com.luizbebe.vip.files;

import com.luizbebe.vip.Main;
import com.luizbebe.vip.setup.VipsSetup;
import com.luizbebe.vip.utils.MultipleFile;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
public class VipsFile {

    private final MultipleFile file;
    private final FileConfiguration config;

    public VipsFile(Main main) {
        file = new MultipleFile(main, null, "vips.yml");
        config = file.getConfig();

        new VipsSetup(config);
    }

}
