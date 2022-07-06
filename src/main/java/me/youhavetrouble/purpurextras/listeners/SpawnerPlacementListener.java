package me.youhavetrouble.purpurextras.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class SpawnerPlacementListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSpawnerPlace(BlockPlaceEvent event){
        Material block = event.getBlock().getType();
        Player player = event.getPlayer();
        if (!(block.equals(Material.SPAWNER))) return;
        if (!(player.hasPermission("purpurextras.spawnerPlace"))){
            player.sendMessage(Component.text("You do not have permission to place spawners", NamedTextColor.RED));
            event.setCancelled(true);
        }
    }
}
