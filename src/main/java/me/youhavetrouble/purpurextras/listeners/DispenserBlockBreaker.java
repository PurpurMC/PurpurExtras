package me.youhavetrouble.purpurextras.listeners;

import io.papermc.paper.event.block.BlockPreDispenseEvent;
import me.youhavetrouble.purpurextras.PurpurExtras;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Dispenser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class DispenserBlockBreaker implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onDispense(BlockPreDispenseEvent event) {
        if (!event.getBlock().getType().equals(Material.DISPENSER)) return;
        Dispenser dispenser = (Dispenser) event.getBlock().getBlockData();
        Block block = event.getBlock().getRelative(dispenser.getFacing());
        org.bukkit.block.Dispenser blockDispenser = (org.bukkit.block.Dispenser) event.getBlock().getState(false);
        ItemStack item = event.getItemStack();

        if (PurpurExtras.getPurpurConfig().dispenserBreakBlockPickaxe && item.getType().toString().endsWith("_PICKAXE")) {
            event.setCancelled(true);
            tryBreakBlock(item, blockDispenser, block);
            return;
        }
        if (PurpurExtras.getPurpurConfig().dispenserBreakBlockAxe && item.getType().toString().endsWith("_AXE")) {
            event.setCancelled(true);
            tryBreakBlock(item, blockDispenser, block);
            return;
        }
        if (PurpurExtras.getPurpurConfig().dispenserBreakBlockShovel && item.getType().toString().endsWith("_SHOVEL")) {
            event.setCancelled(true);
            tryBreakBlock(item, blockDispenser, block);
            return;
        }
        if (PurpurExtras.getPurpurConfig().dispenserBreakBlockHoe && item.getType().toString().endsWith("_HOE")) {
            event.setCancelled(true);
            tryBreakBlock(item, blockDispenser, block);
            return;
        }
        if (PurpurExtras.getPurpurConfig().dispenserBreakBlockShears && item.getType().equals(Material.SHEARS)) {
            event.setCancelled(true);
            tryBreakBlock(item, blockDispenser, block);
            return;
        }
    }

    private void tryBreakBlock(ItemStack itemStack, org.bukkit.block.Dispenser dispenser, Block block) {
        if (block.getDestroySpeed(itemStack, false) <= 1.0f) return;
        Inventory inventory = dispenser.getInventory();
        int slot = inventory.first(itemStack);
        if (slot == -1) return;
        ItemStack item = inventory.getItem(slot);
        if (item == null) return;
        item.damage(1);
        block.breakNaturally(item, true);
    }

}
