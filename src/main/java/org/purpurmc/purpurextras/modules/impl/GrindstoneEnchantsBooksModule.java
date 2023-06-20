package org.purpurmc.purpurextras.modules.impl;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.purpurmc.purpur.event.inventory.GrindstoneTakeResultEvent;
import org.purpurmc.purpurextras.modules.ModuleInfo;
import org.purpurmc.purpurextras.modules.PurpurExtrasModule;

import java.util.Map;

/**
 * If enabled and player has books in their inventory while disenchanting item in a grindstone,
 * books will be consumed to return the enchantments removed from the item to the player.
 * No exp will drop when doing this.
 * <p>
 * Listeners yoinked from <a href="https://gist.github.com/BillyGalbreath/de0f899a27b39daad5f5bf7c00e11045">here</a>}
 */
@ModuleInfo(name = "Grindstone Books", description = "Attaches lost Grindstone enchantments to any books in your inventory!")
public class GrindstoneEnchantsBooksModule extends PurpurExtrasModule {

    private final ItemStack BOOK = new ItemStack(Material.BOOK);

    @Override
    public String getConfigPath() {
        return "settings.grindstone.gives-enchants-back";
    }

    @EventHandler
    public void on(GrindstoneTakeResultEvent event) {
        GrindstoneInventory grindstoneInventory = event.getInventory();

        ItemStack lowerItem = grindstoneInventory.getLowerItem();
        if (lowerItem != null && !lowerItem.getType().isEmpty()) {
            return; // lower slot is not empty, do nothing
        }

        ItemStack upperItem = grindstoneInventory.getUpperItem();
        if (upperItem == null || upperItem.getType().isEmpty()) {
            return; // upper slot is empty, do nothing
        }

        Map<Enchantment, Integer> enchants;
        if (upperItem.getType() == Material.ENCHANTED_BOOK) {
            if (!upperItem.hasItemMeta()) {
                return;
            }
            enchants = ((EnchantmentStorageMeta) upperItem.getItemMeta()).getStoredEnchants();
        } else {
            if (!upperItem.hasEnchants()) {
                return;
            }
            enchants = upperItem.getEnchants();
        }

        if (enchants.isEmpty()) {
            return;
        }

        Player player = event.getPlayer();
        Location location = player.getLocation();
        World world = location.getWorld();
        PlayerInventory playerInventory = player.getInventory();

        for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            if (entry.getKey().isCursed()) {
                continue; // grindstones don't remove curses
            }
            if (player.getGameMode() != GameMode.CREATIVE) {
                if (!playerInventory.containsAtLeast(BOOK, 1)) {
                    return; // no more books to extract to
                }

                if (!playerInventory.removeItem(BOOK).isEmpty()) {
                    return; // could not remove book
                }
            }

            ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
            meta.addStoredEnchant(entry.getKey(), entry.getValue(), true);
            book.setItemMeta(meta);

            playerInventory.addItem(book).forEach((index, stack) -> {
                Item drop = world.dropItemNaturally(location, stack);
                drop.setPickupDelay(0);
            });
            event.setExperienceAmount(0);
        }
    }
}
