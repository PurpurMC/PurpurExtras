package org.purpurmc.purpurextras.modules;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.permissions.PermissionDefault;
import org.purpurmc.purpurextras.PurpurConfig;
import org.purpurmc.purpurextras.PurpurExtras;

import static org.bukkit.util.permissions.DefaultPermissions.registerPermission;

public class NetherBuildHeightModule implements PurpurExtrasModule, Listener {

    private final int configBuildHeight;
    private final String noPermissionMessageContent;
    private final boolean noPermissionMessage;
    private final String netherBuildHeightBypassPermission = "purpurextras.netherbuildheightbypass";

    protected NetherBuildHeightModule() {
        PurpurConfig config = PurpurExtras.getPurpurConfig();
        this.configBuildHeight = config.getInt("settings.block-building-above-nether.height-limit", 128);
        this.noPermissionMessage = config.getBoolean("settings.block-building-above-nether.no-permission-message.enabled", false);
        this.noPermissionMessageContent = config.getString("settings.block-building-above-nether.no-permission-message.message", "<red>Max build height in this world is: <gold><height>");
    }

    @Override
    public void enable() {
        PurpurExtras plugin = PurpurExtras.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        registerPermission(netherBuildHeightBypassPermission, "Allows player to bypass the configured max nether build height", PermissionDefault.OP);
    }

    @Override
    public boolean shouldEnable() {
        return PurpurExtras.getPurpurConfig().getBoolean("settings.block-building-above-nether.enabled", false);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onNetherRoofBuild(BlockPlaceEvent event){
        Block block = event.getBlock();
        Player player = event.getPlayer();
        if(!(block.getWorld().getEnvironment().equals(World.Environment.NETHER))) return;
        if(block.getLocation().getBlockY() < configBuildHeight) return;
        if(player.hasPermission(netherBuildHeightBypassPermission)) return;
        if(noPermissionMessage){
            player.sendActionBar(MiniMessage.miniMessage().deserialize(noPermissionMessageContent, Placeholder.unparsed("height", String.valueOf(configBuildHeight))));
        }
        event.setCancelled(true);

    }

}
