package org.purpurmc.purpurextras.modules.impl;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;
import org.purpurmc.purpurextras.modules.PurpurExtrasModule;

/**
 * Toggles if entities with jump boost effect will take fall damage
 */
public class NoFallDamageWhileHavingJumpBoostModule implements PurpurExtrasModule {

    @Override
    public boolean shouldEnable() {
        return !getConfigBoolean("settings.gameplay-settings.fall-damage-when-jump-boost-applied", true);
    }

    @Override
    public String getConfigPath() {
        return "settings.gameplay-settings.fall-damage-when-jump-boost-applied";
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onFallDamage(EntityDamageEvent event){
        if (!EntityDamageEvent.DamageCause.FALL.equals(event.getCause())) return;
        if (!(event.getEntity() instanceof LivingEntity livingEntity)) return;
        if (!livingEntity.hasPotionEffect(PotionEffectType.JUMP)) return;
        event.setCancelled(true);
    }
}
