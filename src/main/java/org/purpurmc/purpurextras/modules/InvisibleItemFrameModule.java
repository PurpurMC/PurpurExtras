package org.purpurmc.purpurextras.modules;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.permissions.PermissionDefault;
import org.purpurmc.purpurextras.PurpurExtras;

import static org.bukkit.util.permissions.DefaultPermissions.registerPermission;

public class InvisibleItemFrameModule implements PurpurExtrasModule, Listener {

    protected InvisibleItemFrameModule() {}

    private final String invisFramePermission = "purpurextras.invisibleframes";

    @Override
    public void enable() {
        PurpurExtras plugin = PurpurExtras.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        registerPermission(invisFramePermission, "Allows player to shift-right-click an item frame to turn it invisible", PermissionDefault.OP);
    }

    @Override
    public boolean shouldEnable() {
        return PurpurExtras.getPurpurConfig().getBoolean("settings.blocks.shift-right-click-for-invisible-item-frames", false);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onItemFrameInteract(PlayerInteractEntityEvent event){
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        if(event.getHand().equals(EquipmentSlot.OFF_HAND)) return;
        if(!player.isSneaking()) return;
        if(!(entity instanceof ItemFrame itemFrame)) return;
        if(itemFrame.getItem().getType().equals(Material.AIR)) return;
        if(!player.hasPermission("purpurextras.invisibleframes")) return;
        event.setCancelled(true);
        itemFrame.setVisible(!itemFrame.isVisible());
        itemFrame.setFixed(!itemFrame.isFixed());
    }
}
