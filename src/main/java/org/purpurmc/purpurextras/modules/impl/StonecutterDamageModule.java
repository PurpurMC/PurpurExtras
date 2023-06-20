package org.purpurmc.purpurextras.modules.impl;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.purpurmc.purpurextras.modules.ModuleInfo;
import org.purpurmc.purpurextras.modules.PurpurExtrasModule;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/**
 * If enabled, allows filtering which entity types don't get damaged by stonecutters if
 * <a href="https://purpurmc.org/docs/Configuration/stonecutter_1"> stonecutter dealing damage</a> Purpur feature is
 * enabled.
 */
@ModuleInfo(name = "Stonecutter Damage Filter", description = "Filters which entity types don't get damaged by a Stonecutter")
public class StonecutterDamageModule extends PurpurExtrasModule {

    private final HashSet<EntityType> stonecutterDamageBlacklist = new HashSet<>();

    public StonecutterDamageModule() {
        List<String> entityBlacklist = getConfigList("blacklist", List.of("player"));
        if (getConfigBoolean("enabled", false)) {
            if (entityBlacklist.isEmpty()) return;
            for (EntityType entityType : EntityType.values()) {
                if (!entityType.isAlive()) continue;
                for (String str : entityBlacklist) {
                    if (entityType.getKey().getKey().equals(str.toLowerCase(Locale.ENGLISH)))
                        stonecutterDamageBlacklist.add(entityType);
                }
            }
        }
    }

    @Override
    public boolean shouldEnable() {
        return super.shouldEnable() && !stonecutterDamageBlacklist.isEmpty();
    }

    @Override
    public String getConfigPath() {
        return "settings.stonecutter-damage-filter";
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onStonecutterDamage(EntityDamageEvent event) {

        if (!event.getCause().equals(EntityDamageEvent.DamageCause.CONTACT)) return;
        if (!event.getEntity().isOnGround()) return;

        if (stonecutterDamageBlacklist.contains(event.getEntity().getType()))
            event.setCancelled(true);

    }
}
