package me.youhavetrouble.purpurextras.listeners;

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

// Yoinked from https://gist.github.com/BillyGalbreath/de0f899a27b39daad5f5bf7c00e11045
public class GrindstoneEnchantsBooksListener implements Listener {

    public static final ItemStack BOOK = new ItemStack(Material.BOOK);

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
                return; // no enchants to extract
            }
            enchants = ((EnchantmentStorageMeta) upperItem.getItemMeta()).getStoredEnchants();
        } else {
            if (!upperItem.hasEnchants()) {
                return; // no enchants to extract
            }
            enchants = upperItem.getEnchants();
        }

        if (enchants.isEmpty()) {
            return; // no enchants to extract
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

            // create enchanted book
            ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
            meta.addStoredEnchant(entry.getKey(), entry.getValue(), true);
            book.setItemMeta(meta);

            // add enchanted book to player inventory
            playerInventory.addItem(book).forEach((index, stack) -> {
                // drop on ground if didn't fit in inventory
                Item drop = world.dropItemNaturally(location, stack);
                drop.setPickupDelay(0);
            });

            event.setExperienceAmount(0);
        }
    }
}


