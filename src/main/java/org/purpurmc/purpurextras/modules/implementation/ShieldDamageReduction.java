package org.purpurmc.purpurextras.modules.implementation;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.purpurmc.purpurextras.PurpurConfig;
import org.purpurmc.purpurextras.modules.ModuleInfo;
import org.purpurmc.purpurextras.modules.PurpurExtrasModule;

/**
 * Value between 0 and 1. This is the percentage of damage reduction
 * that defending with a shield will provide. By default shield reduces 100% of the damage (1.0).
 */
@ModuleInfo(name = "Shield Damage Reduction", description = "Allows for a custom shield damage reduction value!")
public class ShieldDamageReduction extends PurpurExtrasModule {

    private static final double DEFAULT_SHIELD_DAMAGE_REDUCTION = 1.0;

    private double shieldDamageReduction = DEFAULT_SHIELD_DAMAGE_REDUCTION;

    public ShieldDamageReduction(PurpurConfig config) {
        super(config);
        shieldDamageReduction = getConfigDouble("value", shieldDamageReduction);
    }

    @Override
    public String getConfigPath() {
        return "settings.shield.damage-reduction";
    }

    @Override
    public boolean shouldEnable() {
        return super.shouldEnable() && shieldDamageReduction != DEFAULT_SHIELD_DAMAGE_REDUCTION;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onShieldHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof HumanEntity humanEntity)) return;
        if (!humanEntity.isBlocking()) return;
        double reduction = event.getDamage(EntityDamageEvent.DamageModifier.BLOCKING);
        event.setDamage(EntityDamageEvent.DamageModifier.BLOCKING, shieldDamageReduction * reduction);
    }
}
