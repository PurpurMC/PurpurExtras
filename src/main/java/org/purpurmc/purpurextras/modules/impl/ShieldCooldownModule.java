package org.purpurmc.purpurextras.modules.impl;

import io.papermc.paper.event.player.PlayerItemCooldownEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.purpurmc.purpurextras.modules.ModuleInfo;
import org.purpurmc.purpurextras.modules.PurpurExtrasModule;

/**
 * Amount of ticks (1/20th of a second) of cooldown for a shield after
 * hitting it with an axe crit. By default this is 100 ticks (5 seconds).
 */
@ModuleInfo(name = "Shield Cooldown", description = "Allows for a custom shield cooldown value!")
public class ShieldCooldownModule extends PurpurExtrasModule {
    private static final int DEFAULT_SHIELD_COOLDOWN = 5 * 20;

    private int shieldCooldown = DEFAULT_SHIELD_COOLDOWN;

    public ShieldCooldownModule() {
        shieldCooldown = getConfigInt("value", shieldCooldown);
        shieldCooldown = Math.max(0, shieldCooldown);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onShieldHit(PlayerItemCooldownEvent event) {
        if (!event.getType().equals(Material.SHIELD)) return;
        event.setCooldown(shieldCooldown);
    }

    @Override
    public boolean shouldEnable() {
        return super.shouldEnable() && shieldCooldown != DEFAULT_SHIELD_COOLDOWN;
    }

    @Override
    public String getConfigPath() {
        return "settings.shield.cooldown";
    }

}
