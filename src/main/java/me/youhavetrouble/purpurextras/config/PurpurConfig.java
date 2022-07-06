package me.youhavetrouble.purpurextras.config;

import me.youhavetrouble.purpurextras.PurpurExtras;
import me.youhavetrouble.purpurextras.listeners.*;
import me.youhavetrouble.purpurextras.recipes.ToolUpgradesRecipes;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.HandlerList;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class PurpurConfig {

    Logger logger;
    FileConfiguration config;
    File configPath;

    private final PurpurExtras plugin = PurpurExtras.getInstance();
    public boolean dispenserBreakBlockPickaxe, dispenserBreakBlockShovel, dispenserBreakBlockHoe,
            dispenserBreakBlockShears, dispenserBreakBlockAxe, dispenserShearPumpkin, dispenserActivatesJukebox;
    public final boolean upgradeWoodToStoneTools;
    public final boolean upgradeStoneToIronTools;
    public final boolean upgradeIronToDiamondTools;


    public final HashMap<String, String> lightningTransformEntities = new HashMap<>();
    public final double furnaceBurnTimeMultiplier;

    public PurpurConfig() {
        plugin.reloadConfig();
        logger = plugin.getLogger();
        config = plugin.getConfig();
        configPath = new File(plugin.getDataFolder(), "config.yml");

        // Make sure that no listeners are registered from the plugin
        HandlerList.unregisterAll(plugin);

        enableFeature(RespawnAnchorNeedsChargeListener.class, !getBoolean("settings.gameplay-settings.respawn-anchor-needs-charges", true));

        enableFeature(EscapeCommandSlashListener.class, getBoolean("settings.chat.escape-commands", false));

        enableFeature(GrindstoneEnchantsBooksListener.class, getBoolean("settings.grindstone.gives-enchants-back", false));

        enableFeature(ForceNametaggedForRidingListener.class, getBoolean("settings.rideables.mob-needs-to-be-nametagged-to-ride", false));

        enableFeature(BossBarListener.class, getBoolean("settings.dye-boss-bars", false));

        boolean lightningTransformEntities = getBoolean("settings.lightning-transforms-entities.enabled", false);
        handleLightningTransformedEntities(lightningTransformEntities);

        handleBetterDispenser();

        this.upgradeWoodToStoneTools = getBoolean("settings.smithing-table.tools.wood-to-stone", false);
        this.upgradeStoneToIronTools = getBoolean("settings.smithing-table.tools.stone-to-iron", false);
        this.upgradeIronToDiamondTools = getBoolean("settings.smithing-table.tools.iron-to-diamond", false);

        ToolUpgradesRecipes.addUpgradeRecipes(
                upgradeWoodToStoneTools,
                upgradeStoneToIronTools,
                upgradeIronToDiamondTools
        );

        enableFeature(FurnaceBurnTimeListener.class, getBoolean("settings.furnace.burn-time.enabled", false));
        this.furnaceBurnTimeMultiplier = getDouble("settings.furnace.burn-time.multiplier", 1.0);

        enableFeature(VoidTotemListener.class, getBoolean("settings.totem.work-on-void-death", false));

        boolean openIronDoorsWithHand = getBoolean("settings.gameplay-settings.open-iron-doors-with-hand", false);
        boolean openIronTrapdoorsWithHand = getBoolean("settings.gameplay-settings.open-iron-trapdoors-with-hand", false);

        if (openIronDoorsWithHand || openIronTrapdoorsWithHand) {
            plugin.getServer().getPluginManager().registerEvents(
                    new OpenIronDoorsWithHandListener(openIronDoorsWithHand, openIronTrapdoorsWithHand), plugin
            );
        }

        saveConfig();
    }

    private void saveConfig() {
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

    private void handleLightningTransformedEntities(boolean enable) {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("villager", "witch");
        defaults.put("pig", "zombie_piglin");
        ConfigurationSection section = getConfigSection("settings.lightning-transforms-entities.entities", defaults);
        if (!enable) return;
        for (String key : section.getKeys(false)) {
            String value = section.getString(key);
            lightningTransformEntities.put(key, value);
        }
        if (lightningTransformEntities.isEmpty()) return;

        plugin.getServer().getPluginManager().registerEvents(new LightningTransformsMobsListener(lightningTransformEntities), plugin);
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

    private void enableFeature(Class<?> listenerClass, boolean enable) {
        if (enable)
            plugin.registerListener(listenerClass);
    }
}
