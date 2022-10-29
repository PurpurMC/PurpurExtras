package org.purpurmc.purpurextras.modules;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import com.sk89q.worldedit.util.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.util.permissions.DefaultPermissions;
import org.purpurmc.purpurextras.PurpurExtras;

public class InvisibleItemFrameModule implements PurpurExtrasModule, Listener {

    protected InvisibleItemFrameModule() {}

    private final String invisFramePermission = "purpurextras.invisibleframes";

    @Override
    public void enable() {
        PurpurExtras plugin = PurpurExtras.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

    }

    @Override
    public boolean shouldEnable() {
        DefaultPermissions.registerPermission(invisFramePermission, "Allows player to shift-right-click an item frame to turn it invisible", PermissionDefault.OP);
        return PurpurExtras.getPurpurConfig().getBoolean("settings.blocks.shift-right-click-for-invisible-item-frames", false);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onItemFrameInteract(PlayerInteractEntityEvent event){
        if (event.getHand().equals(EquipmentSlot.OFF_HAND)) return;

        Player player = event.getPlayer();
        if (!player.isSneaking()) return;

        Entity entity = event.getRightClicked();
        if (!(entity instanceof ItemFrame itemFrame)) return;
        if (itemFrame.getItem().getType().equals(Material.AIR)) return;
        if (!player.hasPermission(invisFramePermission)) return;

        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        Location loc = localPlayer.getLocation();
        WorldGuardPlatform platform = WorldGuard.getInstance().getPlatform();
        RegionContainer regionContainer = platform.getRegionContainer();
        RegionQuery query = regionContainer.createQuery();
        boolean canBypass = platform.getSessionManager().hasBypass(localPlayer, localPlayer.getWorld());
        if (!query.testBuild(loc, localPlayer) && !canBypass) return;

        event.setCancelled(true);
        itemFrame.setVisible(!itemFrame.isVisible());
        itemFrame.setFixed(!itemFrame.isFixed());
    }
}
