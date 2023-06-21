package org.purpurmc.purpurextras.modules.implementation;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.util.permissions.DefaultPermissions;
import org.purpurmc.purpurextras.PurpurConfig;
import org.purpurmc.purpurextras.modules.ModuleInfo;
import org.purpurmc.purpurextras.modules.PurpurExtrasModule;

/**
 * Right click when sneaking on an item frame with item inside of it will make the item frame invisible.
 * Requires purpurextras.invisibleframes permission.
 */
@ModuleInfo(name = "Invisible Item Frames", description = "Right click an item frame to make it invisible!")
public class InvisibleItemFrame extends PurpurExtrasModule {

    private static final String invisFramePermission = "purpurextras.invisibleframes";

    public InvisibleItemFrame(PurpurConfig config) {
        super(config);
        DefaultPermissions.registerPermission(invisFramePermission, "Allows player to shift-right-click an item frame to turn it invisible", PermissionDefault.OP);
    }

    @Override
    public String getConfigPath() {
        return "settings.blocks.shift-right-click-for-invisible-item-frames";
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

        event.setCancelled(true);
        itemFrame.setVisible(!itemFrame.isVisible());
        itemFrame.setFixed(!itemFrame.isFixed());
    }
}
