package org.purpurmc.purpurextras.modules.impl;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;
import org.purpurmc.purpurextras.modules.ModuleInfo;
import org.purpurmc.purpurextras.modules.PurpurExtrasModule;

/**
 * Toggles if entities with jump boost effect will take fall damage
 */
@ModuleInfo(name = "No Fall Jump Boost", description = "Stops fall damage when you have jump boost!")
public class NoFallDamageWhileHavingJumpBoostModule extends PurpurExtrasModule {

    @Override
    public String getConfigPath() {
        return "settings.fall-damage-when-jump-boost-applied";
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onFallDamage(EntityDamageEvent event){
        if (!EntityDamageEvent.DamageCause.FALL.equals(event.getCause())) return;
        if (!(event.getEntity() instanceof LivingEntity livingEntity)) return;
        if (!livingEntity.hasPotionEffect(PotionEffectType.JUMP)) return;
        event.setCancelled(true);
    }
}
