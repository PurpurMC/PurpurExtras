package org.purpurmc.purpurextras.modules;

import com.destroystokyo.paper.MaterialSetTag;
import org.bukkit.block.banner.Pattern;
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
import org.purpurmc.purpurextras.PurpurExtras;

import java.util.HashMap;
import java.util.List;

/**
 * If enabled, banners will be able to use more than the limit of 6 layers.
 * In default config up to 10 banner layers are allowed.
 */
public class LoomMaxLayersModule implements PurpurExtrasModule, Listener {

    private final HashMap<Player, List<Pattern>> playerBannerData = new HashMap<>();
    private int maxLayers;

    protected LoomMaxLayersModule() {
        this.maxLayers = PurpurExtras.getPurpurConfig().getInt("settings.loom.max-layers", 6);
        if (this.maxLayers <= 0) this.maxLayers = 6;
    }

    @Override
    public void enable() {
        PurpurExtras plugin = PurpurExtras.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        return this.maxLayers != 6;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack currentItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();

        if (!(event.getInventory() instanceof LoomInventory)) return;
        if (MaterialSetTag.BANNERS.isTagged(currentItem.getType())) {
            if (newLayerExceedsMaxLayers(currentItem)) return;
            switch (event.getSlotType()) {
                case CRAFTING -> {
                    if (isPlaceAction(event.getAction())) {
                        save(player, currentItem);
                    } else {
                        undo(player, currentItem);
                    }
                }
                case CONTAINER, QUICKBAR -> {
                    if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                        save(player, currentItem);
                    }
                }
                case RESULT -> craftLoad(player, currentItem);
            }
        } else if (MaterialSetTag.BANNERS.isTagged(cursorItem.getType())) {
            if (newLayerExceedsMaxLayers(cursorItem)) return;
            if (event.getSlotType().equals(InventoryType.SlotType.CRAFTING) && isPlaceAction(event.getAction())) {
                save(player, cursorItem);
            }
        }
    }

    private boolean isPlaceAction(InventoryAction action) {
        return action == InventoryAction.PLACE_SOME ||
                action == InventoryAction.PLACE_ALL ||
                action == InventoryAction.PLACE_ONE;
    }

    private void save(Player player, ItemStack banner) {
        BannerMeta meta = (BannerMeta) banner.getItemMeta();
        List<Pattern> patterns = meta.getPatterns();
        if (patterns.size() <= 5) return;
        playerBannerData.remove(player);
        playerBannerData.put(player, patterns);
        changeStack(banner);
    }

    private void changeStack(ItemStack banner) {
        BannerMeta meta = (BannerMeta) banner.getItemMeta();
        List<Pattern> patterns = meta.getPatterns();
        while (patterns.size() > 5) patterns.remove(0);
        meta.setPatterns(patterns);
        banner.setItemMeta(meta);
    }

    private void undo(Player player, ItemStack banner) {
        if (!playerBannerData.containsKey(player)) return;
        List<Pattern> oldPatterns = playerBannerData.get(player);
        BannerMeta meta = (BannerMeta) banner.getItemMeta();

        meta.setPatterns(oldPatterns);
        banner.setItemMeta(meta);
        playerBannerData.remove(player);
    }

    private void craftLoad(Player player, ItemStack banner) {
        if (!playerBannerData.containsKey(player)) return;
        List<Pattern> oldPatterns = playerBannerData.get(player);
        BannerMeta meta = (BannerMeta) banner.getItemMeta();

        List<Pattern> newPatterns = meta.getPatterns();
        oldPatterns.add(newPatterns.get(newPatterns.size() - 1));

        meta.setPatterns(oldPatterns);
        banner.setItemMeta(meta);
        playerBannerData.remove(player);
    }

    private boolean newLayerExceedsMaxLayers(ItemStack banner) {
        BannerMeta meta = (BannerMeta) banner.getItemMeta();
        List<Pattern> patterns = meta.getPatterns();
        return patterns.size() == maxLayers;
    }
}