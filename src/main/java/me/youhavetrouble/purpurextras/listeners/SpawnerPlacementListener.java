package me.youhavetrouble.purpurextras.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Locale;

public class SpawnerPlacementListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSpawnerPlace(BlockPlaceEvent event){
        Player player = event.getPlayer();
        String entityType;
        if (event.getBlock().getState(false) instanceof CreatureSpawner spawner) {
            entityType = ("." + spawner.getSpawnedType()).toLowerCase(Locale.ROOT);
        } else {
            return;
        }
        if (!(player.hasPermission("purpurextras.spawnerplace" + entityType) || player.hasPermission("purpurextras.spawnerplace.everything"))){
            event.setCancelled(true);
            player.sendMessage(Component.text("You do not have permission to place a " + spawner.getSpawnedType().toString().toLowerCase(Locale.ROOT) + " spawner!", NamedTextColor.RED));
        }
    }
}
