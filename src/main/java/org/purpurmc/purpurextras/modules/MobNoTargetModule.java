package org.purpurmc.purpurextras.modules;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.purpurmc.purpurextras.PurpurExtras;

/**
 * If enabled, players having target.bypass.<mojang_mob_name> permission won't be targetted by that type of mob.
 */
public class MobNoTargetModule implements PurpurExtrasModule, Listener {
    protected MobNoTargetModule() {}
    @Override
    public void enable() {
        PurpurExtras plugin = PurpurExtras.getInstance();
        registerTargetPermissions(plugin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        return PurpurExtras.getPurpurConfig().getBoolean("settings.use-notarget-permissions", false);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMobTarget(EntityTargetEvent event) {
        if (!(event.getTarget() instanceof Player player)) return;
        if (!player.hasPermission("target.bypass." + event.getEntityType().getKey().getKey())) return;
        event.setCancelled(true);
    }

    private void registerTargetPermissions(PurpurExtras plugin){
        for (EntityType entityType : EntityType.values()) {
            if (!entityType.isAlive()) continue;
            String name = entityType.getKey().getKey();
            String permissionName = "target.bypass." + name;
            String description = "Prevents a user from being targeted by " + name + "s ";
            plugin.getServer().getPluginManager().addPermission(new Permission(permissionName,
                    description,
                    PermissionDefault.FALSE
            ));
        }
    }
}
