package me.youhavetrouble.purpurextras.listeners;

import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.spigotmc.event.entity.EntityMountEvent;

public class ForceNametaggedForRidingListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onMount(EntityMountEvent event) {
        Entity entity = event.getMount();

        // if pig or strider with a saddle, ignore
        if (entity instanceof Steerable steerable && steerable.hasSaddle()) return;

        // if mountable in vanilla, ignore
        if (entity instanceof Vehicle && (!(entity instanceof Steerable))) return;

        // if player, ignore
        if (entity.getType().equals(EntityType.PLAYER)) return;

        // if has nametag, ignore
        if (entity.getCustomName() != null) return;

        // otherwise don't allow mounting
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onMountMove(EntityMoveEvent event) {
        if (!(event.getEntity() instanceof Vehicle vehicle)) return;
        if (vehicle.getPassengers().isEmpty()) return;
        Entity passenger = vehicle.getPassengers().get(0);
        if (!(passenger instanceof Player player)) return;
        if (vehicle.getCustomName() != null && player.hasPermission("allow.ride."+vehicle.getType().getKey().getKey())) return;
        event.setCancelled(true);
    }

}
