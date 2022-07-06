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
    public boolean dispenserBreakBlockPickaxe, dispenserBreakBlockShovel, dispenserBreakBlockHoe,
            dispenserBreakBlockShears, dispenserBreakBlockAxe, dispenserShearPumpkin, dispenserActivatesJukebox;
    public final boolean upgradeWoodToStoneTools;
    public final boolean upgradeStoneToIronTools;
    public final boolean upgradeIronToDiamondTools;
    public final String beeHiveLoreBees, beeHiveLoreHoney;
    public final HashMap<Material, Material> anvilCrushBlocksIndex = new HashMap<>();
    public final HashSet<EntityType> stonecutterDamageBlacklist = new HashSet<>();
    public final HashMap<String, String> lightningTransformEntities = new HashMap<>();
    public final double furnaceBurnTimeMultiplier;

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

        enableFeature(ChorusFlowerListener.class, getBoolean("settings.blocks.chorus-flowers-always-drop", false));

        enableFeature(DispenserCauldronListener.class, getBoolean("settings.blocks.dispense-filled-buckets-in-cauldrons", false));

        enableFeature(RespawnAnchorNeedsChargeListener.class, !getBoolean("settings.gameplay-settings.respawn-anchor-needs-charges", true));

        enableFeature(EscapeCommandSlashListener.class, getBoolean("settings.chat.escape-commands", false));

        getAnvilCrushIndex();
        enableFeature(AnvilMakesSandListener.class, getBoolean("settings.anvil-crushes-blocks.enabled", false));

        enableFeature(GrindstoneEnchantsBooksListener.class, getBoolean("settings.grindstone.gives-enchants-back", false));

        enableFeature(ForceNametaggedForRidingListener.class, getBoolean("settings.rideables.mob-needs-to-be-nametagged-to-ride", false));

        enableFeature(MobNoTargetListener.class, getBoolean("settings.use-notarget-permissions", false));

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

        List<String> stonecutterDamageblacklist = getList("settings.stonecutter-damage-filter.blacklist", List.of("player"));
        if (getBoolean("settings.stonecutter-damage-filter.enabled", false)) {
            handleStonecutterDamageBlacklist(stonecutterDamageblacklist, plugin);
        }

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
     * @param defKV Default key-value map
     */
    private ConfigurationSection getConfigSection(String path, Map<String, Object> defKV) {
        if (config.isConfigurationSection(path))
            return config.getConfigurationSection(path);
        return config.createSection(path, defKV);
    }

    /**
     * @return List of strings or empty list if list doesn't exist in configuration file
     */
    private List<String> getList(String path, List<String> def) {
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

    private void getAnvilCrushIndex() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("cobblestone", "sand");
        ConfigurationSection section = getConfigSection("settings.anvil-crushes-blocks.blocks", defaults);
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
        if (stonecutterDamageBlacklist.isEmpty()) return;
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
