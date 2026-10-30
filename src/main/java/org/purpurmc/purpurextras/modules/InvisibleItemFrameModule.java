package org.purpurmc.purpurextras.modules;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.purpurmc.purpurextras.PurpurExtras;

/**
 * Right click when sneaking on an item frame with an item inside of it will make the item frame invisible.
 * Requires purpurextras.invisibleframes permission.
 * Players who are not allowed to build at the frame cannot toggle it.
 */
public class InvisibleItemFrameModule implements PurpurExtrasModule, Listener {

    protected InvisibleItemFrameModule() {}

    private final Permission invisFramePermission = new Permission(
            "purpurextras.invisibleframes",
            "Allows player to shift-right-click an item frame to turn it invisible",
            PermissionDefault.OP
    );

    @Override
    public void enable() {
        PurpurExtras plugin = PurpurExtras.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        registerPermissions(invisFramePermission);
        return PurpurExtras.getPurpurConfig().getBoolean("settings.blocks.shift-right-click-for-invisible-item-frames", false);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemFrameInteract(PlayerInteractEntityEvent event){
        if (event.getHand().equals(EquipmentSlot.OFF_HAND)) return;

        Player player = event.getPlayer();
        if (!player.isSneaking()) return;

        Entity entity = event.getRightClicked();
        if (!(entity instanceof ItemFrame itemFrame)) return;
        if (itemFrame.getItem().getType().equals(Material.AIR)) return;
        if (!player.hasPermission(invisFramePermission)) return;
        if (!canBuild(player, itemFrame)) return;

        event.setCancelled(true);
        itemFrame.setVisible(!itemFrame.isVisible());
        itemFrame.setFixed(!itemFrame.isFixed());
    }

    /**
     * Protection plugins decide build access by cancelling a block break.
     * The block is not actually broken.
     */
    private boolean canBuild(Player player, ItemFrame itemFrame) {
        Block occupied = itemFrame.getLocation().getBlock();
        Block attached = occupied.getRelative(itemFrame.getAttachedFace());
        if (!isBreakAllowed(player, attached)) return false;
        if (attached.equals(occupied)) return true;
        return isBreakAllowed(player, occupied);
    }

    private boolean isBreakAllowed(Player player, Block block) {
        BlockBreakEvent breakEvent = new BlockBreakEvent(block, player);
        player.getServer().getPluginManager().callEvent(breakEvent);
        return !breakEvent.isCancelled();
    }
}
