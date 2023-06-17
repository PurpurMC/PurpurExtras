package org.purpurmc.purpurextras.modules;

import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.LoomInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.BannerMeta;
import org.purpurmc.purpurextras.PurpurConfig;
import org.purpurmc.purpurextras.PurpurExtras;

import java.util.*;
import java.util.logging.Logger;

/**
 * If enabled, banners will be able to use more than the limit of 6 layers.
 * In default config up to 10 banner layers are allowed.
 */
public class BannerLayersModule implements PurpurExtrasModule, Listener {
    protected BannerLayersModule() {
        PurpurConfig config = PurpurExtras.getPurpurConfig();
        Map<String, Object> defaults = new HashMap<>();

        defaults.put("layers", 10);
        ConfigurationSection section = config.getConfigSection("settings.banner-layers", defaults);
        Logger logger = PurpurExtras.getInstance().getLogger();
        if (section.getInt("layers") < 6) {
            logger.warning("Banner layers is set to less than 6, this is not currently supported.");
        }
    }

    // predefined arraylist
    private static final List<Material> bannertypes = new ArrayList<>();


    public static HashMap<Player, List<Pattern>> plpett = new HashMap<>();

    @Override
    public void enable() {
        PurpurExtras plugin = PurpurExtras.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        bannertypes.add(Material.BLACK_BANNER);
        bannertypes.add(Material.BLUE_BANNER);
        bannertypes.add(Material.BROWN_BANNER);
        bannertypes.add(Material.CYAN_BANNER);
        bannertypes.add(Material.GRAY_BANNER);
        bannertypes.add(Material.GREEN_BANNER);
        bannertypes.add(Material.LIGHT_BLUE_BANNER);
        bannertypes.add(Material.LIGHT_GRAY_BANNER);
        bannertypes.add(Material.LIME_BANNER);
        bannertypes.add(Material.MAGENTA_BANNER);
        bannertypes.add(Material.ORANGE_BANNER);
        bannertypes.add(Material.PINK_BANNER);
        bannertypes.add(Material.PURPLE_BANNER);
        bannertypes.add(Material.RED_BANNER);
        bannertypes.add(Material.WHITE_BANNER);
        bannertypes.add(Material.YELLOW_BANNER);
    }

    @Override
    public boolean shouldEnable() {
        return PurpurExtras.getPurpurConfig().getBoolean("settings.banner-layers.enabled", false);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        if (event.getInventory() instanceof LoomInventory) {
            if (isMaterialBanner(Objects.requireNonNull(event.getCurrentItem()).getType())) {
                if (cantSetLayer(event.getCurrentItem())) {
                    p.sendPlainMessage("You cannot add another layer to this banner.");
                    return;
                }
                if (event.getSlotType().equals(InventoryType.SlotType.CRAFTING)) {
                    if ((event.getAction().equals(InventoryAction.PLACE_SOME)) ||
                            (event.getAction().equals(InventoryAction.PLACE_ALL))
                            || (event.getAction().equals(InventoryAction.PLACE_ONE))) {
                        Save(p, event.getCurrentItem());
                    } else {
                        back(p, event.getCurrentItem());
                    }
                } else if (event.getSlotType().equals(InventoryType.SlotType.CONTAINER)) {
                    if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                        Save(p, event.getCurrentItem());
                    }
                } else if (event.getSlotType().equals(InventoryType.SlotType.RESULT)) {
                    craftload(p, event.getCurrentItem());
                } else if (event.getSlotType().equals(InventoryType.SlotType.QUICKBAR)) {
                    if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                        Save(p, event.getCurrentItem());
                    }
                }
            } else if (isMaterialBanner(Objects.requireNonNull(event.getCursor()).getType())) {
                if (cantSetLayer(event.getCursor())) {
                    p.sendPlainMessage("You cannot add another layer to this banner.");
                    return;
                }
                if (event.getSlotType().equals(InventoryType.SlotType.CRAFTING) && (event.getAction().equals(InventoryAction.PLACE_SOME) || event.getAction().equals(InventoryAction.PLACE_ALL) || event.getAction().equals(InventoryAction.PLACE_ONE))) {
                    Save(p, event.getCursor());
                }
            }
        }
    }

    public static boolean isMaterialBanner(Material banner) {
        return bannertypes.contains(banner);
    }

    public static void Save(Player p, ItemStack Banner) {
        BannerMeta meta = (BannerMeta) Banner.getItemMeta();
        List<Pattern> list = meta.getPatterns();
        if ((list.size() <= 5)) return;
        plpett.remove(p);
        plpett.put(p, list);
        changestack(Banner);
    }

    public static void changestack(ItemStack Banner) {
        BannerMeta meta = (BannerMeta) Banner.getItemMeta();
        List<Pattern> list = meta.getPatterns();
        while (list.size() > 5) {
            list.remove(0);
        }
        meta.setPatterns(list);
        Banner.setItemMeta(meta);
    }

    public static void back(Player p, ItemStack Banner) {
        if (!plpett.containsKey(p)) return;
        List<Pattern> oldpatterns = plpett.get(p);
        BannerMeta meta = (BannerMeta) Banner.getItemMeta();

        meta.setPatterns(oldpatterns);
        Banner.setItemMeta(meta);
        plpett.remove(p);
    }

    public static void craftload(Player p, ItemStack Banner) {
        if (!plpett.containsKey(p)) return;
        List<Pattern> oldpatterns = plpett.get(p);
        BannerMeta meta = (BannerMeta) Banner.getItemMeta();

        List<Pattern> newpatterns = meta.getPatterns();
        oldpatterns.add(newpatterns.get(newpatterns.size() - 1));

        meta.setPatterns(oldpatterns);
        Banner.setItemMeta(meta);
        plpett.remove(p);
    }

    public static int getMaxLayers() {
        return PurpurExtras.getPurpurConfig().getInt("settings.banner-layers.layers", 6);
    }

    public static boolean cantSetLayer(ItemStack Banner) {
        BannerMeta meta = (BannerMeta) Banner.getItemMeta();
        List<Pattern> list = meta.getPatterns();
        int a = getMaxLayers();
        return list.size() >= a;
    }
}