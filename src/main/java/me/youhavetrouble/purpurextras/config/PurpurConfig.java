package me.youhavetrouble.purpurextras.config;

import me.youhavetrouble.purpurextras.PurpurExtras;
import me.youhavetrouble.purpurextras.listeners.*;
import me.youhavetrouble.purpurextras.recipes.ToolUpgradesRecipes;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

public class PurpurConfig {

    Logger logger;
    FileConfiguration config;
    File configPath;
    public final boolean beeHiveLore, respawnAnchorNeedsCharges, escapeEscapedCommands, anvilCrushesBlocks,
            dispenserBreakBlockPickaxe, dispenserBreakBlockShovel, dispenserBreakBlockHoe, dispenserBreakBlockShears,
            dispenserBreakBlockAxe, grindstoneGivesEnchantsBack, dispenserShearPumpkin, dispenserActivatesJukebox,
            upgradeWoodToStoneTools, upgradeStoneToIronTools, upgradeIronToDiamondTools;
    public final String beeHiveLoreBees, beeHiveLoreHoney;
    public final HashMap<Material, Material> anvilCrushBlocksIndex = new HashMap<>();

    public PurpurConfig(PurpurExtras plugin) {
        plugin.reloadConfig();
        logger = plugin.getLogger();
        config = plugin.getConfig();
        configPath = new File(plugin.getDataFolder(), "config.yml");

        // Make sure that no listeners are registered from the plugin
        HandlerList.unregisterAll(plugin);

        this.beeHiveLore = getBoolean("settings.items.beehive-lore.enabled", false);
        if (beeHiveLore)
            plugin.registerListener(BeehiveLoreListener.class);
        this.beeHiveLoreBees = getString("settings.items.beehive-lore.bees", "<reset><gray>Bees: <bees>/<maxbees>");
        this.beeHiveLoreHoney = getString("settings.items.beehive-lore.honey", "<reset><gray>Honey level: <honey>/<maxhoney>");

        this.respawnAnchorNeedsCharges = getBoolean("settings.gameplay-settings.respawn-anchor-needs-charges", true);
        if (!respawnAnchorNeedsCharges)
            plugin.registerListener(RespawnAnchorNeedsChargeListener.class);

        this.escapeEscapedCommands = getBoolean("settings.chat.escape-commands", false);
        if (escapeEscapedCommands)
            plugin.registerListener(EscapeCommandSlashListener.class);

        this.anvilCrushesBlocks = getBoolean("settings.anvil-crushes-blocks.enabled", false);

        ConfigurationSection anvilToCrush = config.getConfigurationSection("settings.anvil-crushes-blocks.blocks");
        getAnvilCrushIndex(anvilToCrush);
        if (anvilCrushesBlocks) {
            plugin.registerListener(AnvilMakesSandListener.class);
        }

        this.grindstoneGivesEnchantsBack = getBoolean("settings.grindstone.gives-enchants-back", false);
        if (grindstoneGivesEnchantsBack) {
            plugin.registerListener(GrindstoneEnchantsBooksListener.class);
        }

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

        this.upgradeWoodToStoneTools = getBoolean("settings.smithing-table.tools.wood-to-stone", false);
        this.upgradeStoneToIronTools = getBoolean("settings.smithing-table.tools.stone-to-iron", false);
        this.upgradeIronToDiamondTools = getBoolean("settings.smithing-table.tools.iron-to-diamond", false);

        ToolUpgradesRecipes.addUpgradeRecipes(
                upgradeWoodToStoneTools,
                upgradeStoneToIronTools,
                upgradeIronToDiamondTools
        );

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

}
