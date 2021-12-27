package me.youhavetrouble.purpurextras.listeners;

import me.youhavetrouble.purpurextras.PurpurExtras;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import java.util.HashSet;

public class AnvilMakesSandListener implements Listener {

    private final HashSet<Material> anvils = new HashSet<>();

    public AnvilMakesSandListener() {
        anvils.add(Material.ANVIL);
        anvils.add(Material.CHIPPED_ANVIL);
        anvils.add(Material.DAMAGED_ANVIL);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onAnvilDrop(EntityChangeBlockEvent event) {

        if (!anvils.contains(event.getTo())) return;

        Location belowAnvil = event.getBlock().getLocation().clone().subtract(0, 1, 0);
        Block blockBelowAnvil = belowAnvil.getBlock();

        if (!PurpurExtras.getPurpurConfig().anvilCrushBlocksIndex.containsKey(blockBelowAnvil.getType())) return;

        blockBelowAnvil.setType(PurpurExtras.getPurpurConfig().anvilCrushBlocksIndex.get(blockBelowAnvil.getType()), true);

    }

}
