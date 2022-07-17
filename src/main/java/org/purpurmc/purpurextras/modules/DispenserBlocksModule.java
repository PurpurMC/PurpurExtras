package org.purpurmc.purpurextras.modules;

import com.destroystokyo.paper.MaterialTags;
import io.papermc.paper.event.block.BlockPreDispenseEvent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Jukebox;
import org.bukkit.block.data.type.Dispenser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.purpurmc.purpurextras.PurpurConfig;
import org.purpurmc.purpurextras.PurpurExtras;

public class DispenserBlocksModule implements PurpurExtrasModule, Listener {

    private final boolean breakBlockPickaxe, breakBlockShovel, breakBlockHoe, breakBlockAxe, breakBlockShears,
            shearPumpkin, activateJukebox;

    protected DispenserBlocksModule() {
        PurpurConfig config = PurpurExtras.getPurpurConfig();
        breakBlockPickaxe = config.getBoolean("settings.dispenser.break-blocks.pickaxe", false);
        breakBlockShovel = config.getBoolean("settings.dispenser.break-blocks.shovel", false);
        breakBlockHoe = config.getBoolean("settings.dispenser.break-blocks.hoe", false);
        breakBlockAxe = config.getBoolean("settings.dispenser.break-blocks.axe", false);
        breakBlockShears = config.getBoolean("settings.dispenser.break-blocks.shears", false);
        shearPumpkin = config.getBoolean("settings.dispenser.shears-shear-pumpkin", false);
        activateJukebox = config.getBoolean("settings.dispenser.puts-discs-in-jukebox", false);
    }

    @Override
    public void enable() {
        PurpurExtras plugin = PurpurExtras.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        return anyTrue(
                breakBlockAxe,
                breakBlockHoe,
                breakBlockPickaxe,
                breakBlockShovel,
                breakBlockShears,
                shearPumpkin,
                activateJukebox
        );
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPreDispense(BlockPreDispenseEvent event) {
        if (!event.getBlock().getType().equals(Material.DISPENSER)) return;
        Dispenser dispenser = (Dispenser) event.getBlock().getBlockData();
        Block block = event.getBlock().getRelative(dispenser.getFacing());
        org.bukkit.block.Dispenser blockDispenser = (org.bukkit.block.Dispenser) event.getBlock().getState(false);
        ItemStack item = event.getItemStack();

        if (breakBlockPickaxe && MaterialTags.PICKAXES.isTagged(item)) {
            event.setCancelled(true);
            if (tryBreakBlock(item, blockDispenser, block)) return;
        }
        if (breakBlockAxe && MaterialTags.AXES.isTagged(item)) {
            event.setCancelled(true);
            if (tryBreakBlock(item, blockDispenser, block)) return;
        }
        if (breakBlockShovel && MaterialTags.SHOVELS.isTagged(item)) {
            event.setCancelled(true);
            if (tryBreakBlock(item, blockDispenser, block)) return;
        }
        if (breakBlockHoe && MaterialTags.HOES.isTagged(item)) {
            event.setCancelled(true);
            if (tryBreakBlock(item, blockDispenser, block)) return;
        }

        // Shear pumpkin
        if (shearPumpkin
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
        if (activateJukebox
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

    private boolean anyTrue(boolean... booleans) {
        for (boolean b : booleans) {
            if (b) return true;
        }
        return false;
    }
}
