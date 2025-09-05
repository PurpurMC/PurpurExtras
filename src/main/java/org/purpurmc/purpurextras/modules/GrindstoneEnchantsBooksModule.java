package org.purpurmc.purpurextras.modules;

import org.purpurmc.purpurextras.PurpurExtras;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.purpurmc.purpur.event.inventory.GrindstoneTakeResultEvent;

import java.util.Map;

/**
 * If enabled and player has books in their inventory while disenchanting a non-book item in a grindstone,
 * books will be consumed to return the enchantments removed from the item to the player as enchanted books.
 * No exp will drop when doing this.
 * <p>
 * When disenchanting an ENCHANTED_BOOK in the grindstone, this module lets vanilla behavior convert it
 * to a normal BOOK (removing non-cursed enchants) and simply zeros the XP payout.
 */
public class GrindstoneEnchantsBooksModule implements PurpurExtrasModule, Listener {

    private final ItemStack BOOK = new ItemStack(Material.BOOK);

    protected GrindstoneEnchantsBooksModule() {
    }

    @Override
    public void enable() {
        try {
            Class.forName("org.purpurmc.purpur.event.inventory.GrindstoneTakeResultEvent");
        } catch (ClassNotFoundException e) {
            PurpurExtras.getInstance().getLogger().warning(this.getClass().getSimpleName() + " module requires you to run Purpur as your server software.");
            return;
        }
        PurpurExtras plugin = PurpurExtras.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        return PurpurExtras.getPurpurConfig().getBoolean("settings.grindstone.gives-enchants-back", false);
    }

    @EventHandler
    public void on(GrindstoneTakeResultEvent event) {
        GrindstoneInventory grindstoneInventory = event.getInventory();

        ItemStack lowerItem = grindstoneInventory.getLowerItem();
        if (lowerItem != null && !lowerItem.getType().isAir()) {
            return; // lower slot is not empty, do nothing
        }

        ItemStack upperItem = grindstoneInventory.getUpperItem();
        if (upperItem == null || upperItem.getType().isAir()) {
            return; // upper slot is empty, do nothing
        }

        // If the upper item is an ENCHANTED_BOOK, don't give enchants back as new books.
        // Let vanilla produce a normal BOOK (if any non-cursed enchants are present) and just zero XP.
        if (upperItem.getType() == Material.ENCHANTED_BOOK) {
            if (upperItem.hasItemMeta()) {
                Map<Enchantment, Integer> stored = ((EnchantmentStorageMeta) upperItem.getItemMeta()).getStoredEnchants();
                boolean hasNonCursed = stored.keySet().stream().anyMatch(e -> !e.isCursed());
                if (hasNonCursed) {
                    event.setExperienceAmount(0);
                }
            }
            return;
        }

        // For non-book items, consume blank BOOKs and give removed enchants back as enchanted books.
        if (!upperItem.hasEnchants()) {
            return;
        }
        Map<Enchantment, Integer> enchants = upperItem.getEnchants();
        if (enchants.isEmpty()) {
            return;
        }

        Player player = event.getPlayer();
        Location location = player.getLocation();
        World world = location.getWorld();
        PlayerInventory playerInventory = player.getInventory();

        boolean gaveAny = false;

        for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            Enchantment enchant = entry.getKey();
            int level = entry.getValue();

            if (enchant.isCursed()) {
                continue; // grindstones don't remove curses
            }

            if (player.getGameMode() != GameMode.CREATIVE) {
                // need one blank book per enchant we return
                if (!playerInventory.containsAtLeast(BOOK, 1)) {
                    break; // out of blank books - stop returning enchants
                }
                if (!playerInventory.removeItem(BOOK).isEmpty()) {
                    break; // could not remove book
                }
            }

            ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
            meta.addStoredEnchant(enchant, level, true);
            book.setItemMeta(meta);

            // add to inventory or drop if full
            playerInventory.addItem(book).forEach((index, stack) -> {
                Item drop = world.dropItemNaturally(location, stack);
                drop.setPickupDelay(0);
            });

            gaveAny = true;
        }

        if (gaveAny) {
            event.setExperienceAmount(0);
        }
    }
}
