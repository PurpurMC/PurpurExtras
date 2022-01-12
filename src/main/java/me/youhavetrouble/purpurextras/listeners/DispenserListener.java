package me.youhavetrouble.purpurextras.listeners;

import com.destroystokyo.paper.MaterialTags;
import io.papermc.paper.event.block.BlockPreDispenseEvent;
import me.youhavetrouble.purpurextras.PurpurExtras;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Jukebox;
import org.bukkit.block.data.type.Dispenser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class DispenserListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPreDispense(BlockPreDispenseEvent event) {
        if (!event.getBlock().getType().equals(Material.DISPENSER)) return;
        Dispenser dispenser = (Dispenser) event.getBlock().getBlockData();
        Block block = event.getBlock().getRelative(dispenser.getFacing());
        org.bukkit.block.Dispenser blockDispenser = (org.bukkit.block.Dispenser) event.getBlock().getState(false);
        ItemStack item = event.getItemStack();

        // Block breaking

        // if block is broken, stop processing
        if (handleBlockBreaking(event, item, blockDispenser, block)) return;

        // shears do not drop as an item from dispenser by default, so no need to cancel event
        if (PurpurExtras.getPurpurConfig().dispenserBreakBlockShears && item.getType().equals(Material.SHEARS)) {
            // if block was broken, don't process more
            if (tryBreakBlock(item, blockDispenser, block)) return;
        }

        // Interactions

        // Shear pumpkin
        if (PurpurExtras.getPurpurConfig().dispenserShearPumpkin
                && item.getType().equals(Material.SHEARS)
                && block.getType().equals(Material.PUMPKIN)
        ) {
            Inventory inventory = blockDispenser.getInventory();
            damageItem(item, inventory);
            event.setCancelled(true);
            block.setType(Material.CARVED_PUMPKIN);
            return;
        }

        // Swap records in jukebox
        if (PurpurExtras.getPurpurConfig().dispenserActivatesJukebox
                && MaterialTags.MUSIC_DISCS.isTagged(item)
                && block.getType().equals(Material.JUKEBOX)
        ) {
            event.setCancelled(true);
            Jukebox jukebox = (Jukebox) block.getState(false);
            jukebox.eject();
            ItemStack record = consumeItem(item);
            jukebox.setRecord(record);
            jukebox.update();
            return;
        }

    }

    private boolean handleBlockBreaking(
            BlockPreDispenseEvent event,
            ItemStack item,
            org.bukkit.block.Dispenser blockDispenser,
            Block block
    ) {
        if (PurpurExtras.getPurpurConfig().dispenserBreakBlockPickaxe && MaterialTags.PICKAXES.isTagged(item)) {
            event.setCancelled(true);
            tryBreakBlock(item, blockDispenser, block);
            return true;
        }
        if (PurpurExtras.getPurpurConfig().dispenserBreakBlockAxe && MaterialTags.AXES.isTagged(item)) {
            event.setCancelled(true);
            tryBreakBlock(item, blockDispenser, block);
            return true;
        }
        if (PurpurExtras.getPurpurConfig().dispenserBreakBlockShovel && MaterialTags.SHOVELS.isTagged(item)) {
            event.setCancelled(true);
            tryBreakBlock(item, blockDispenser, block);
            return true;
        }
        if (PurpurExtras.getPurpurConfig().dispenserBreakBlockHoe && MaterialTags.HOES.isTagged(item)) {
            event.setCancelled(true);
            tryBreakBlock(item, blockDispenser, block);
            return true;
        }
        return false;
    }

    private void damageItem(ItemStack itemStack, Inventory inventory) {
        int slot = inventory.first(itemStack);
        if (slot == -1) return;
        ItemStack item = inventory.getItem(slot);
        if (item == null) return;
        item.damage(1);
    }

    private ItemStack consumeItem(ItemStack itemStack) {
        ItemStack consumed = itemStack.clone();
        consumed.setAmount(1);
        itemStack.setAmount(itemStack.getAmount()-1);
        return consumed;
    }

    private boolean tryBreakBlock(ItemStack itemStack, org.bukkit.block.Dispenser dispenser, Block block) {
        if (block.getDestroySpeed(itemStack, false) <= 1.0f) return false;
        Inventory inventory = dispenser.getInventory();
        damageItem(itemStack, inventory);
        return block.breakNaturally(itemStack, true);
    }

}
