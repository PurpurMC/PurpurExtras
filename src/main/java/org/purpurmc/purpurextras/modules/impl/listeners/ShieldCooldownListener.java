package org.purpurmc.purpurextras.modules.impl.listeners;

import io.papermc.paper.event.player.PlayerItemCooldownEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ShieldCooldownListener implements Listener {
    private final int shieldCooldown;

    public ShieldCooldownListener(int cooldown) {
        this.shieldCooldown = cooldown;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onShieldHit(PlayerItemCooldownEvent event) {
        if (!event.getType().equals(Material.SHIELD)) return;
        event.setCooldown(shieldCooldown);
    }

}
