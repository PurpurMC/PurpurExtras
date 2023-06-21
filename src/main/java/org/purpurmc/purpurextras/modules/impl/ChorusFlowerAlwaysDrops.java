package org.purpurmc.purpurextras.modules.impl;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.purpurmc.purpurextras.modules.ModuleInfo;
import org.purpurmc.purpurextras.modules.PurpurExtrasModule;

/**
 * Makes it so chorus flowers always drop, no matter if they were destroyed directly or not.
 */
@ModuleInfo(name = "Chorus Flowers Drop", description = "Chorus Flowers will always drop their item!")
public class ChorusFlowerAlwaysDrops extends PurpurExtrasModule {

    @Override
    public String getConfigPath() {
        return "settings.blocks.chorus-flowers-always-drop";
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void entityHitChorusFlower(ProjectileHitEvent event){
        if(event.getHitBlock() == null) return;
        if (event.getHitBlock().getType().equals(Material.CHORUS_FLOWER)){
            Block chorusFlower = event.getHitBlock();
            event.setCancelled(true);
            chorusFlower.breakNaturally();
            chorusFlower.getWorld().dropItem(chorusFlower.getLocation(), new ItemStack(Material.CHORUS_FLOWER));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void chorusFlowerBreak(BlockDestroyEvent event){
        Block block = event.getBlock();
        if(!(block.getType().equals(Material.CHORUS_FLOWER))) return;
        Location flowerLocation = block.getLocation();
        Material blockMaterial = block.getType();
        flowerLocation.getWorld().dropItem(flowerLocation, new ItemStack(blockMaterial));
    }
}
