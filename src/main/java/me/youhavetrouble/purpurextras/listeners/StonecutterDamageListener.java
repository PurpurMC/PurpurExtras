package me.youhavetrouble.purpurextras.listeners;

import me.youhavetrouble.purpurextras.PurpurExtras;
import me.youhavetrouble.purpurextras.config.PurpurConfig;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class StonecutterDamageListener implements Listener {
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onStonecutterDamage(EntityDamageEvent e) {
        PurpurConfig config = PurpurExtras.getPurpurConfig();
        if ((e.getCause().equals(EntityDamageEvent.DamageCause.CONTACT)) && e.getEntity().isOnGround()
            && !config.getStonecutterDamageFilter(e.getEntity().getType().toString())) {
            Location loc = e.getEntity().getLocation().clone().subtract(0, 0.35, 0);
            Block b = loc.getBlock();
            if (b.getType().equals(Material.STONECUTTER)) {
                e.setCancelled(true);
            }
        }
    }
}
