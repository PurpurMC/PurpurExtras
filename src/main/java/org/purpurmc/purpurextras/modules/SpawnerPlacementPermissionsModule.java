package org.purpurmc.purpurextras.modules;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.purpurmc.purpurextras.PurpurExtras;


/**
 * Players will need purpurextras.spawnerplace.<mobtype> permission to place spawners of that mob.
 */
public class SpawnerPlacementPermissionsModule implements PurpurExtrasModule, Listener {

    protected SpawnerPlacementPermissionsModule() {
    }

    private final String basePermissionString = "purpurextras.spawnerplace.";

    @Override
    public void enable() {
        PurpurExtras plugin = PurpurExtras.getInstance();
        registerSpawnerPermissions(plugin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        return PurpurExtras.getPurpurConfig().getBoolean("settings.gameplay-settings.spawner-placement-requires-specific-permission", false);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSpawnerPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!(event.getBlock().getState(false) instanceof CreatureSpawner spawner)) return;
        EntityType type = spawner.getSpawnedType();
        if (type == null) return;
        String entityName = type.getKey().getKey();
        if (player.hasPermission(basePermissionString + entityName)) return;
        event.setCancelled(true);
        player.sendMessage(MiniMessage.miniMessage().deserialize("<red>You do not have permission to place a <spawner> spawner!", Placeholder.unparsed("spawner", entityName)));
    }

    private void registerSpawnerPermissions(PurpurExtras plugin){
        for (EntityType type : EntityType.values()) {
            if (!type.isAlive() || !type.isSpawnable()) continue;
            String entityName = type.getKey().getKey();
            Permission spawnerPermissions = new Permission(basePermissionString + entityName,
                    "Allows player to place a " + entityName + " spawner",
                    PermissionDefault.OP);
            plugin.getServer().getPluginManager().addPermission(spawnerPermissions);
        }
    }
}

