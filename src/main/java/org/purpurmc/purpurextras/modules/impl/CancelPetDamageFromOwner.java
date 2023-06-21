package org.purpurmc.purpurextras.modules.impl;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.purpurmc.purpurextras.modules.ModuleInfo;
import org.purpurmc.purpurextras.modules.PurpurExtrasModule;

/**
 * If enabled, pet owners will not be able to harm their own pets.
 */
@ModuleInfo(name = "Cancel Pet Damage by Owner", description = "You can't damage your pets!")
public class CancelPetDamageFromOwner extends PurpurExtrasModule {

    @Override
    public String getConfigPath() {
        return "settings.cancel-damage-from-pet-owner";
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
            if ((projectile instanceof AbstractArrow && !projectile.getType().equals(EntityType.TRIDENT))){
                projectile.remove();
            }
            return;
        }
        if(!(damager instanceof Player damagingPlayer)) return;
        if(damagingPlayer != owner) return;
        damageEvent.setCancelled(true);
    }
}
