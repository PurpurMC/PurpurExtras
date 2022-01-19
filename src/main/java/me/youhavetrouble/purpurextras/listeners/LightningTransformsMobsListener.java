package me.youhavetrouble.purpurextras.listeners;

import com.destroystokyo.paper.event.entity.EntityZapEvent;
import me.youhavetrouble.purpurextras.PurpurExtras;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class LightningTransformsMobsListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onLightiningStrike(EntityZapEvent event) {
        event.setCancelled(true);
        Entity entity = event.getEntity();
        EntityType type = entity.getType();
        Location location = entity.getLocation();
        EntityType newEntityType = PurpurExtras.getPurpurConfig().lightningTransformEntities.get(type);
        if (newEntityType == null) return;

        entity.remove();
        location.getWorld().spawnEntity(location, newEntityType, CreatureSpawnEvent.SpawnReason.LIGHTNING);
    }
}
