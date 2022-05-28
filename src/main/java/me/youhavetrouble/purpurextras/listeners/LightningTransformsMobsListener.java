package me.youhavetrouble.purpurextras.listeners;

import com.destroystokyo.paper.event.entity.EntityZapEvent;
import me.youhavetrouble.entiddy.Entiddy;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LightningTransformsMobsListener implements Listener {

    private static final HashMap<String, Object> entities = new HashMap<>();

    public LightningTransformsMobsListener(Map<String, String> lightningTransformEntities) {
        for (Map.Entry<String, String> entry : lightningTransformEntities.entrySet()) {
            getEntityTypeOrSpecial(entry.getKey(), entry.getValue());
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onLightningStrike(EntityDamageByEntityEvent event) {
        if (!event.getDamager().getType().equals(EntityType.LIGHTNING)) return;
        Entity entity = event.getEntity();
        if (!(entity instanceof LivingEntity livingEntity)) return;
        if (entity.getEntitySpawnReason().equals(CreatureSpawnEvent.SpawnReason.LIGHTNING)) {
            event.setCancelled(true);
            return;
        };
        Location location = entity.getLocation();
        Entiddy specialEntity = Entiddy.fromEntity(livingEntity);
        if (specialEntity != null) {
            event.setCancelled(true);
            entity.remove();
            String specialEntityKey = specialEntity.entiddy().toString().toLowerCase(Locale.ROOT);
            Object targetEntity = entities.get(specialEntityKey);
            spawnEntity(targetEntity, location);
            return;
        }
        Object targetEntity = entities.get(entity.getType().getKey().getKey());
        if (targetEntity == null) return;
        event.setCancelled(true);
        entity.remove();
        spawnEntity(targetEntity, location);
    }

    private void spawnEntity(Object entity, Location location) {
        if (entity instanceof EntityType entityType) {
            location.getWorld().spawnEntity(location, entityType, CreatureSpawnEvent.SpawnReason.LIGHTNING);
        } else if (entity instanceof Entiddy entiddy) {
            entiddy.entiddy().spawn(location, CreatureSpawnEvent.SpawnReason.LIGHTNING);
        }
    }

    private void getEntityTypeOrSpecial(String key, String value) {
        String sourceKey = null;
        Object goal = null;
        for (EntityType entityType : EntityType.values()) {
            if (!entityType.isSpawnable()) continue;
            String entityKey = entityType.getKey().getKey();
            if (entityKey.equals(key.toLowerCase(Locale.ROOT))) {
                sourceKey = key;
            }
            if (entityKey.equals(value.toLowerCase(Locale.ROOT))) {
                goal = entityType;
            }
        }
        if (sourceKey == null) {
            try {
                Entiddy entiddy = Entiddy.valueOf(key.toUpperCase(Locale.ROOT));
                sourceKey = key;
            } catch (IllegalArgumentException ignored) {}
        }
        if (goal == null) {
            try {
                goal = Entiddy.valueOf(value.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ignored) {}
        }
        System.out.println(sourceKey);
        System.out.println(goal);
        entities.put(sourceKey, goal);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onLightningStrike(EntityZapEvent event) {
        event.setCancelled(true);
    }
}
