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
import org.bukkit.permissions.PermissionDefault;
import org.purpurmc.purpurextras.PurpurExtras;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.bukkit.util.permissions.DefaultPermissions.registerPermission;

public class SpawnerPlacementPermissionsModule implements PurpurExtrasModule, Listener {

    protected SpawnerPlacementPermissionsModule() {}

    private final String spawnerPlacePermission = "purpurextras.spawnerplace";
    private final Map<String, Boolean> mobSpawners = new HashMap<String, Boolean>();

    @Override
    public void enable() {
        PurpurExtras plugin = PurpurExtras.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
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
    public boolean shouldEnable() {
        return PurpurExtras.getPurpurConfig().getBoolean("settings.gameplay-settings.spawner-placement-requires-specific-pemission", false);
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
        player.sendMessage(MiniMessage.miniMessage().deserialize("<red>You do not have permission to place a <spawner> spawner!", Placeholder.unparsed("spawner", entityType)));
    }
}

