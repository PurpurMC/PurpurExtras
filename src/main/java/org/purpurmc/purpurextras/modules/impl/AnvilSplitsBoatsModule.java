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
import org.purpurmc.purpurextras.modules.ModuleInfo;
import org.purpurmc.purpurextras.modules.PurpurExtrasModule;

/**
 * If enabled, dropping an anvil from significant height onto boats with content
 * in its item form, it will not destroy the item, but split the boat and the
 * content and drop them both.
 */
@ModuleInfo(name = "Anvil Breaks Boats", description = "Anvils can fall and break Boats with Chests in them!")
public class AnvilSplitsBoatsModule extends PurpurExtrasModule {
    @Override
    public String getConfigPath() {
        return "settings.anvil-splits-boats";
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onAnvilDrop(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Item item)) return;
        if (!(event.getDamager() instanceof FallingBlock fallingBlock)) return;

        if (!MaterialSetTag.ANVIL.isTagged(fallingBlock.getBlockData().getMaterial())) return;

        Material itemMaterial = item.getItemStack().getType();
        Location location = event.getEntity().getLocation();

        if (MaterialSetTag.ITEMS_CHEST_BOATS.isTagged(itemMaterial)) {
            String boatMaterialString = item.getItemStack().getType().toString().replace("_CHEST", "");
            Material boatMaterial = Material.matchMaterial(boatMaterialString);
            if (boatMaterial == null) return;
            location.getWorld().dropItemNaturally(location, new ItemStack(Material.CHEST));
            location.getWorld().dropItemNaturally(location, new ItemStack(boatMaterial));
        }
    }
}
