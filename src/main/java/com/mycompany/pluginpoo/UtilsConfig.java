package com.mycompany.pluginpoo;

import org.bukkit.configuration.file.FileConfiguration;

public class UtilsConfig {
    public static String obtener(FileConfiguration cfg, String path, String defecto) {
        if (!cfg.contains(path)) {
            cfg.set(path, defecto);
        }
        return cfg.getString(path);
    }
}
