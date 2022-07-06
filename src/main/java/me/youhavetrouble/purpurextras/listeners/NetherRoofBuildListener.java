package me.youhavetrouble.purpurextras.listeners;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class NetherRoofBuildListener implements Listener {

    @EventHandler
    public void onBuild(BlockPlaceEvent event){
        Block block = event.getBlock();
        if(!(block.getWorld().hasCeiling())) return;
        if(block.getLocation().getBlockY() < 128) return;
        event.setCancelled(true);
    }
}
