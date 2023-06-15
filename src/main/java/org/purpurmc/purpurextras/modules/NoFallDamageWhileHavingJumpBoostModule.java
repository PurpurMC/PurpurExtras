package org.purpurmc.purpurextras.modules;

import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;
import org.purpurmc.purpurextras.PurpurExtras;

/**
 * Toggles if entities with jump boost effect will take fall damage
 */
public class NoFallDamageWhileHavingJumpBoostModule implements PurpurExtrasModule, Listener {

    protected NoFallDamageWhileHavingJumpBoostModule() {}

    @Override
    public void enable() {
        PurpurExtras plugin = PurpurExtras.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        return !PurpurExtras.getPurpurConfig().getBoolean("settings.gameplay-settings.fall-damage-when-jump-boost-applied", true);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onFallDamage(EntityDamageEvent event){
        if (!EntityDamageEvent.DamageCause.FALL.equals(event.getCause())) return;
        if (!(event.getEntity() instanceof LivingEntity livingEntity)) return;
        if (!livingEntity.hasPotionEffect(PotionEffectType.JUMP)) return;
        event.setCancelled(true);
    }
}
