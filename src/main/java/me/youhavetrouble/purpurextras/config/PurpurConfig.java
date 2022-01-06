package me.youhavetrouble.purpurextras.config;

import me.youhavetrouble.purpurextras.PurpurExtras;
import me.youhavetrouble.purpurextras.listeners.*;
import me.youhavetrouble.purpurextras.recipes.ToolUpgradesRecipes;
import org.bukkit.Material;
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
    public boolean dispenserBreakBlockPickaxe,dispenserBreakBlockShovel, dispenserBreakBlockHoe,
            dispenserBreakBlockShears, dispenserBreakBlockAxe, dispenserShearPumpkin, dispenserActivatesJukebox;
    public final boolean upgradeWoodToStoneTools;
    public final boolean upgradeStoneToIronTools;
    public final boolean upgradeIronToDiamondTools;
    public final String beeHiveLoreBees, beeHiveLoreHoney;
    public final HashMap<Material, Material> anvilCrushBlocksIndex = new HashMap<>();
    public final HashSet<EntityType> stonecutterDamageBlacklist = new HashSet<>();

    public PurpurConfig() {
        plugin.reloadConfig();
        logger = plugin.getLogger();
        config = plugin.getConfig();
        configPath = new File(plugin.getDataFolder(), "config.yml");

        // Make sure that no listeners are registered from the plugin
        HandlerList.unregisterAll(plugin);

        enableFeature(BeehiveLoreListener.class, getBoolean("settings.items.beehive-lore.enabled", false));

        this.beeHiveLoreBees = getString("settings.items.beehive-lore.bees", "<reset><gray>Bees: <bees>/<maxbees>");
        this.beeHiveLoreHoney = getString("settings.items.beehive-lore.honey", "<reset><gray>Honey level: <honey>/<maxhoney>");

        enableFeature(RespawnAnchorNeedsChargeListener.class, getBoolean("settings.gameplay-settings.respawn-anchor-needs-charges", true));

        enableFeature(EscapeCommandSlashListener.class, getBoolean("settings.chat.escape-commands", false));

        ConfigurationSection anvilToCrush = config.getConfigurationSection("settings.anvil-crushes-blocks.blocks");
        getAnvilCrushIndex(anvilToCrush);
        enableFeature(AnvilMakesSandListener.class, getBoolean("settings.anvil-crushes-blocks.enabled", false));

        enableFeature(GrindstoneEnchantsBooksListener.class, getBoolean("settings.grindstone.gives-enchants-back", false));

        enableFeature(ForceNametaggedForRidingListener.class, getBoolean("settings.rideables.mob-needs-to-be-nametagged-to-ride", false));

        enableFeature(MobNoTargetListener.class, getBoolean("settings.use-notarget-permissions", false));

        handleBetterDispenser();

        this.upgradeWoodToStoneTools = getBoolean("settings.smithing-table.tools.wood-to-stone", false);
        this.upgradeStoneToIronTools = getBoolean("settings.smithing-table.tools.stone-to-iron", false);
        this.upgradeIronToDiamondTools = getBoolean("settings.smithing-table.tools.iron-to-diamond", false);

        ToolUpgradesRecipes.addUpgradeRecipes(
                upgradeWoodToStoneTools,
                upgradeStoneToIronTools,
                upgradeIronToDiamondTools
        );

        List<String> blacklist = getList("settings.stonecutter-damage-filter.blacklist");
        if (getBoolean("settings.stonecutter-damage-filter.enabled", false)) {
            handleStonecutterDamageBlacklist(blacklist, plugin);
        }

        saveConfig();
    }

    public void saveConfig() {
        try {
            config.save(configPath);
        } catch (IOException e) {
            logger.severe("Failed to save configuration file! - " + e.getLocalizedMessage());
        }
    }

    private boolean getBoolean(String path, boolean def) {
        if (config.isSet(path))
            return config.getBoolean(path, def);
        config.set(path, def);
        return def;
    }

    private String getString(String path, String def) {
        if (config.isSet(path))
            return config.getString(path, def);
        config.set(path, def);
        return def;
    }

    private double getDouble(String path, double def) {
        if (config.isSet(path))
            return config.getDouble(path, def);
        config.set(path, def);
        return def;
    }

    /**
     * @param path config path
     * @return List of strings or empty list if list doesn't exist in configuration file
     */
    private List<String> getList(String path) {
        if (config.isSet(path))
            return config.getStringList(path);
        List<String> newList = new ArrayList<>();
        config.set(path, newList);
        return newList;
    }

    private void getAnvilCrushIndex(ConfigurationSection section) {
        if (section == null) {
            ConfigurationSection newSection = config.createSection("settings.anvil-crushes-blocks.blocks");
            newSection.set("cobblestone", "sand");
            section = newSection;
        }
        for (String key : section.getKeys(false)) {
            String matString = section.getString(key);
            if (matString == null) continue;
            Material materialFrom = Material.getMaterial(key.toUpperCase());
            if (materialFrom == null || !materialFrom.isBlock()) {
                logger.warning(key + " is not valid block material.");
                continue;
            }
            Material materialTo = Material.getMaterial(matString.toUpperCase());
            if (materialTo == null || !materialTo.isBlock()) {
                logger.warning(matString + " is not valid block material.");
                continue;
            }
            anvilCrushBlocksIndex.put(materialFrom, materialTo);
        }
    }

    private void handleStonecutterDamageBlacklist(List<String> blacklist, PurpurExtras plugin) {
        if (blacklist.isEmpty()) return;
        for (EntityType entityType : EntityType.values()) {
            if (!entityType.isAlive()) continue;
            for (String str : blacklist) {
                if (entityType.getKey().getKey().equals(str.toLowerCase(Locale.ROOT)))
                    stonecutterDamageBlacklist.add(entityType);
            }
        }
        if (!stonecutterDamageBlacklist.isEmpty())
            plugin.registerListener(StonecutterDamageListener.class);
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
                || dispenserShearPumpkin) {
            plugin.registerListener(DispenserListener.class);
        }
    }

    private void enableFeature(Class<?> listenerClass, boolean enable) {
        if (enable)
            plugin.registerListener(listenerClass);
    }
}
