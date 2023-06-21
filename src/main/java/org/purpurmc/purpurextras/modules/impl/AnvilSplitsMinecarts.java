package org.purpurmc.purpurextras.modules.impl;

import com.destroystokyo.paper.MaterialSetTag;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.purpurmc.purpurextras.PurpurConfig;
import org.purpurmc.purpurextras.modules.ModuleInfo;
import org.purpurmc.purpurextras.modules.PurpurExtrasModule;

/**
 * If enabled, dropping an anvil from significant height onto minecarts with content
 * in its item form, it will not destroy the item, but split the minecart and the
 * content and drop them both.
 */
@ModuleInfo(name = "Anvil Breaks Minecarts", description = "Anvils can fall and break Minecarts with Chests in them!")
public class AnvilSplitsMinecarts extends PurpurExtrasModule {

    public AnvilSplitsMinecarts(PurpurConfig config) {
        super(config);
    }

    @Override
    public String getConfigPath() {
        return "settings.anvil-splits-minecarts";
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onAnvilDrop(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Item item)) return;
        if (!(event.getDamager() instanceof FallingBlock fallingBlock)) return;

        if (!MaterialSetTag.ANVIL.isTagged(fallingBlock.getBlockData().getMaterial())) return;

        Material itemMaterial = item.getItemStack().getType();
        Location location = event.getEntity().getLocation();

        switch (itemMaterial) {
            case CHEST_MINECART -> {
                location.getWorld().dropItemNaturally(location, new ItemStack(Material.CHEST));
                location.getWorld().dropItemNaturally(location, new ItemStack(Material.MINECART));
            }
            case FURNACE_MINECART -> {
                location.getWorld().dropItemNaturally(location, new ItemStack(Material.FURNACE));
                location.getWorld().dropItemNaturally(location, new ItemStack(Material.MINECART));
            }
            case TNT_MINECART -> {
                location.getWorld().dropItemNaturally(location, new ItemStack(Material.TNT));
                location.getWorld().dropItemNaturally(location, new ItemStack(Material.MINECART));
            }
            case HOPPER_MINECART -> {
                location.getWorld().dropItemNaturally(location, new ItemStack(Material.HOPPER));
                location.getWorld().dropItemNaturally(location, new ItemStack(Material.MINECART));
            }
        }
    }
}
