package org.purpurmc.purpurextras.modules;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.purpurmc.purpurextras.PurpurConfig;
import org.purpurmc.purpurextras.PurpurExtras;

/**
 * Allows opening iron doors and trapdoors with a hand, just like wooden doors.
 */
public class OpenIronDoorsWithHandModule implements PurpurExtrasModule, Listener {

    private final boolean doors, trapdoors;
    private final Permission openIronDoorsPermission = new Permission("purpurextras.openirondoors", "Allows for opening iron doors with a hand", PermissionDefault.TRUE);
    private final Permission openIronTrapdoorsPermission = new Permission("purpurextras.openirontrapdoors","Allows for opening iron trapdoors with a hand", PermissionDefault.TRUE);

    protected OpenIronDoorsWithHandModule() {
        PurpurConfig config = PurpurExtras.getPurpurConfig();
        doors = config.getBoolean("settings.gameplay-settings.open-iron-doors-with-hand", false);
        trapdoors = config.getBoolean("settings.gameplay-settings.open-iron-trapdoors-with-hand", false);
    }
    @Override
    public void enable() {
        PurpurExtras plugin = PurpurExtras.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        if (!isRegistered(openIronDoorsPermission)) {
            PurpurExtras.getInstance().getServer().getPluginManager().addPermission(openIronDoorsPermission);
        }
        if (!isRegistered(openIronTrapdoorsPermission)) {
            PurpurExtras.getInstance().getServer().getPluginManager().addPermission(openIronTrapdoorsPermission);
        }
        return doors || trapdoors;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onIronDoorOpen(PlayerInteractEvent event) {
        if (!EquipmentSlot.HAND.equals(event.getHand())) return;
        if (event.getPlayer().isSneaking()) return;
        if (!event.getAction().isRightClick()) return;
        if (event.getClickedBlock() == null) return;
        Block block = event.getClickedBlock();
        World world = block.getWorld();
        switch (block.getType()) {
            case IRON_DOOR -> {
                if (!doors) return;
                if (!event.getPlayer().hasPermission(openIronDoorsPermission)) return;
                event.setCancelled(true);
                if (open(block, event.getPlayer())) {
                    world.playSound(block.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, SoundCategory.BLOCKS, 1f, 1f);
                } else {
                    world.playSound(block.getLocation(), Sound.BLOCK_IRON_DOOR_CLOSE, SoundCategory.BLOCKS, 1f, 1f);
                }
            }
            case IRON_TRAPDOOR -> {
                if (!trapdoors) return;
                if (!event.getPlayer().hasPermission(openIronTrapdoorsPermission)) return;
                event.setCancelled(true);
                if (open(block, event.getPlayer())) {
                    world.playSound(block.getLocation(), Sound.BLOCK_IRON_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1f, 1f);
                } else {
                    world.playSound(block.getLocation(), Sound.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.BLOCKS, 1f, 1f);
                }
            }
        }
    }

    private boolean open(Block block, Player player) {
        Openable openable = (Openable) block.getBlockData();
        boolean isOpen = openable.isOpen();
        openable.setOpen(!isOpen);
        block.setBlockData(openable);
        player.swingMainHand();
        return !isOpen;
    }


}
