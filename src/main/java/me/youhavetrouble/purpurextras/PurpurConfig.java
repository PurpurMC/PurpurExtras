package me.youhavetrouble.purpurextras;

import me.youhavetrouble.purpurextras.listeners.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class PurpurConfig {

    private final Logger logger;
    private final FileConfiguration config;
    private final File configPath;

    private final PurpurExtras plugin = PurpurExtras.getInstance();
    public boolean dispenserBreakBlockPickaxe, dispenserBreakBlockShovel, dispenserBreakBlockHoe,
            dispenserBreakBlockShears, dispenserBreakBlockAxe, dispenserShearPumpkin, dispenserActivatesJukebox;

    protected PurpurConfig() {
        plugin.reloadConfig();
        logger = plugin.getLogger();
        config = plugin.getConfig();
        configPath = new File(plugin.getDataFolder(), "config.yml");

        handleBetterDispenser();
    }

    protected void saveConfig() {
        try {
            config.save(configPath);
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

    private void handleBetterDispenser() {
        this.dispenserBreakBlockPickaxe = getBoolean("settings.dispenser.break-blocks.pickaxe", false);
        this.dispenserBreakBlockShovel = getBoolean("settings.dispenser.break-blocks.shovel", false);
        this.dispenserBreakBlockHoe = getBoolean("settings.dispenser.break-blocks.hoe", false);
        this.dispenserBreakBlockAxe = getBoolean("settings.dispenser.break-blocks.axe", false);
        this.dispenserBreakBlockShears = getBoolean("settings.dispenser.break-blocks.shears", false);

        this.dispenserShearPumpkin = getBoolean("settings.dispenser.shears-shear-pumpkin", false);
        this.dispenserActivatesJukebox = getBoolean("settings.dispenser.puts-discs-in-jukebox", false);

        if (dispenserBreakBlockPickaxe
                || dispenserBreakBlockAxe
                || dispenserBreakBlockShovel
                || dispenserBreakBlockHoe
                || dispenserBreakBlockShears
                || dispenserShearPumpkin
                || dispenserActivatesJukebox
        ) {
            plugin.registerListener(DispenserListener.class);
        }
    }

}
