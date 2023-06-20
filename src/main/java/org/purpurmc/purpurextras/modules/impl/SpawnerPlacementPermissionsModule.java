package org.purpurmc.purpurextras.modules.impl;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.permissions.PermissionDefault;
import org.purpurmc.purpurextras.PurpurExtras;
import org.purpurmc.purpurextras.modules.ModuleInfo;
import org.purpurmc.purpurextras.modules.PurpurExtrasModule;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.bukkit.util.permissions.DefaultPermissions.registerPermission;

/**
 * Players will need purpurextras.spawnerplace.<mobtype> permission to place spawners of that mob.
 */
@ModuleInfo(name = "Spawner Placement Permissions", description = "Binds the ability to place certain spawners by permission")
public class SpawnerPlacementPermissionsModule extends PurpurExtrasModule {

    public SpawnerPlacementPermissionsModule() {}

    private final String spawnerPlacePermission = "purpurextras.spawnerplace";
    private final Map<String, Boolean> mobSpawners = new HashMap<>();

    @Override
    public void enable() {
        super.enable();
        for (EntityType type : EntityType.values())
        {
            if (type.isAlive() && type.isSpawnable() )
            {
                String entityPermission = "." + type.toString().toLowerCase(Locale.ENGLISH);
                mobSpawners.put((spawnerPlacePermission + entityPermission), true);
            }
        }
        registerPermission(spawnerPlacePermission, "Allows player to place spawner", PermissionDefault.OP, mobSpawners);
    }

    @Override
    public String getConfigPath() {
        return "settings.spawner-placement-requires-specific-permission";
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSpawnerPlace(BlockPlaceEvent event){
        Player player = event.getPlayer();
        String entityType;
        String entityTypePermission;
        if (event.getBlock().getState(false) instanceof CreatureSpawner spawner) {
            entityType = String.valueOf(spawner.getSpawnedType()).toLowerCase(Locale.ENGLISH);
            entityTypePermission = ("." + entityType);
        } else {
            return;
        }
        if (player.hasPermission( spawnerPlacePermission + entityTypePermission)) return;
        event.setCancelled(true);
        player.sendMessage(PurpurExtras.getInstance().miniMessage.deserialize("<red>You do not have permission to place a <spawner> spawner!", Placeholder.unparsed("spawner", entityType)));
    }
}

