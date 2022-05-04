package me.youhavetrouble.purpurextras.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class SkeletonWitherRoseListener implements Listener {
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onRightClick(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (!(entity instanceof Skeleton)) return;
        Location location = entity.getLocation();
        Player player = event.getPlayer();
        if (!player.getInventory().getItemInMainHand().getType().equals(Material.WITHER_ROSE)) return;
        player.swingMainHand();
        entity.remove();
        location.getWorld().spawnEntity(location, EntityType.WITHER_SKELETON, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

}
