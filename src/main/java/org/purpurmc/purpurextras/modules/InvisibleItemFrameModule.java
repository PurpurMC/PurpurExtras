package org.purpurmc.purpurextras.modules;

import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.purpurmc.purpurextras.PurpurConfig;
import org.purpurmc.purpurextras.PurpurExtras;

/**
 * Right click when sneaking on an item frame with item inside of it will make the item frame invisible.
 * Requires purpurextras.invisibleframes permission.
 */
public class InvisibleItemFrameModule implements PurpurExtrasModule, Listener {

    private ToggleClickType clickType;

    protected InvisibleItemFrameModule() {
        String clickString = PurpurExtras.getPurpurConfig().getString("settings.blocks.invisible-frames.click-type", "LEFT");
        try {
            clickType = ToggleClickType.valueOf(clickString.toUpperCase());
        } catch (IllegalArgumentException e) {
            PurpurExtras.getInstance().getSLF4JLogger().warn("{} is not a valid choice for click type! Please use the values 'RIGHT' or 'LEFT'! Defaulting to 'LEFT'", clickString);
            clickType = ToggleClickType.LEFT;
        }
    }

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
        PurpurConfig config = PurpurExtras.getPurpurConfig();
        migrateConfig(config);
        return config.getBoolean("settings.blocks.invisible-frames.enabled", false);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemFrameInteract(PlayerInteractEntityEvent event) {
        if (event.getHand().equals(EquipmentSlot.OFF_HAND)) return;
        if (clickType.equals(ToggleClickType.LEFT)) return;

        Player player = event.getPlayer();
        if (!player.isSneaking()) return;

        Entity entity = event.getRightClicked();
        if (!(entity instanceof ItemFrame itemFrame)) return;
        if (!passesChecks(player, itemFrame)) return;
        event.setCancelled(true);
        toggleFrame(itemFrame);
    }


    /**
     * This handles the attack when the item frame is invisible.
     * FOR SOME REASON the other event doesn't fire when the item frame is hit in this state
     */

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemFrameAttack(EntityDamageByEntityEvent event) {
        if (clickType.equals(ToggleClickType.RIGHT)) return;
        if (!(event.getEntity() instanceof ItemFrame itemFrame)) return;
        if (!(event.getDamager() instanceof Player player)) return;
        if (!player.isSneaking()) return;
        if (!passesChecks(player, itemFrame)) return;
        event.setCancelled(true);
        toggleFrame(itemFrame);
    }

    /**
     * Handles when the item frame is invisible, 'cause apparently it can't handle the visible one as well for some reason.
     */

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemFrameAttack(PrePlayerAttackEntityEvent event) {
        Player player = event.getPlayer();
        if (!player.isSneaking()) return;
        if (clickType.equals(ToggleClickType.RIGHT)) return;
        if (!(event.getAttacked() instanceof ItemFrame itemFrame)) return;
        if (!passesChecks(player, itemFrame)) return;
        event.setCancelled(true);
        toggleFrame(itemFrame);
    }

    private boolean passesChecks(Player player, ItemFrame itemFrame) {
        if (itemFrame.getItem().getType().equals(Material.AIR)) return false;
        return player.hasPermission(invisFramePermission);
    }

    private void toggleFrame(ItemFrame itemFrame) {
        itemFrame.setVisible(!itemFrame.isVisible());
        itemFrame.setFixed(!itemFrame.isFixed());
    }

    public enum ToggleClickType {
        LEFT,
        RIGHT;
    }

    private void migrateConfig(PurpurConfig config) {
        boolean previousValue = config.getBooleanAndRemove("settings.blocks.shift-right-click-for-invisible-item-frames", false);
        config.getBoolean("settings.blocks.invisible-frames.enabled", previousValue);
    }
}
