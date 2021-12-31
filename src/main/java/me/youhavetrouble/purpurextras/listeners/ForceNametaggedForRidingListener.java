package me.youhavetrouble.purpurextras.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.spigotmc.event.entity.EntityMountEvent;

public class ForceNametaggedForRidingListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onMount(EntityMountEvent event) {
        Entity entity = event.getMount();
        if (entity.getType().equals(EntityType.PLAYER)) return;
        if (entity.getCustomName() != null) return;
        event.setCancelled(true);
    }

}
