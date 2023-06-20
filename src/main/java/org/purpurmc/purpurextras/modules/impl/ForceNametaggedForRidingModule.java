package org.purpurmc.purpurextras.modules.impl;

import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Steerable;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.purpurmc.purpurextras.modules.ModuleInfo;
import org.purpurmc.purpurextras.modules.PurpurExtrasModule;
import org.spigotmc.event.entity.EntityMountEvent;

/**
 * If enabled, only nametagged mobs can be mounted/steered using purpur's rideable option.
 */
@ModuleInfo(name = "Mounts need a name", description = "Rideable mobs need a nametag to be ridden!")
public class ForceNametaggedForRidingModule extends PurpurExtrasModule {

    @Override
    public String getConfigPath() {
        return "settings.rideables.mob-needs-to-be-nametagged-to-ride";
    }

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
