package org.purpurmc.purpurextras.modules.impl;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.purpurmc.purpurextras.modules.ModuleInfo;
import org.purpurmc.purpurextras.modules.PurpurExtrasModule;

/**
 * Allows opening iron doors with a hand, just like wooden doors.
 */
@ModuleInfo(name = "Open Iron Doors", description = "Open Iron Doors with your hand!")
public class OpenIronDoorsWithHand extends PurpurExtrasModule {

    @Override
    public String getConfigPath() {
        return "settings.open-iron-doors-with-hand";
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onIronDoorOpen(PlayerInteractEvent event) {
        if (!EquipmentSlot.HAND.equals(event.getHand())) return;
        if (event.getPlayer().isSneaking()) return;
        if (!event.getAction().isRightClick()) return;
        if (event.getClickedBlock() == null) return;
        Block block = event.getClickedBlock();
        if(block.getType() != Material.IRON_DOOR) return;
        World world = block.getWorld();
        event.setCancelled(true);
        if (open(block, event.getPlayer())) {
            world.playSound(block.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, SoundCategory.BLOCKS, 1f, 1f);
        } else {
            world.playSound(block.getLocation(), Sound.BLOCK_IRON_DOOR_CLOSE, SoundCategory.BLOCKS, 1f, 1f);
        }
    }

    public static boolean open(Block block, Player player) {
        Openable openable = (Openable) block.getBlockData();
        boolean isOpen = openable.isOpen();
        openable.setOpen(!isOpen);
        block.setBlockData(openable);
        player.swingMainHand();
        return !isOpen;
    }
}
