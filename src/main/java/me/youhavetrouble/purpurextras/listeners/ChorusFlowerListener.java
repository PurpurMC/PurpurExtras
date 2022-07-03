package me.youhavetrouble.purpurextras.listeners;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import me.youhavetrouble.purpurextras.PurpurExtras;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

public class ChorusFlowerListener implements Listener {
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void entityHitFlower(ProjectileHitEvent event){
        if(event.getHitBlock() == null) return;
        if (event.getHitBlock().getType().equals(Material.CHORUS_FLOWER)){
            Block chorusFlower = event.getHitBlock();
            event.setCancelled(true);
            chorusFlower.breakNaturally();
            chorusFlower.getWorld().dropItem(chorusFlower.getLocation(), new ItemStack(Material.CHORUS_FLOWER));
        }
    }
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void flowersBreak(BlockDestroyEvent event){
        if(!(event.getBlock().getType().equals(Material.CHORUS_FLOWER))) return;
        Location flowerLocation = event.getBlock().getLocation();
        Material blockMaterial = event.getBlock().getType();
        flowerLocation.getWorld().dropItem(flowerLocation, new ItemStack(blockMaterial));
    }
}
