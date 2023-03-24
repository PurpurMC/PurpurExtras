package org.purpurmc.purpurextras.modules;

import com.destroystokyo.paper.MaterialSetTag;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.purpurmc.purpurextras.PurpurExtras;

public class AnvilSplitsMinecartsAndBoatsModule implements PurpurExtrasModule, Listener {

    private final boolean splitBoats, splitMinecarts;

    protected AnvilSplitsMinecartsAndBoatsModule() {
        this.splitMinecarts = PurpurExtras.getPurpurConfig().getBoolean("settings.anvil-splits-minecarts", false);
        this.splitBoats = PurpurExtras.getPurpurConfig().getBoolean("settings.anvil-splits-boats", false);
    }
    @Override
    public void enable() {
        PurpurExtras plugin = PurpurExtras.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        return PurpurExtras.getPurpurConfig().getBoolean("settings.anvil-splits-minecarts", false)
                || PurpurExtras.getPurpurConfig().getBoolean("settings.anvil-splits-boats", false);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onAnvilDrop(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Item item)) return;
        if (!(event.getDamager() instanceof FallingBlock fallingBlock)) return;

        if (!MaterialSetTag.ANVIL.isTagged(fallingBlock.getBlockData().getMaterial())) return;

        Material itemMaterial = item.getItemStack().getType();
        Location location = event.getEntity().getLocation();

        if (splitBoats && MaterialSetTag.ITEMS_CHEST_BOATS.isTagged(itemMaterial)) {
            String boatMaterialString = item.getItemStack().getType().toString().replace("_CHEST", "");
            Material boatMaterial = Material.matchMaterial(boatMaterialString);
            if (boatMaterial == null) return;
            location.getWorld().dropItemNaturally(location, new ItemStack(Material.CHEST));
            location.getWorld().dropItemNaturally(location, new ItemStack(boatMaterial));
            return;
        }

        if (!splitMinecarts) return;

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
