package org.purpurmc.purpurextras.modules.impl;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.purpurmc.purpurextras.PurpurConfig;
import org.purpurmc.purpurextras.modules.ModuleInfo;
import org.purpurmc.purpurextras.modules.PurpurExtrasModule;

import static org.purpurmc.purpurextras.modules.impl.OpenIronDoorsWithHand.open;

/**
 * Allows opening iron trapdoors with a hand, just like wooden doors.
 */
@ModuleInfo(name = "Open Iron Trapdoors", description = "Open Iron Trapdoors with your hand!")
public class OpenIronTrapdoorsWithHand extends PurpurExtrasModule {
    public OpenIronTrapdoorsWithHand(PurpurConfig config) {
        super(config);
    }

    @Override
    public String getConfigPath() {
        return "settings.open-iron-trapdoors-with-hand";
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onIronDoorOpen(PlayerInteractEvent event) {
        if (!EquipmentSlot.HAND.equals(event.getHand())) return;
        if (event.getPlayer().isSneaking()) return;
        if (!event.getAction().isRightClick()) return;
        if (event.getClickedBlock() == null) return;
        Block block = event.getClickedBlock();
        if(block.getType() != Material.IRON_TRAPDOOR) return;
        World world = block.getWorld();
        event.setCancelled(true);
        if (open(block, event.getPlayer())) {
            world.playSound(block.getLocation(), Sound.BLOCK_IRON_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1f, 1f);
        } else {
            world.playSound(block.getLocation(), Sound.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.BLOCKS, 1f, 1f);
        }
    }
}
