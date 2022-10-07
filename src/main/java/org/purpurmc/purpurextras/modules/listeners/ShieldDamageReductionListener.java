package org.purpurmc.purpurextras.modules.listeners;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class ShieldDamageReductionListener implements Listener {

    private final double shieldDamageReduction;

    public ShieldDamageReductionListener(double reductionModifier) {
        this.shieldDamageReduction = reductionModifier;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onShieldHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof HumanEntity humanEntity)) return;
        if (!humanEntity.isBlocking()) return;
        double reduction = event.getDamage(EntityDamageEvent.DamageModifier.BLOCKING);
        event.setDamage(EntityDamageEvent.DamageModifier.BLOCKING, shieldDamageReduction * reduction);
    }

}
