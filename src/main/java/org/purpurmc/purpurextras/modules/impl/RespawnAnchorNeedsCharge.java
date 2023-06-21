package org.purpurmc.purpurextras.modules.impl;

import org.purpurmc.purpurextras.PurpurExtras;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.util.BoundingBox;
import org.purpurmc.purpurextras.modules.ModuleInfo;
import org.purpurmc.purpurextras.modules.PurpurExtrasModule;

/**
 * If false, will make it so respawn anchors will never run out of charges.
 */
@ModuleInfo(name = "Respawn Anchor Charge", description = "If false, respawn anchors will never run out of charges!")
public class RespawnAnchorNeedsCharge extends PurpurExtrasModule {

    @Override
    public String getConfigPath() {
        return "settings.respawn-anchor-needs-charges";
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (!event.isAnchorSpawn())
            return;
        Block block = event.getRespawnLocation().getBlock();
        BoundingBox box = BoundingBox.of(block.getLocation(), 2, 2, 2);
        for (int y = (int) box.getMinY(); y < box.getMaxY(); y++) {
            for (int x = (int) box.getMinX(); x < box.getMaxX(); x++) {
                for (int z = (int) box.getMinZ(); z < box.getMaxZ(); z++) {
                    Location location = new Location(event.getRespawnLocation().getWorld(), x, y, z);
                    if (!location.getBlock().getType().equals(Material.RESPAWN_ANCHOR))
                        continue;
                    Block potentialAnchor = location.getBlock();
                    Bukkit.getScheduler().runTaskLater(PurpurExtras.getInstance(), () -> {
                        if (!potentialAnchor.getType().equals(Material.RESPAWN_ANCHOR))
                            return;
                        RespawnAnchor anchor = (RespawnAnchor) potentialAnchor.getBlockData();
                        anchor.setCharges(anchor.getMaximumCharges());
                        potentialAnchor.setBlockData(anchor);
                    }, 2);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerPlaceAnchor(BlockPlaceEvent event) {
        if (!event.getBlockPlaced().getType().equals(Material.RESPAWN_ANCHOR)) return;
        RespawnAnchor anchor = (RespawnAnchor) event.getBlockPlaced().getBlockData();
        anchor.setCharges(anchor.getMaximumCharges());
        event.getBlockPlaced().setBlockData(anchor);
    }
}
