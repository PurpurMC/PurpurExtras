package org.purpurmc.purpurextras.modules.implementation;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.util.permissions.DefaultPermissions;
import org.purpurmc.purpurextras.PurpurConfig;
import org.purpurmc.purpurextras.PurpurExtras;
import org.purpurmc.purpurextras.modules.ModuleInfo;
import org.purpurmc.purpurextras.modules.PurpurExtrasModule;

/**
 * Adds a build height limit to the nether
 * Configuration:
 * <p>
 * **enabled**
 * Enables the feature.
 * <p>
 * **height-limit**
 * Maximum height players without purpurextras.netherbuildheightbypass permission can build in nether worlds.
 * <p>
 * **no-permission-message**
 * Message to display in action bar when trying to build above set limit in nether worlds.
 */
@ModuleInfo(name = "Nether Build Height Limit", description = "Adds a permission based build height limit to the nether!")
public class NetherBuildHeight extends PurpurExtrasModule {

    private final int configBuildHeight;
    private final String noPermissionMessageContent;
    private final String netherBuildHeightBypassPermission = "purpurextras.netherbuildheightbypass";

    public NetherBuildHeight(PurpurConfig config) {
        super(config);
        this.configBuildHeight = getConfigInt("height-limit", 128);
        this.noPermissionMessageContent = getConfigString("no-permission-message", "<red>Max build height in this world is: <gold><height>");
        DefaultPermissions.registerPermission(netherBuildHeightBypassPermission, "Allows player to bypass the configured max nether build height", PermissionDefault.OP);
    }

    @Override
    public String getConfigPath() {
        return "settings.block-building-above-nether";
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
