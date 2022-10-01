package org.purpurmc.purpurextras.modules;

import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.purpurmc.purpurextras.PurpurExtras;

public class CancelPetDamageFromOwnerModule implements PurpurExtrasModule, Listener {

    protected CancelPetDamageFromOwnerModule() {}

    @Override
    public void enable() {
        PurpurExtras plugin = PurpurExtras.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        return PurpurExtras.getPurpurConfig().getBoolean("settings.gameplay-settings.cancel-damage-from-pet-owner", false);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPetDamage(EntityDamageByEntityEvent damageEvent){
        Entity damager = damageEvent.getDamager();
        if(!(damageEvent.getEntity() instanceof Tameable pet)) return;
        if(!pet.isTamed()) return;
        Player owner = (Player) pet.getOwner();
        if(damager instanceof Projectile projectile) {
            ProjectileSource shooter = projectile.getShooter();
            if (!(shooter instanceof Player playerShooter)) return;
            if (playerShooter != owner) return;
            damageEvent.setCancelled(true);
            if ((projectile instanceof Arrow) || (projectile instanceof SpectralArrow)){
                projectile.remove();
            }
            return;
        }
        if(!(damager instanceof Player damagingPlayer)) return;
        if(damagingPlayer != owner) return;
        damageEvent.setCancelled(true);
    }
}
