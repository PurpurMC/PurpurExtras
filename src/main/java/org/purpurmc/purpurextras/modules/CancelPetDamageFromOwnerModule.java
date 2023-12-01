package org.purpurmc.purpurextras.modules;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.purpurmc.purpurextras.PurpurExtras;

/**
 * If enabled, pet owners will not be able to harm their own pets.
 */
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
        if (!(pet.getOwner() instanceof OfflinePlayer owner)) return;
        if(damager instanceof Projectile projectile) {
            ProjectileSource shooter = projectile.getShooter();
            if (!(shooter instanceof OfflinePlayer playerShooter)) return;
            if (playerShooter != owner) return;
            damageEvent.setCancelled(true);
            if ((projectile instanceof AbstractArrow && !projectile.getType().equals(EntityType.TRIDENT))){
                projectile.remove();
            }
            return;
        }
        if(!(damager instanceof OfflinePlayer damagingPlayer)) return;
        if(damagingPlayer.getUniqueId() != owner.getUniqueId()) return;
        damageEvent.setCancelled(true);
    }
}
