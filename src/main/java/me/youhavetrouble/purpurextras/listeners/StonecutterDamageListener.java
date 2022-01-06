package me.youhavetrouble.purpurextras.listeners;

import me.youhavetrouble.purpurextras.PurpurExtras;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashSet;

public class StonecutterDamageListener implements Listener {
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onStonecutterDamage(EntityDamageEvent event) {

        if (!event.getCause().equals(EntityDamageEvent.DamageCause.CONTACT)) return;
        if (!event.getEntity().isOnGround()) return;

        HashSet<EntityType> damageBlacklist = PurpurExtras.getPurpurConfig().stonecutterDamageBlacklist;

        if (damageBlacklist.contains(event.getEntity().getType()))
            event.setCancelled(true);

    }
}
