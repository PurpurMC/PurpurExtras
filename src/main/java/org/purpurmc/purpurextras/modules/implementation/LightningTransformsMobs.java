package org.purpurmc.purpurextras.modules.implementation;

import com.destroystokyo.paper.event.entity.EntityZapEvent;
import me.youhavetrouble.entiddy.Entiddy;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.purpurmc.purpurextras.PurpurConfig;
import org.purpurmc.purpurextras.modules.ModuleInfo;
import org.purpurmc.purpurextras.modules.PurpurExtrasModule;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
@ModuleInfo(name = "Lightning Transforms Mobs", description = "Lightning hitting mobs transforms them into something special!")
public class LightningTransformsMobs extends PurpurExtrasModule {

    private final HashMap<String, Object> entities = new HashMap<>();

    public LightningTransformsMobs(PurpurConfig config) {
        super(config);
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("villager", "witch");
        defaults.put("pig", "zombie_piglin");
        ConfigurationSection section = getConfigSection("entities", defaults);
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
    public boolean shouldEnable() {
        return super.shouldEnable() && !entities.isEmpty();
    }

    @Override
    public String getConfigPath() {
        return "settings.lightning-transforms-entities";
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onLightningStrike(EntityDamageByEntityEvent event) {
        if (!event.getDamager().getType().equals(EntityType.LIGHTNING)) return;
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
            entity.remove();
            String specialEntityKey = specialEntity.entiddy().toString().toLowerCase(Locale.ROOT);
            Object targetEntity = entities.get(specialEntityKey);
            spawnEntity(targetEntity, location);
            return;
        }
        Object targetEntity = entities.get(entity.getType().getKey().getKey());
        if (targetEntity == null) {
            livingEntity.damage(5, event.getBolt());
            event.setCancelled(true);
            return;
        }
        entity.remove();
        spawnEntity(targetEntity, location);
        event.setCancelled(true);
    }

}
