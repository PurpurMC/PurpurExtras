package org.purpurmc.purpurextras.modules;

import org.purpurmc.purpurextras.PurpurExtras;
import org.purpurmc.purpurextras.PurpurConfig;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/**
 * If enabled, allows filtering which entity types don't get damaged by stonecutters if
 * <a href="https://purpurmc.org/docs/Configuration/stonecutter_1"> stonecutter dealing damage</a> Purpur feature is
 * enabled.
 */
public class StonecutterDamageModule implements PurpurExtrasModule, Listener {

    private final HashSet<EntityType> stonecutterDamageBlacklist = new HashSet<>();

    protected StonecutterDamageModule() {
        PurpurConfig config = PurpurExtras.getPurpurConfig();
        List<String> entityBlacklist = config.getList("settings.stonecutter-damage-filter.blacklist", List.of("player"));
        if (config.getBoolean("settings.stonecutter-damage-filter.enabled", false)) {
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
    public void enable() {
        PurpurExtras plugin = PurpurExtras.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        if (stonecutterDamageBlacklist.isEmpty()) return false;
        return PurpurExtras.getPurpurConfig().getBoolean("settings.stonecutter-damage-filter.enabled", false);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onStonecutterDamage(EntityDamageEvent event) {

        if (!event.getCause().equals(EntityDamageEvent.DamageCause.CONTACT)) return;
        if (!event.getEntity().isOnGround()) return;

        if (stonecutterDamageBlacklist.contains(event.getEntity().getType()))
            event.setCancelled(true);

    }
}
