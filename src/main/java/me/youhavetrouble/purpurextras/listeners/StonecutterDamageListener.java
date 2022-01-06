package me.youhavetrouble.purpurextras.listeners;

import me.youhavetrouble.purpurextras.PurpurExtras;
import me.youhavetrouble.purpurextras.config.PurpurConfig;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashMap;

public class StonecutterDamageListener implements Listener {
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onStonecutterDamage(EntityDamageEvent e) {
        // Return if DamageCause is not CONTACT or entity is not on ground
        if (!(e.getCause().equals(EntityDamageEvent.DamageCause.CONTACT)) || !(e.getEntity().isOnGround())) return;

        HashMap<EntityType, Boolean> hashMap = PurpurExtras.getPurpurConfig().stonecutterBlacklist;
        if (hashMap.containsKey(e.getEntity().getType())) {
            e.setCancelled(true);
        }
    }
}
