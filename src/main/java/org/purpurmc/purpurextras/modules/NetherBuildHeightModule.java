package org.purpurmc.purpurextras.modules;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.purpurmc.purpurextras.PurpurConfig;
import org.purpurmc.purpurextras.PurpurExtras;

/**
 * Adds a build height limit to the nether
 * Configuration:
 *
 * **enabled**
 * Enables the feature.
 *
 * **height-limit**
 * Maximum height players without purpurextras.netherbuildheightbypass permission can build in nether worlds.
 *
 * **no-permission-message**
 * Message to display in action bar when trying to build above set limit in nether worlds.
 */
public class NetherBuildHeightModule implements PurpurExtrasModule, Listener {

    private final int configBuildHeight;
    private final String noPermissionMessageContent;
    private final Permission netherBuildHeightBypassPermission = new Permission("purpurextras.netherbuildheightbypass",
            "Allows player to bypass the configured max nether build height",
            PermissionDefault.OP);

    protected NetherBuildHeightModule() {
        PurpurConfig config = PurpurExtras.getPurpurConfig();
        this.configBuildHeight = config.getInt("settings.block-building-above-nether.height-limit", 128);
        this.noPermissionMessageContent = config.getString("settings.block-building-above-nether.no-permission-message", "<red>Max build height in this world is: <gold><height>");
    }

    @Override
    public void enable() {
        PurpurExtras plugin = PurpurExtras.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        if (!isPermissionRegistered(netherBuildHeightBypassPermission)) {
            PurpurExtras.getInstance().getServer().getPluginManager().addPermission(netherBuildHeightBypassPermission);
        }
        return PurpurExtras.getPurpurConfig().getBoolean("settings.block-building-above-nether.enabled", false);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onNetherRoofBuild(BlockPlaceEvent event){
        Block block = event.getBlock();
        Player player = event.getPlayer();
        if (!(block.getWorld().getEnvironment().equals(World.Environment.NETHER))) return;
        if (block.getLocation().getBlockY() < configBuildHeight) return;
        if (player.hasPermission(netherBuildHeightBypassPermission)) return;
        if (!"".equals(noPermissionMessageContent)) {
            player.sendActionBar(PurpurExtras.getInstance().miniMessage.deserialize(noPermissionMessageContent, Placeholder.unparsed("height", String.valueOf(configBuildHeight))));
        }
        event.setCancelled(true);
    }

}
