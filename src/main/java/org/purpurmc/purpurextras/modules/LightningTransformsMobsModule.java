package org.purpurmc.purpurextras.modules;

import com.destroystokyo.paper.event.entity.EntityZapEvent;
import me.youhavetrouble.entiddy.Entiddy;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.purpurmc.purpurextras.PurpurExtras;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.purpurmc.purpurextras.util.EntityStatePreserverUtil.preserveEntityState;

/**
 * If enabled, entities with type on the left will be transformed into entity of type on the right.
 * This overrides vanilla transformations. Vanilla mob ids are used to identify mobs.
 * There are also special cases:
 * <p>
 * `killer_bunny` - a killer bunny
 * `jeb_sheep` - rainbow sheep
 * `johhny` - vindicator aggressive to most mobs
 * `toast` - special variant of rabbit
 */
public class LightningTransformsMobsModule implements PurpurExtrasModule, Listener {

    private final HashMap<String, Object> entities = new HashMap<>();
    private final boolean preserveMobStateOnLightningTransformation;

    protected LightningTransformsMobsModule() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("villager", "witch");
        defaults.put("pig", "zombified_piglin");
        ConfigurationSection section = PurpurExtras.getPurpurConfig().getConfigSection("settings.lightning-transforms-entities.entities", defaults);
        this.preserveMobStateOnLightningTransformation = PurpurExtras.getPurpurConfig().getBoolean("settings.lightning-transforms-entities.preserve-entity-state", false);
        HashMap<String, String> lightningTransformEntities = new HashMap<>();
        for (String key : section.getKeys(false)) {
            String value = section.getString(key);
            lightningTransformEntities.put(key, value);
        }
        for (Map.Entry<String, String> entry : lightningTransformEntities.entrySet()) {
            getEntityTypeOrSpecial(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void enable() {
        PurpurExtras plugin = PurpurExtras.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        if (entities.isEmpty()) return false;
        return PurpurExtras.getPurpurConfig().getBoolean("settings.lightning-transforms-entities.enabled", false);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onLightningStrike(EntityDamageByEntityEvent event) {
        if (!event.getDamager().getType().equals(EntityType.LIGHTNING_BOLT)) return;
        Entity entity = event.getEntity();
        if (!(entity instanceof LivingEntity livingEntity)) return;
        if (entity.getEntitySpawnReason().equals(CreatureSpawnEvent.SpawnReason.LIGHTNING)) {
            event.setCancelled(true);
            return;
        }
        Location location = entity.getLocation();
        Entiddy specialEntity = Entiddy.fromEntity(livingEntity);
        Object targetEntity = null;
        String entityKey = null;
        if (specialEntity != null) {
            entityKey = specialEntity.entiddy().toString().toLowerCase(Locale.ROOT);
            targetEntity = entities.get(entityKey);
        } else {
            entityKey = entity.getType().getKey().getKey();
            targetEntity = entities.get(entityKey);
        }
        if (targetEntity == null) return;
        event.setCancelled(true);
        Entity spawnedEntity = spawnEntity(targetEntity, location);
        // Preserve entity state before it is removed
        if (preserveMobStateOnLightningTransformation && spawnedEntity instanceof LivingEntity newEntity) {
            preserveEntityState(livingEntity, newEntity);
        }

        entity.remove();
    }

    private Entity spawnEntity(Object entity, Location location) {
        Entity spawnedEntity = null;
        if (entity instanceof EntityType entityType) {
            spawnedEntity = location.getWorld().spawnEntity(location, entityType, CreatureSpawnEvent.SpawnReason.LIGHTNING);
        } else if (entity instanceof Entiddy entiddy) {
            spawnedEntity = entiddy.entiddy().spawn(location, CreatureSpawnEvent.SpawnReason.LIGHTNING);
        }

        return spawnedEntity;
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
                Entiddy.valueOf(key.toUpperCase(Locale.ROOT));
                sourceKey = key;
            } catch (IllegalArgumentException ignored) {
            }
        }
        if (goal == null) {
            try {
                goal = Entiddy.valueOf(value.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ignored) {
            }
        }
        entities.put(sourceKey, goal);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onLightningStrike(EntityZapEvent event) {
        if (event.getBolt().isEffect()) return;
        Entity entity = event.getEntity();
        if (!(entity instanceof LivingEntity livingEntity)) return;
        if (entity.getEntitySpawnReason().equals(CreatureSpawnEvent.SpawnReason.LIGHTNING)) {
            event.setCancelled(true);
            return;
        }
        Location location = entity.getLocation();
        Entiddy specialEntity = Entiddy.fromEntity(livingEntity);
        if (specialEntity != null) {
            event.setCancelled(true);
            String specialEntityKey = specialEntity.entiddy().toString().toLowerCase(Locale.ROOT);
            Object targetEntity = entities.get(specialEntityKey);
            Entity spawnedEntity = spawnEntity(targetEntity, location);
            if (preserveMobStateOnLightningTransformation && spawnedEntity instanceof LivingEntity newEntity) {
                preserveEntityState(livingEntity, newEntity);
            }
            // Remove old entity after preserving the state
            entity.remove();
            return;
        }
        Object targetEntity = entities.get(entity.getType().getKey().getKey());
        if (targetEntity == null) {
            livingEntity.damage(5, event.getBolt());
            event.setCancelled(true);
            return;
        }
        Entity spawnedEntity = spawnEntity(targetEntity, location);
        if(preserveMobStateOnLightningTransformation && spawnedEntity instanceof  LivingEntity newEntity) {
            preserveEntityState(livingEntity,  newEntity);
        }
        // Remove old entity after transformation
        entity.remove();
        event.setCancelled(true);
    }

}
