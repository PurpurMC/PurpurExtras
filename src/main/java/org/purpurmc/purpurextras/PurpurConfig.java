package org.purpurmc.purpurextras;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class PurpurConfig {

    private final Logger logger;
    private FileConfiguration config;
    private final File configPath;

    protected PurpurConfig() {
        PurpurExtras plugin = PurpurExtras.getInstance();
        plugin.reloadConfig();
        logger = plugin.getLogger();
        config = plugin.getConfig();
        configPath = new File(plugin.getDataFolder(), "config.yml");
        ConfigurationSection sec = config.getConfigurationSection("settings.gameplay-settings");
        if(sec != null) {
            logger.info("Running gameplay-settings config migration");
            sec.getValues(false).forEach((name, obj) -> {
                if(obj instanceof Boolean) {
                    config.set("settings." + name + ".enabled", obj);
                } else if(obj instanceof ConfigurationSection cs) {
                    config.set("settings." + name, cs);
                    config.set("settings." + name + ".enabled", false);
                }
            });
            config.set("settings.gameplay-settings", null);
        }
    }

    protected void saveConfig() {
        try {
            config.save(configPath);
            config = PurpurExtras.getInstance().getConfig();
        } catch (IOException e) {
            logger.severe("Failed to save configuration file! - " + e.getLocalizedMessage());
        }
    }

    public boolean getBoolean(String path, boolean def) {
        if (config.isSet(path))
            return config.getBoolean(path, def);
        config.set(path, def);
        return def;
    }

    public String getString(String path, String def) {
        if (config.isSet(path))
            return config.getString(path, def);
        config.set(path, def);
        return def;
    }

    public double getDouble(String path, double def) {
        if (config.isSet(path))
            return config.getDouble(path, def);
        config.set(path, def);
        return def;
    }

    public int getInt(String path, int def) {
        if (config.isSet(path))
            return config.getInt(path, def);
        config.set(path, def);
        return def;
    }

    /**
     * @param defKV Default key-value map
     */
    public ConfigurationSection getConfigSection(String path, Map<String, Object> defKV) {
        if (config.isConfigurationSection(path))
            return config.getConfigurationSection(path);
        return config.createSection(path, defKV);
    }

    /**
     * @return List of strings or empty list if list doesn't exist in configuration file
     */
    public List<String> getList(String path, List<String> def) {
        if (config.isSet(path))
            return config.getStringList(path);
        config.set(path, def);
        return def;
    }

}
